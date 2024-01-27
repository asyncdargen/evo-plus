package ru.dargen.evoplus.protocol.registry

import pro.diamondworld.protocol.packet.boss.BossTypes
import ru.dargen.evoplus.util.adapter
import ru.dargen.evoplus.util.collection.concurrentHashMapOf
import ru.dargen.evoplus.util.gson
import ru.dargen.evoplus.util.minecraft.customItem
import ru.dargen.evoplus.util.minecraft.itemOf
import ru.dargen.evoplus.util.minecraft.uncolored


data class BossType(val data: BossTypes.BossType) : TypeRegistry.TypeRegistryEntry<String>(data.id),
    Comparable<BossType> {

    override val link = BossLink(id)

    val level get() = data.level
    val name get() = data.name

    val displayLevel = "§8[§6$level§8]"
    val displayName = "§6$name $displayLevel"

    val displayItem = customItem(itemOf(data.material), data.customModelData)

    override fun compareTo(other: BossType) = compareValues(level, other.level)

    companion object : EnumRegistry<BossTypes.BossType, BossType, BossTypes>(
        BossTypes::class, BossTypes::getTypes, ::BossType
    ) {

        init {
            gson {
                adapter<BossLink>(
                    BossLink::class,
                    { link, ctx -> ctx.serialize(link.id) },
                    { element, ctx -> BossLink(element.asString) }
                )
            }
        }

        private val MedalPattern = "\\s([\uE124\uE125\uE126])(\\sx\\d+|)".toRegex()
        private val name2type = concurrentHashMapOf<String, BossType>()
        override fun update(received: Map<String, BossType>) =
            name2type.putAll(received.values.associateBy { it.name.lowercase() })

        fun valueOfName(name: String) = name2type[name.uncolored().lowercase().replace(MedalPattern, "")]

    }

}


class BossLink(key: String) : RegistryLink<String, BossType>(key), Comparable<BossLink> {

    override val isPresent get() = BossType.containsKey(id)
    override val ref get() = BossType.valueOf(id)
    override fun compareTo(other: BossLink) = compareValues(ref?.level, other.ref?.level)

}