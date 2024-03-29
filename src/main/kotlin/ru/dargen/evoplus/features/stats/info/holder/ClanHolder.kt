package ru.dargen.evoplus.features.stats.info.holder

import ru.dargen.evoplus.protocol.registry.BossType
import ru.dargen.evoplus.protocol.info.InfoCollector
import ru.dargen.evoplus.protocol.info.collect

object ClanHolder : InfoCollector() {

    val Level by collect("level", 0)
    val Members by collect("members", emptyList<String>())
    val Name by collect("name", "")
    var Bosses = emptyList<BossType>()

    init {
        collect("bosses", emptyList<String>()) { bossesId ->
            Bosses = bossesId.mapNotNull { BossType.valueOf(it) }
        }
    }
}