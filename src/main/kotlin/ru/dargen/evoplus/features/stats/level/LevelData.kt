package ru.dargen.evoplus.features.stats.level

import pro.diamondworld.protocol.packet.game.LevelInfo

data class LevelData(var money: Double = .0, var blocks: Int = 0, var isMaxLevel: Boolean = true) {

    fun fetch(info: LevelInfo) {
        money = info.requiredMoney
        blocks = info.requiredBlocks
        isMaxLevel = info.isMaxLevel
    }

}
