package ru.dargen.evoplus.mixins;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.dargen.evoplus.EvoPlus;
import ru.dargen.evoplus.event.entity.AttackEntityEvent;
import ru.dargen.evoplus.event.interact.BlockBreakEvent;
import ru.dargen.evoplus.event.interact.InteractItemEvent;
import ru.dargen.evoplus.util.Util;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Inject(at = @At("HEAD"), method = "attackEntity", cancellable = true)
    public void attackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (EvoPlus.instance().getEventBus().fireEvent(new AttackEntityEvent(target)).isCancelled())
            ci.cancel();
    }

    @Inject(at = @At("HEAD"), method = "interactItem", cancellable = true)
    public void interactItem(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (EvoPlus.instance().getEventBus().fireEvent(new InteractItemEvent(player.world, player.getStackInHand(hand))).isCancelled())
            cir.setReturnValue(ActionResult.SUCCESS);
    }

    @Inject(at = @At("HEAD"), method = "interactBlock", cancellable = true)
    public void interactBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (EvoPlus.instance().getEventBus().fireEvent(new InteractItemEvent(player.world, player.getStackInHand(hand))).isCancelled())
            cir.setReturnValue(ActionResult.SUCCESS);
    }

    @Inject(at = @At("HEAD"), method = "breakBlock", cancellable = true)
    public void breakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        EvoPlus.instance().getEventBus().fireEvent(new BlockBreakEvent(pos, Util.getWorld().getBlockState(pos)));
    }
}
