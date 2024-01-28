package ru.dargen.evoplus.protocol.registry

import net.minecraft.item.Items
import pro.diamondworld.protocol.packet.potion.PotionTypes
import ru.dargen.evoplus.util.minecraft.colored
import ru.dargen.evoplus.util.minecraft.customItem

data class PotionType(val data: PotionTypes.PotionType) : TypeRegistry.TypeRegistryEntry<Int>(data.modelId) {

    override val holder = PotionHolder(id)

    val displayName = data.name.colored()

    val displayItem = customItem(Items.POTION, data.modelId)

    companion object : OrdinalRegistry<PotionTypes.PotionType, PotionType, PotionTypes>(
        PotionTypes::class, PotionTypes::getTypes, ::PotionType
    )

}


class PotionHolder(key: Int) : RegistryHolder<Int, PotionType>(key) {

    override val isPresent get() = PotionType.containsKey(id)
    override fun get() = PotionType.byOrdinal(id)

}