package ru.dargen.evoplus.feature.type.staff

import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import ru.dargen.evoplus.util.minecraft.customItem
import ru.dargen.evoplus.util.minecraft.displayName


enum class StaffType(val displayName: String, val id: Int, val item: ItemStack = customItem(Items.WOODEN_HOE, id)) {

    EARTH("Посох Земли", 1),
    FIRE("Посох Огня", 2),
    LIGHTING("Посох Молний", 3),
    MINES("Посох Шахтёра", 4),
    REGENERATION("Посох Регенерации", 5),
    STRENGTH("Посох Силы", 6);

    companion object {

        operator fun get(name: String?) =
            name?.let { entries.firstOrNull { it.displayName.equals(name, true) } }

        operator fun get(itemStack: ItemStack) = if (itemStack.item != Items.WOODEN_HOE) null
        else get(itemStack.displayName?.string)

    }
}

