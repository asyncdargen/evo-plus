package ru.dargen.evoplus.features.stats.info

import ru.dargen.evoplus.protocol.registry.BossType
import ru.dargen.evoplus.util.json.deserializer
import ru.dargen.evoplus.util.json.gson

class GameLocation(val id: String) {

    val isShaft get() = id.startsWith("shaft")
    val shaftLevel get() = if (isShaft) id.substring(6).toInt() else 0

    val isBoss get() = id.startsWith("boss")
    val bossType get() = if (isBoss) BossType.valueOf(id.substring(5)) else null

    val isWarp get() = !isShaft && !isBoss
    val warp get() = if (isWarp) id else null

    companion object {
        init {
            gson { deserializer<GameLocation> { element, ctx -> GameLocation(element.asString) } }
        }
    }

}