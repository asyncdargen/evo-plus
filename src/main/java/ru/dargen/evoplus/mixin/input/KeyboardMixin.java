package ru.dargen.evoplus.mixin.input;

import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.api.event.EventBus;
import ru.dargen.evoplus.api.event.input.KeyCharEvent;
import ru.dargen.evoplus.api.event.input.KeyEvent;
import ru.dargen.evoplus.api.event.input.KeyReleaseEvent;
import ru.dargen.evoplus.api.event.input.KeyTypeEvent;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "onKey", at = @At("RETURN"))
    private void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        boolean state = action > 0;

        EventBus.INSTANCE.fire(new KeyEvent(key, state));
        EventBus.INSTANCE.fire(state ? new KeyTypeEvent(key) : new KeyReleaseEvent(key));
    }

    @Inject(method = "onChar", at = @At("RETURN"))
    private void onChar(long window, int code, int modifiers, CallbackInfo ci) {
        if (Character.charCount(code) == 1) {
            fireCharEvent(code, (char) code);
        } else {
            for (char character : Character.toChars(code)) {
                fireCharEvent(code, character);
            }
        }

    }

    @Unique
    private static void fireCharEvent(int code, char character) {
        EventBus.INSTANCE.fire(new KeyCharEvent(character, code));
    }

}
