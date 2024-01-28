package ru.dargen.evoplus.features.stats.info

import pro.diamondworld.protocol.packet.game.LevelInfo
import ru.dargen.evoplus.features.stats.level.LevelData

data class StatisticData(
    var level: Int = 1, var nextLevel: LevelData = LevelData(),

    var money: Double = .0, var blocks: Int = 0,

    var shards: Int = 0
) {

    fun fetch(info: LevelInfo) {
        level = info.level
        blocks = info.blocks
        money = info.money

        nextLevel.fetch(info)
    }

}
