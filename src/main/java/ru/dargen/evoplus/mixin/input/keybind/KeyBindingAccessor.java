package ru.dargen.evoplus.mixin.input.keybind;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(KeyBinding.class)
public interface KeyBindingAccessor {

    @Accessor("CATEGORY_ORDER_MAP")
    static Map<String, Integer> categoryMap() {
        throw new AssertionError();
    }

    @Accessor("boundKey")
    InputUtil.Key getBoundKey();

    @Accessor("timesPressed")
    int getTimesPressed();

    @Accessor("timesPressed")
    void setTimesPressed(int times);

}
