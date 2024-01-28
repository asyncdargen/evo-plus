package ru.dargen.evoplus.features.stats.info

import pro.diamondworld.protocol.packet.game.GameEvent.EventType
import ru.dargen.evoplus.features.stats.combo.ComboData
import ru.dargen.evoplus.protocol.info.InfoCollector
import ru.dargen.evoplus.protocol.info.collect

object StatisticHolder : InfoCollector() {

    val Location by collect("gameLocation", GameLocation("spawn"))
    val ActivePets by collect<List<PetData>>("pets", emptyList())

    val Data = StatisticData()
    val Combo = ComboData()

    var Event = EventType.NONE

    init {
        collect<Int>("level") { Data.level = it }
        collect<Int>("blocks") { Data.blocks = it }
        collect<Double>("balance") { Data.money = it }
        collect<Int>("shards") { Data.shards = it }
    }

}