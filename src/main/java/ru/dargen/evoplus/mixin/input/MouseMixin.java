package ru.dargen.evoplus.mixin.input;

import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.api.event.EventBus;
import ru.dargen.evoplus.api.event.input.MouseClickEvent;
import ru.dargen.evoplus.api.event.input.MouseMoveEvent;
import ru.dargen.evoplus.api.event.input.MouseWheelEvent;
import ru.dargen.evoplus.util.MinecraftKt;

@Mixin(Mouse.class)
public class MouseMixin {

    @Shadow
    private double x;

    @Shadow
    private double y;

    @Inject(method = "onMouseButton", at = @At("RETURN"))
    private void onButton(long window, int button, int action, int mods, CallbackInfo ci) {
        EventBus.INSTANCE.fire(new MouseClickEvent(MinecraftKt.getMousePosition(), button, action == 1));
    }

    @Inject(method = "onMouseScroll", at = @At("RETURN"))
    private void onScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        EventBus.INSTANCE.fire(new MouseWheelEvent(MinecraftKt.getMousePosition(), horizontal, vertical));
    }

    @Inject(method = "onCursorPos", at = @At("RETURN"))
    private void onMove(long window, double x, double y, CallbackInfo ci) {
        EventBus.INSTANCE.fire(new MouseMoveEvent(
                MinecraftKt.getMousePosition()
//                        .div(this.x, this.y, 1.0)
//                        .times(x, y, .0)
//                        .fixNaN()
        ));
    }

}
