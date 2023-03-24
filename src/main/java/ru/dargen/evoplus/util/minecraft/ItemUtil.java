package ru.dargen.evoplus.util.minecraft;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.Text;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@UtilityClass
public class ItemUtil {

    public String getDisplayName(ItemStack itemStack) {
        if (itemStack == null) return null;

        val text = itemStack.getName();

        return text == null ? null : text.getString();
    }

    public List<String> getStringLore(ItemStack itemStack) {
        return getTextLore(itemStack).stream().map(Text::getString).collect(Collectors.toList());
    }

    public List<Text> getTextLore(ItemStack itemStack) {
        if (itemStack == null) return Collections.emptyList();
        val tag = itemStack.getOrCreateSubTag("display");
        val loreTag = tag.getList("Lore", 8);
        if (loreTag == null) return Collections.emptyList();
        return loreTag.stream()
                .map(Tag::asString)
                .map(Text.Serializer::fromJson)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private final Constructor<ListTag> LIST_TAG_CONSTRUCTOR = getListTagConstructor();

    @SneakyThrows
    private Constructor<ListTag> getListTagConstructor() {
        val constructor = Arrays.stream(ListTag.class.getDeclaredConstructors())
                .filter(c -> c.getParameterCount() == 2)
                .findFirst()
                .get();
        constructor.setAccessible(true);
        return (Constructor<ListTag>) constructor;
    }

    @SneakyThrows
    public void setStringLore(ItemStack itemStack, List<String> lore) {
        setTextLore(itemStack, lore.stream().map(Text::of).collect(Collectors.toList()));
    }

    @SneakyThrows
    public void setTextLore(ItemStack itemStack, List<Text> lore) {
        if (itemStack == null || lore == null) return;
        val tag = itemStack.getOrCreateSubTag("display");
        val loreTag = LIST_TAG_CONSTRUCTOR.newInstance(
                lore.stream()
                        .map(Text.Serializer::toJson)
                        .map(StringTag::of)
                        .collect(Collectors.toList()),
                ((byte) 8)
        );
        tag.put("Lore", loreTag);
    }
}
