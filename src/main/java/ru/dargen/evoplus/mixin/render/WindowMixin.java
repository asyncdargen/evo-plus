package ru.dargen.evoplus.mixin.render;

import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.api.event.EventBus;
import ru.dargen.evoplus.api.event.window.WindowRescaleEvent;

@Mixin(Window.class)
public class WindowMixin {

    @Inject(method = "setScaleFactor", at =@At("RETURN"))
    private void setScaleFactor(double scaleFactor, CallbackInfo ci) {
        EventBus.INSTANCE.fire(new WindowRescaleEvent(scaleFactor));
    }

}
