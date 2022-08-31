package ru.dargen.evoplus.feature.setting;

import com.google.gson.JsonElement;
import ru.dargen.evoplus.gui.GuiElement;

public interface Setting<T> {

    String getName();

    String getId();

    T getValue();

    GuiElement getElement();

    void load(JsonElement jsonElement);

    JsonElement unload();

    default boolean isDrawable() {
        return true;
    }

}
