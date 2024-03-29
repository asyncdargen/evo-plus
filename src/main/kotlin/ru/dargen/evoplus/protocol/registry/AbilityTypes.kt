package ru.dargen.evoplus.protocol.registry

import pro.diamondworld.protocol.packet.ability.AbilityTypes
import ru.dargen.evoplus.util.collection.concurrentHashMapOf
import ru.dargen.evoplus.util.minecraft.uncolored

class AbilityType(val data: AbilityTypes.AbilityType) : TypeRegistry.TypeRegistryEntry<String>(data.id) {

    override val holder = AbilityTypeHolder(id)

    val name get() = data.name

    companion object : EnumRegistry<AbilityTypes.AbilityType, AbilityType, AbilityTypes>(
        AbilityTypes::class, AbilityTypes::getTypes, ::AbilityType
    ) {

        private val name2type = concurrentHashMapOf<String, AbilityType>()

        override fun update(received: Map<String, AbilityType>) =
            name2type.putAll(received.values.associateBy { it.name.uncolored().lowercase() })

        fun valueOfName(name: String) = name2type[name.uncolored().lowercase()]

    }

}


class AbilityTypeHolder(key: String) : RegistryHolder<String, AbilityType>(key) {

    override val isPresent get() = AbilityType.containsKey(id)
    override fun get() = AbilityType.valueOf(id)

}