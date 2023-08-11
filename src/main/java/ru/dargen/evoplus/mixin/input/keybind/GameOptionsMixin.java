package ru.dargen.evoplus.mixin.input.keybind;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.api.keybind.KeyBindings;

@Mixin(GameOptions.class)
public class GameOptionsMixin {

    @Mutable
    @Final
    @Shadow
    public KeyBinding[] allKeys;

    @Inject(at = @At("HEAD"), method = "load()V")
    public void load(CallbackInfo info) {
        allKeys = KeyBindings.INSTANCE.interceptKeys(allKeys);
    }

}
