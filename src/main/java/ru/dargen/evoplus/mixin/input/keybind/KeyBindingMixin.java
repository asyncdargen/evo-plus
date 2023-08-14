package ru.dargen.evoplus.mixin.input.keybind;

import lombok.val;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.api.render.context.ScreenContext;

@Mixin(KeyBinding.class)
public class KeyBindingMixin {

    @Inject(method = "unpressAll", at = @At("HEAD"), cancellable = true)
    private static void unpressAll(CallbackInfo ci) {
        val screen = ScreenContext.Companion.current();
        if (screen != null && screen.isPassEvents()) {
            ci.cancel();
        }
    }

}
