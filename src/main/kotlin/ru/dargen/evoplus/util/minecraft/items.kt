package ru.dargen.evoplus.util.minecraft

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.text.Text
import java.lang.reflect.Constructor


fun itemStack(type: Item, block: ItemStack.() -> Unit = {}) = ItemStack(type).apply(block)

fun customItem(type: Item, customModelData: Int, block: ItemStack.() -> Unit = {}) =
    itemStack(type) { editNBT { putInt("CustomModelData", customModelData) } }.apply(block)

fun ItemStack.editNBT(block: NbtCompound.() -> Unit) = apply {
    nbt = orCreateNbt.apply(block)
}

var ItemStack.displayName: String?
    get() = name?.string
    set(value) {
        setCustomName(value?.asText ?: Text.empty())
    }

var ItemStack.lore: MutableList<Text>
    get() = getSubNbt("display")
        ?.getList("Lore", 8)
        ?.map(NbtElement::asString)
        ?.mapNotNull(Text.Serializer::fromJson)
        ?.toMutableList() ?: mutableListOf()
    set(value) {
        getOrCreateSubNbt("display").put(
            "Lore",
            ListTagConstructor.newInstance(value.map(Text.Serializer::toJson).map(NbtString::of), 8.toByte())
        )
    }

@Suppress("unchecked_cast")
private val ListTagConstructor
    get() = NbtList::class.java.declaredConstructors
        .find { it.parameterCount == 2 }!!
        .apply { isAccessible = true } as Constructor<NbtList>