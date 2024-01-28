package ru.dargen.evoplus.protocol.registry

import pro.diamondworld.protocol.packet.boss.BossTypes
import ru.dargen.evoplus.util.collection.concurrentHashMapOf
import ru.dargen.evoplus.util.minecraft.customItem
import ru.dargen.evoplus.util.minecraft.itemOf
import ru.dargen.evoplus.util.minecraft.uncolored


data class BossType(private val data: BossTypes.BossType) : TypeRegistry.TypeRegistryEntry<String>(data.id),
    Comparable<BossType> {

    override val holder = BossHolder(id)

    val level get() = data.level
    val name get() = data.name

    val isRaid get() = data.isRaid
    val capturePoints get() = data.capturePoints

    val displayLevel = "§8[§6$level§8]"
    val displayName = "§6$name $displayLevel"

    val displayItem = customItem(itemOf(data.material), data.customModelData)

    override fun compareTo(other: BossType) = compareValues(level, other.level)

    companion object : EnumRegistry<BossTypes.BossType, BossType, BossTypes>(
        BossTypes::class, BossTypes::getTypes, ::BossType
    ) {

        private val MedalPattern = "\\s([\uE124\uE125\uE126])(\\sx\\d+|)".toRegex()
        private val name2type = concurrentHashMapOf<String, BossType>()

        override fun update(received: Map<String, BossType>) =
            name2type.putAll(received.values.associateBy { it.name.lowercase() })

        fun valueOfName(name: String) = name2type[name.uncolored().lowercase().replace(MedalPattern, "")]

    }

}


class BossHolder(key: String) : RegistryHolder<String, BossType>(key), Comparable<BossHolder> {

    override val isPresent get() = BossType.containsKey(id)
    override fun get() = BossType.valueOf(id)
    override fun compareTo(other: BossHolder) = compareValues(get()?.level, other.get()?.level)

}