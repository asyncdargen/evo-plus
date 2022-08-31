package ru.dargen.evoplus.feature.setting;

import com.google.gson.JsonElement;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.val;
import ru.dargen.evoplus.EvoPlus;
import ru.dargen.evoplus.gui.GuiElement;
import ru.dargen.evoplus.gui.element.GuiRangeElement;

import java.util.List;
import java.util.function.Consumer;

@Data @Builder
public class RangeSetting<T> implements Setting<T> {

    protected final String name;
    protected final String id;
    protected final List<T> elements;
    @Default
    protected int index = 0;
    @Default
    protected Consumer<RangeSetting<T>> changeHandler = null;

    @Override
    public T getValue() {
        return elements.get(fixIndex());
    }

    @Override
    public GuiElement getElement() {
        return new GuiRangeElement<>(elements, 0, 0, 100, 20, element -> {
            index = element.getCurrentPosition();
            fixIndex();
            if (changeHandler != null) changeHandler.accept(this);
        }, fixIndex());
    }

    @Override
    public void load(JsonElement jsonElement) {
        val index = jsonElement.getAsInt();
        val changed = index != this.index;
        this.index = index;
        fixIndex();
        if (changed && changeHandler != null) changeHandler.accept(this);
    }

    @Override
    public JsonElement unload() {
        return EvoPlus.instance().getPrettyGson().toJsonTree(fixIndex());
    }

    protected int fixIndex() {
        if (index < 0) index = 0;
        else if (index >= elements.size()) index = elements.size() - 1;
        return index;
    }

}
