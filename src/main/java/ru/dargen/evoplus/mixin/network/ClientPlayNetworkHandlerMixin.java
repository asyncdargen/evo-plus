package ru.dargen.evoplus.mixin.network;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.val;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.message.LastSeenMessagesCollector;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageChain;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.s2c.play.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.api.event.EventBus;
import ru.dargen.evoplus.api.event.chat.ChatSendEvent;
import ru.dargen.evoplus.api.event.inventory.InventoryCloseEvent;
import ru.dargen.evoplus.api.event.inventory.InventoryFillEvent;
import ru.dargen.evoplus.api.event.inventory.InventoryOpenEvent;
import ru.dargen.evoplus.api.event.inventory.InventorySlotUpdateEvent;
import ru.dargen.evoplus.api.event.network.ChangeServerEvent;
import ru.dargen.evoplus.api.event.network.CustomPayloadEvent;
import ru.dargen.evoplus.api.event.player.PlayerListEvent;
import ru.dargen.evoplus.api.event.world.ChunkLoadEvent;
import ru.dargen.evoplus.features.misc.RenderFeature;
import ru.dargen.evoplus.util.minecraft.Inventories;
import ru.dargen.evoplus.util.minecraft.TextKt;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    private LastSeenMessagesCollector lastSeenMessagesCollector;
    @Shadow
    private MessageChain.Packer messagePacker;

    @Shadow
    public abstract void sendPacket(Packet<?> packet);

    private static final Cache<Integer, InventoryOpenEvent> INVENTORY_OPEN_EVENTS = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build();

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void sendChatMessage(String content, CallbackInfo ci) {
        ci.cancel();
        ChatSendEvent event = new ChatSendEvent(content);
        if (!EventBus.INSTANCE.fireResult(event)) return;

        content = TextKt.composeHex(event.getText());

        Instant instant = Instant.now();
        long l = NetworkEncryptionUtils.SecureRandomUtil.nextLong();
        LastSeenMessagesCollector.LastSeenMessages lastSeenMessages = lastSeenMessagesCollector.collect();
        MessageSignatureData messageSignatureData = messagePacker.pack(new MessageBody(content, instant, l, lastSeenMessages.lastSeen()));
        sendPacket(new ChatMessageC2SPacket(content, instant, l, messageSignatureData, lastSeenMessages.update()));
    }

    @Inject(method = "onEntitySpawn", at = @At("HEAD"), cancellable = true)
    private void onEntitySpawn(EntitySpawnS2CPacket packet, CallbackInfo ci) {
        if (packet.getEntityType() == EntityType.LIGHTNING_BOLT && RenderFeature.INSTANCE.getNoStrikes()) {
            ci.cancel();
        } else if (packet.getEntityType() == EntityType.FALLING_BLOCK && RenderFeature.INSTANCE.getNoFalling()) {
            ci.cancel();
        }
    }

    @Inject(method = "onOpenScreen", at = @At("HEAD"), cancellable = true)
    private void onOpenScreen(OpenScreenS2CPacket packet, CallbackInfo ci) {
        if (packet.getSyncId() == 0) return;
        ci.cancel();

        val event = EventBus.INSTANCE.fire(
                new InventoryOpenEvent(packet.getSyncId(), packet.getScreenHandlerType(), packet.getName(), false)
        );
        INVENTORY_OPEN_EVENTS.put(packet.getSyncId(), event);
        if (!event.isCancelled()) {
            if (!event.isHidden()) {
                NetworkThreadUtils.forceMainThread(((Packet) packet), (PacketListener) this, client);
                HandledScreens.open(event.getScreenHandlerType(), client, event.getSyncId(), event.getTitle());
            }
        } else Inventories.INSTANCE.close(packet.getSyncId());
    }

    @Inject(method = "onInventory", at = @At("HEAD"), cancellable = true)
    private void onInventory(InventoryS2CPacket packet, CallbackInfo ci) {
        if (packet.getSyncId() == 0) return;
        ci.cancel();
        val event = EventBus.INSTANCE.fire(
                new InventoryFillEvent(
                        packet.getSyncId(), packet.getContents(), INVENTORY_OPEN_EVENTS.getIfPresent(packet.getSyncId()),
                        client.player.currentScreenHandler, false
                )
        );
        if (!event.getOpenEvent().isCancelled() || !event.isCancelled()) {
            if (!event.isHidden()) {
                NetworkThreadUtils.forceMainThread((Packet) packet, (PacketListener) this, client);
                PlayerEntity playerEntity = client.player;
                if (packet.getSyncId() == 0) {
                    playerEntity.playerScreenHandler.updateSlotStacks(packet.getRevision(), packet.getContents(), packet.getCursorStack());
                } else if (packet.getSyncId() == playerEntity.currentScreenHandler.syncId) {
                    playerEntity.currentScreenHandler.updateSlotStacks(packet.getRevision(), packet.getContents(), packet.getCursorStack());
                }
            }
        }
    }

    @Inject(method = "onScreenHandlerSlotUpdate", at = @At("HEAD"), cancellable = true)
    private void onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo ci) {
        if (packet.getSyncId() == 0) return;
        ci.cancel();

        val event = EventBus.INSTANCE.fire(
                new InventorySlotUpdateEvent(packet.getSyncId(), packet.getSlot(), packet.getItemStack(),
                        INVENTORY_OPEN_EVENTS.getIfPresent(packet.getSyncId()), false)
        );

        if (!event.isCancelled()) {
            if (!event.isHidden()) {
                NetworkThreadUtils.forceMainThread((Packet) packet, (PacketListener) this, this.client);
                PlayerEntity playerEntity = this.client.player;
                ItemStack itemStack = event.getStack();
                int i = event.getSlot();
                this.client.getTutorialManager().onSlotUpdate(itemStack);
                if (event.getSyncId() == -1) {
                    if (!(this.client.currentScreen instanceof CreativeInventoryScreen)) {
                        playerEntity.currentScreenHandler.setCursorStack(itemStack);
                    }
                } else if (event.getSyncId() == -2) {
                    playerEntity.getInventory().setStack(i, itemStack);
                } else {
                    boolean bl = false;
                    if (this.client.currentScreen instanceof CreativeInventoryScreen) {
                        CreativeInventoryScreen creativeInventoryScreen = (CreativeInventoryScreen) this.client.currentScreen;
                        bl = creativeInventoryScreen.isInventoryTabSelected();
                    }

                    if (event.getSyncId() == 0 && event.getSlot() >= 36 && i < 45) {
                        if (!itemStack.isEmpty()) {
                            ItemStack itemStack2 = playerEntity.playerScreenHandler.getSlot(i).getStack();
                            if (itemStack2.isEmpty() || itemStack2.getCount() < itemStack.getCount()) {
                                playerEntity.getItemCooldownManager()
                                        .set(itemStack.getItem(), 5);
                            }
                        }

                        playerEntity.playerScreenHandler.setStackInSlot(i, i, itemStack);
                    } else if (event.getSyncId() == playerEntity.currentScreenHandler.syncId && (event.getSyncId() != 0 || !bl)) {
                        playerEntity.currentScreenHandler.setStackInSlot(i, i, itemStack);
                    }
                }
            }
        }
    }

    @Inject(method = "onCloseScreen", at = @At("HEAD"), cancellable = true)
    private void onCloseScreen(CloseScreenS2CPacket packet, CallbackInfo ci) {
        if (packet.getSyncId() == 0) return;

        if (!EventBus.INSTANCE.fireResult(
                new InventoryCloseEvent(packet.getSyncId(), INVENTORY_OPEN_EVENTS.getIfPresent(packet.getSyncId())))) {
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "onCustomPayload", cancellable = true)
    public void onCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci) {
        if (packet.getChannel().toString().equals("minecraft:brand")) {
            EventBus.INSTANCE.fire(ChangeServerEvent.INSTANCE);
        }

        if (!EventBus.INSTANCE.fireResult(new CustomPayloadEvent(packet.getChannel().toString(), packet.getData()))) {
            ci.cancel();
        }
    }

    @Inject(method = "onChunkData", at = @At("TAIL"))
    private void onChunkData(ChunkDataS2CPacket packet, CallbackInfo info) {
        val chunk = client.world.getChunk(packet.getX(), packet.getZ());
        EventBus.INSTANCE.fire(new ChunkLoadEvent(chunk));
    }

//    @Inject(method = "onPlayerList", at = @At("TAIL"))
//    private void onPlayerList(PlayerListS2CPacket packet, CallbackInfo ci) {
//        EventBus.INSTANCE.fire(new PlayerListEvent(packet.getActions(), packet.getEntries()));
//    }


}
