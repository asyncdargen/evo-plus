package ru.dargen.evoplus.util.minecraft

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.registry.Registries
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.lang.reflect.Constructor

val Item.identifier get() = Registries.ITEM.getId(this).path

fun itemOf(path: String) = Registries.ITEM.get(Identifier.of("minecraft", path.lowercase()))

fun itemStack(type: Item, block: ItemStack.() -> Unit = {}) = ItemStack(type).apply(block)

fun customItem(type: Item, customModelData: Int, block: ItemStack.() -> Unit = {}) =
    itemStack(type) { editNBT { putInt("CustomModelData", customModelData) } }.apply(block)

fun ItemStack.editNBT(block: NbtCompound.() -> Unit) = apply {
    nbt = orCreateNbt.apply(block)
}

var ItemStack.displayName: Text?
    get() = name
    set(value) {
        setCustomName(value ?: Text.empty())
    }

var ItemStack.lore: List<Text>
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