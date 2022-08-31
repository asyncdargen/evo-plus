package ru.dargen.evoplus.feature.setting;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonElement;
import io.netty.util.internal.ConcurrentSet;
import lombok.Builder;
import lombok.Data;
import ru.dargen.evoplus.EvoPlus;
import ru.dargen.evoplus.gui.GuiElement;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

@Data @Builder
public class SetConfig<T> implements Setting<Set<T>> {

    protected final Type hashSetType = new TypeToken<HashSet<T>>(getClass()) {}.getType();
    protected final Set<T> value = new ConcurrentSet<>();
    protected final String name;
    protected final String id;

    @Override
    public GuiElement getElement() {
        return null;
    }

    @Override
    public void load(JsonElement jsonElement) {
        Set<T> set = EvoPlus.instance().getGson().fromJson(jsonElement.getAsString(), hashSetType);
        if (set != null) value.addAll(set);
    }

    @Override
    public JsonElement unload() {
        return EvoPlus.instance().getGson().toJsonTree(EvoPlus.instance().getGson().toJson(value));
    }

    @Override
    public boolean isDrawable() {
        return false;
    }

}
