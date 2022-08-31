package ru.dargen.evoplus.feature.setting;

import com.google.gson.JsonElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ru.dargen.evoplus.EvoPlus;
import ru.dargen.evoplus.gui.GuiElement;

import java.util.function.Consumer;

@Builder
@Getter
@AllArgsConstructor
public class StringSetting implements Setting<String> {

    @Builder.Default
    protected String value = "";
    protected final String name;
    protected final String id;
    @Builder.Default
    protected final Consumer<StringSetting> changeHandler = null;

    @Override
    public GuiElement getElement() {
        return null;
    }

    public void setValue(String value) {
        this.value = value;
        if (changeHandler != null) changeHandler.accept(this);
    }

    @Override
    public void load(JsonElement jsonElement) {
        setValue(jsonElement.getAsString());
    }

    @Override
    public JsonElement unload() {
        return EvoPlus.instance().getGson().toJsonTree(value);
    }

    @Override
    public boolean isDrawable() {
        return false;
    }

}
