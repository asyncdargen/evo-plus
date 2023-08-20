package ru.dargen.evoplus.mixin;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

//    @Inject(method = "interactItem", at = @At("HEAD"), cancellable = true)
//    public void interactItem(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
//        if (EvoPlus.instance().getEventBus().fireEvent(new InteractItemEvent(player.world, player.getStackInHand(hand))).isCancelled())
//            cir.setReturnValue(ActionResult.SUCCESS);
//    }
//
//    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
//    public void interactBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
//        if (EvoPlus.instance().getEventBus().fireEvent(new InteractItemEvent(player.world, player.getStackInHand(hand))).isCancelled())
//            cir.setReturnValue(ActionResult.SUCCESS);
//    }

}
