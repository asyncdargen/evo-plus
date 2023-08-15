package ru.dargen.evoplus.mixin;

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
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.api.event.EventBus;
import ru.dargen.evoplus.api.event.chat.ChatSendEvent;
import ru.dargen.evoplus.api.event.inventory.InventoryFillEvent;
import ru.dargen.evoplus.api.event.inventory.InventoryOpenEvent;
import ru.dargen.evoplus.api.event.inventory.InventorySlotUpdateEvent;
import ru.dargen.evoplus.feature.type.RenderFeature;
import ru.dargen.evoplus.util.minecraft.Inventories;

import java.util.concurrent.TimeUnit;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Shadow @Final private MinecraftClient client;
    private static final Cache<Integer, InventoryOpenEvent> EVENTS = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build();

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void sendChatMessage(String content, CallbackInfo ci) {
        if (!EventBus.INSTANCE.fireResult(new ChatSendEvent(content))) {
            ci.cancel();
        }
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
        ci.cancel();

        if (packet.getSyncId() == 0) return;
        ci.cancel();
        val event = EventBus.INSTANCE.fire(
                new InventoryOpenEvent(packet.getSyncId(), packet.getScreenHandlerType(), packet.getName(), false)
        );
        EVENTS.put(packet.getSyncId(), event);
        if (!event.isCancelled()) {
            if (!event.isHidden()) {
                NetworkThreadUtils.forceMainThread(((Packet) packet), (PacketListener) this, client);
                HandledScreens.open(event.getScreenHandlerType(), client, event.getSyncId(), event.getName());
            }
        } else Inventories.INSTANCE.close(packet.getSyncId());
    }

    @Inject(method = "onInventory", at = @At("HEAD"), cancellable = true)
    private void onInventory(InventoryS2CPacket packet, CallbackInfo ci) {
        if (packet.getSyncId() == 0) return;
        ci.cancel();
        val event = EventBus.INSTANCE.fire(
                new InventoryFillEvent(
                        packet.getSyncId(), packet.getContents(), EVENTS.getIfPresent(packet.getSyncId()),
                        client.player.currentScreenHandler
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
                        EVENTS.getIfPresent(packet.getSyncId()), false)
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
                        CreativeInventoryScreen creativeInventoryScreen = (CreativeInventoryScreen)this.client.currentScreen;
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



}
