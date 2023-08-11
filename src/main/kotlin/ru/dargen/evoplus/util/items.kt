package ru.dargen.evoplus.util

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound


fun itemStack(type: Item, block: ItemStack.() -> Unit = {}) = ItemStack(type).apply(block)

fun customItem(type: Item, customModelData: Int, block: ItemStack.() -> Unit = {}) =
    itemStack(type) { editNBT { putInt("CustomModelData", customModelData) } }.apply(block)

fun ItemStack.editNBT(block: NbtCompound.() -> Unit) = apply {
    nbt = orCreateNbt.apply(block)
}

val ItemStack.displayName: String?
    get() =name?.string