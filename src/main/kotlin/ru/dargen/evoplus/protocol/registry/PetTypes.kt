package ru.dargen.evoplus.protocol.registry

import pro.diamondworld.protocol.packet.pet.PetTypes
import ru.dargen.evoplus.util.collection.concurrentHashMapOf
import ru.dargen.evoplus.util.minecraft.colored
import ru.dargen.evoplus.util.minecraft.customItem
import ru.dargen.evoplus.util.minecraft.itemOf
import ru.dargen.evoplus.util.minecraft.uncolored

data class PetType(val data: PetTypes.PetType) : TypeRegistry.TypeRegistryEntry<String>(data.id) {

    override val link = PetLink(id)

    val name get() = data.name

    val displayName = data.name.colored()
    val displayItem = customItem(itemOf(data.material), data.customModelData)

    companion object : EnumRegistry<PetTypes.PetType, PetType, PetTypes>(
        PetTypes::class, PetTypes::getTypes, ::PetType
    ) {

        private val name2type = concurrentHashMapOf<String, PetType>()
        override fun update(received: Map<String, PetType>) =
            name2type.putAll(received.values.associateBy { it.name.uncolored().lowercase() })

        fun valueOfName(name: String) = name2type[name.uncolored().lowercase()]

    }

}


class PetLink(key: String) : RegistryLink<String, PetType>(key) {

    override val isPresent get() = PetType.containsKey(id)
    override val ref get() = PetType.valueOf(id)

}