package ru.dargen.evoplus.mixin.input.keybind;

import lombok.val;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Type;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.api.render.context.ScreenContext;
import ru.dargen.evoplus.util.minecraft.MinecraftKt;

import java.util.*;

import static net.minecraft.client.util.InputUtil.isKeyPressed;

@Mixin(KeyBinding.class)
public class KeyBindingMixin {

    @Shadow
    @Final
    private static Map<String, KeyBinding> KEYS_BY_ID;
    @Shadow
    @Final
    private static Map<InputUtil.Key, KeyBinding> KEY_TO_BINDINGS;
    @Shadow private boolean pressed;
    @Unique
    private static final Map<InputUtil.Key, List<KeyBinding>> KEY_BINDINGS = new HashMap();

    private static void putKey(KeyBinding keyBinding) {
        KEY_BINDINGS.computeIfAbsent(((KeyBindingAccessor) keyBinding).getBoundKey(), any -> new ArrayList<>()).add(keyBinding);
    }

    @Inject(method = "<init>(Ljava/lang/String;Lnet/minecraft/client/util/InputUtil$Type;ILjava/lang/String;)V", at = @At("TAIL"))
    private void init(String translationKey, Type type, int code, String category, CallbackInfo ci) {
        putKey((KeyBinding) (Object) this);
    }

    @Inject(method = "setKeyPressed", at = @At("TAIL"))
    private static void setKeyPressed(InputUtil.Key key, boolean pressed, CallbackInfo ci) {
        KEY_BINDINGS.getOrDefault(key, Collections.emptyList()).forEach(bind -> bind.setPressed(pressed));
    }

    @Inject(method = "onKeyPressed", at = @At("TAIL"))
    private static void onKeyPressed(InputUtil.Key key, CallbackInfo ci) {
        KEY_BINDINGS.getOrDefault(key, Collections.emptyList()).forEach(bind -> {
            if (KEY_TO_BINDINGS.get(key) == bind) return;

            var accessor = ((KeyBindingAccessor) bind);
            accessor.setTimesPressed(accessor.getTimesPressed() + 1);
        });
    }

    @Inject(method = "updateKeysByCode", at = @At("TAIL"))
    private static void updateKeysByCode(CallbackInfo ci) {
        KEY_BINDINGS.clear();
        for (KeyBinding keyBinding : KEYS_BY_ID.values()) {
            KEY_BINDINGS.computeIfAbsent(((KeyBindingAccessor) keyBinding).getBoundKey(), any -> new ArrayList<>()).add(keyBinding);
        }
    }

    @Inject(method = "updatePressedStates", at = @At("TAIL"))
    private static void updatePressedStates(CallbackInfo ci) {
        for (KeyBinding keyBinding : KEYS_BY_ID.values()) {
            var accessor = (KeyBindingAccessor) keyBinding;
            if (accessor.getBoundKey().getCategory() != Type.KEYSYM || accessor.getBoundKey().getCode() == InputUtil.UNKNOWN_KEY.getCode())
                continue;
            keyBinding.setPressed(isKeyPressed(MinecraftKt.getWindow().getHandle(), accessor.getBoundKey().getCode()));
        }
    }

    @Inject(method = "unpressAll", at = @At("HEAD"), cancellable = true)
    private static void unpressAll(CallbackInfo ci) {
        val screen = ScreenContext.Companion.current();
        if (screen != null && screen.isPassEvents()) {
            ci.cancel();
        } else {
            for (KeyBinding keyBinding : KEYS_BY_ID.values()) {
                var accessor = (KeyBindingAccessor) keyBinding;
                accessor.setTimesPressed(0);
                keyBinding.setPressed(false);
            }
        }
    }

}
