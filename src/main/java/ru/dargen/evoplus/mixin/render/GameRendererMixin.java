package ru.dargen.evoplus.mixin.render;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.feature.type.RenderFeature;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(at = @At("HEAD"),
            method = "tiltViewWhenHurt(Lnet/minecraft/client/util/math/MatrixStack;F)V",
            cancellable = true)
    private void onTiltViewWhenHurt(MatrixStack matrixStack, float f, CallbackInfo ci) {
        if (RenderFeature.INSTANCE.getNoDamageShake()) ci.cancel();
    }

}
