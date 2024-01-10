package ru.dargen.evoplus.mixin.network;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.dargen.evoplus.api.event.EventBus;
import ru.dargen.evoplus.api.event.interact.AttackEvent;
import ru.dargen.evoplus.api.event.interact.BlockBreakEvent;
import ru.dargen.evoplus.api.event.inventory.InventoryClickEvent;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Inject(method = "clickSlot", at = @At("HEAD"), cancellable = true)
    public void clickSlot(int syncId, int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (!EventBus.INSTANCE.fireResult(new InventoryClickEvent(syncId, slotId, button, actionType))) {
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "breakBlock", cancellable = true)
    public void breakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!EventBus.INSTANCE.fireResult(new BlockBreakEvent(pos))) {
            cir.setReturnValue(false);
        }
    }

    @Inject(at = @At("HEAD"), method = "attackEntity", cancellable = true)
    public void attackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (!EventBus.INSTANCE.fireResult(new AttackEvent(target)))
            ci.cancel();
    }

}
