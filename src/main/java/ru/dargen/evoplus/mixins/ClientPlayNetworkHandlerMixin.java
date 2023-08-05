package ru.dargen.evoplus.mixins;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.val;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.EvoPlus;
import ru.dargen.evoplus.event.entity.EntityDeathEvent;
import ru.dargen.evoplus.event.inventory.InventoryFillEvent;
import ru.dargen.evoplus.event.inventory.InventoryOpenEvent;
import ru.dargen.evoplus.event.inventory.InventorySlotUpdateEvent;
import ru.dargen.evoplus.event.server.ChangeServerEvent;
import ru.dargen.evoplus.util.Util;
import ru.dargen.evoplus.util.minecraft.InventoryUtil;

import java.util.concurrent.TimeUnit;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    private static final Cache<Integer, InventoryOpenEvent> EVENTS = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build();

    @Final
    @Shadow
    private MinecraftClient client;

    @Inject(at = @At("HEAD"), method = "onCustomPayload")
    public void onCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci) {
        if (packet.getChannel().toString().equals("minecraft:brand"))
            EvoPlus.instance().getEventBus().fireEvent(new ChangeServerEvent());
    }

    @Inject(at = @At("HEAD"), method = "onOpenScreen", cancellable = true)
    public void onOpenScreen(OpenScreenS2CPacket packet, CallbackInfo ci) {
        if (packet.getSyncId() == 0) return;
        ci.cancel();
        val event = EvoPlus.instance().getEventBus().fireEvent(
                new InventoryOpenEvent(packet.getSyncId(), packet.getScreenHandlerType(), packet.getName(), false)
        );
        EVENTS.put(packet.getSyncId(), event);
        if (!event.isCancelled()) {
            if (!event.isHidden()) {
                NetworkThreadUtils.forceMainThread(((Packet) packet), (PacketListener) this, client);
                HandledScreens.open(event.getScreenHandlerType(), client, event.getSyncId(), event.getName());
            }
        } else InventoryUtil.closeInventory(packet.getSyncId());
    }

    @Inject(at = @At("HEAD"), method = "onScreenHandlerSlotUpdate", cancellable = true)
    public void onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo ci) {
        if (packet.getSyncId() == 0) return;
        ci.cancel();
        val event = EvoPlus.instance().getEventBus().fireEvent(
                new InventorySlotUpdateEvent(packet.getSyncId(), packet.getSlot(), packet.getItemStack(), EVENTS.getIfPresent(packet.getSyncId()), false)
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

    @Inject(at = @At("HEAD"), method = "onInventory", cancellable = true)
    public void onInventory(InventoryS2CPacket packet, CallbackInfo ci) {
        if (packet.getSyncId() == 0) return;
        ci.cancel();
        val event = EvoPlus.instance().getEventBus().fireEvent(
                new InventoryFillEvent(
                        packet.getSyncId(), packet.getContents(), EVENTS.getIfPresent(packet.getSyncId()),
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

    @Inject(at = @At("HEAD"), method = "onEntityStatus")
    public void onEntityStatus(EntityStatusS2CPacket packet, CallbackInfo ci) {
        Entity entity;
        if (packet.getStatus() == 3 && (entity = packet.getEntity(Util.getWorld())) != null)
            EvoPlus.instance().getEventBus().fireEvent(new EntityDeathEvent(entity));
    }

}
