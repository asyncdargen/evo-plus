package ru.dargen.evoplus.feature.setting;

import com.google.gson.JsonElement;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.val;
import ru.dargen.evoplus.EvoPlus;
import ru.dargen.evoplus.gui.GuiElement;
import ru.dargen.evoplus.gui.element.GuiButtonElement;

import java.util.function.Consumer;

@Data @Builder
public class BooleanSetting implements Setting<Boolean> {

    protected final String name;
    protected final String id;
    @Default
    protected Boolean value = true;
    @Default
    protected Consumer<BooleanSetting> changeHandler = null;

    @Override
    public GuiElement getElement() {
        return new GuiButtonElement(toString(), 0, 0, 100, 20, element -> {
            value = !value;
            element.setText(toString());
            if (changeHandler != null) changeHandler.accept(this);
        });
    }

    public String toString() {
        return value ? "§aВключено" : "§cВыключено";
    }

    @Override
    public void load(JsonElement jsonElement) {
        val value = jsonElement.getAsBoolean();
        val changed = value != this.value;
        this.value = value;
        if (changed && changeHandler != null) changeHandler.accept(this);
    }

    @Override
    public JsonElement unload() {
        return EvoPlus.instance().getPrettyGson().toJsonTree(value);
    }

}
