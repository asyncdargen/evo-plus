package ru.dargen.evoplus.protocol.registry

import pro.diamondworld.protocol.packet.fishing.FishingSpots
import ru.dargen.evoplus.util.collection.concurrentHashMapOf
import ru.dargen.evoplus.util.minecraft.uncolored

class FishingSpot(val data: FishingSpots.FishingSpot) : TypeRegistry.TypeRegistryEntry<String>(data.id) {

    override val holder = FishingSpotHolder(id)

    val name get() = data.name

    companion object : EnumRegistry<FishingSpots.FishingSpot, FishingSpot, FishingSpots>(
        FishingSpots::class, FishingSpots::getSpots, ::FishingSpot
    ) {

        private val name2type = concurrentHashMapOf<String, FishingSpot>()

        override fun update(received: Map<String, FishingSpot>) =
            name2type.putAll(received.values.associateBy { it.name.uncolored().lowercase() })

        fun valueOfName(name: String) = name2type[name.uncolored().lowercase()]

    }

}


class FishingSpotHolder(key: String) : RegistryHolder<String, FishingSpot>(key) {

    override val isPresent get() = FishingSpot.containsKey(id)
    override fun get() = FishingSpot.valueOf(id)

}