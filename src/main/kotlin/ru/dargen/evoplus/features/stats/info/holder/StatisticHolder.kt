package ru.dargen.evoplus.features.stats.info.holder

import pro.diamondworld.protocol.packet.game.GameEvent.EventType
import ru.dargen.evoplus.api.event.evo.ChangeLocationEvent
import ru.dargen.evoplus.api.event.fire
import ru.dargen.evoplus.features.stats.combo.ComboData
import ru.dargen.evoplus.features.stats.info.GameLocation
import ru.dargen.evoplus.features.stats.info.PetData
import ru.dargen.evoplus.features.stats.info.StatisticData
import ru.dargen.evoplus.protocol.info.InfoCollector
import ru.dargen.evoplus.protocol.info.collect

object StatisticHolder : InfoCollector() {

    val Location by collect("gameLocation", GameLocation("spawn")) {
        ChangeLocationEvent.fire()
    }
    val ActivePets by collect("pets", emptyList<PetData>())

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