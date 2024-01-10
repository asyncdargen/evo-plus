package ru.dargen.evoplus.mixin.render;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.api.event.EventBus;
import ru.dargen.evoplus.api.event.render.ScreenRenderEvent;
import ru.dargen.evoplus.features.game.RenderFeature;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "tiltViewWhenHurt(Lnet/minecraft/client/util/math/MatrixStack;F)V",
            at = @At("HEAD"),
            cancellable = true)
    private void onTiltViewWhenHurt(MatrixStack matrixStack, float f, CallbackInfo ci) {
        if (RenderFeature.INSTANCE.getNoDamageShake()) ci.cancel();
    }

    @Redirect(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderWithTooltip(Lnet/minecraft/client/util/math/MatrixStack;IIF)V"))
    private void render(Screen instance, MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (EventBus.INSTANCE.fireResult(new ScreenRenderEvent.Pre(instance, matrices, delta))) {
            instance.renderWithTooltip(matrices, mouseX, mouseY, delta);
            EventBus.INSTANCE.fire(new ScreenRenderEvent.Post(instance, matrices, delta));
        }
    }

}
