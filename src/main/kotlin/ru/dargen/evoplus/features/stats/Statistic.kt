package ru.dargen.evoplus.features.stats

import pro.diamondworld.protocol.packet.game.LevelInfo
import ru.dargen.evoplus.features.stats.level.LevelData

data class Statistic(
    var level: Int = 1,
    var money: Double = .0, var blocks: Int = 0,
    var nextLevel: LevelData = LevelData()
) {

    fun fetch(info: LevelInfo) {
        level = info.level
        blocks = info.blocks
        money = info.money

        nextLevel.fetch(info)
    }

}
