package ru.dargen.evoplus.protocol.registry

import pro.diamondworld.protocol.packet.fishing.quest.HourlyQuestTypes
import ru.dargen.evoplus.util.collection.concurrentHashMapOf
import ru.dargen.evoplus.util.minecraft.uncolored

class HourlyQuestType(val data: HourlyQuestTypes.HourlyQuestType) : TypeRegistry.TypeRegistryEntry<Int>(data.id) {

    override val holder = HourlyQuestHolder(id)

    val type get() = data.type
    val name get() = data.name
    val lore get() = data.lore
    val needed get() = data.needed

    companion object : OrdinalRegistry<HourlyQuestTypes.HourlyQuestType, HourlyQuestType, HourlyQuestTypes>(
        HourlyQuestTypes::class, HourlyQuestTypes::getTypes, ::HourlyQuestType
    ) {

        private val name2type = concurrentHashMapOf<String, HourlyQuestType>()

        override fun update(received: Map<Int, HourlyQuestType>) =
            name2type.putAll(received.values.associateBy { it.name.uncolored().lowercase() })

        fun valueOfName(name: String) = name2type[name.uncolored().lowercase()]

    }

}


class HourlyQuestHolder(key: Int) : RegistryHolder<Int, HourlyQuestType>(key) {

    override val isPresent get() = HourlyQuestType.containsKey(id)
    override fun get() = HourlyQuestType.byOrdinal(id)

}