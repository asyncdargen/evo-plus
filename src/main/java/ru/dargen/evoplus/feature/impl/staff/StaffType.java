package ru.dargen.evoplus.feature.impl.staff;

import lombok.Getter;
import lombok.val;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import ru.dargen.evoplus.util.Util;
import ru.dargen.evoplus.util.minecraft.ItemUtil;

@Getter
public enum StaffType {

    EARTH("Посох Земли", 1),
    FIRE("Посох Огня", 2),
    LIGHTING("Посох Молний", 3),
    MINES("Посох Шахтёра", 4),
    REGENERATION("Посох Регенерации", 5),
    STRENGTH("Посох Силы", 6),
    ;

    private final String name;
    private final int modelId;
    private final ItemStack renderItem;

    StaffType(String name, int modelId) {
        this.name = name;
        this.modelId = modelId;
        renderItem = Items.WOODEN_HOE.getDefaultStack().copy();
        val tag = renderItem.getOrCreateTag();
        tag.putInt("CustomModelData", modelId);
        renderItem.setTag(tag);
    }

    public static StaffType getByName(String name) {
        name = Util.stripColor(name);
        for (StaffType type : values())
            if (type.name.equalsIgnoreCase(name))
                return type;
        return null;
    }

    public static StaffType getByStack(ItemStack itemStack) {
        if (itemStack.getItem() != Items.WOODEN_HOE)
            return null;

        return getByName(ItemUtil.getDisplayName(itemStack));
    }

}
