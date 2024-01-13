package ru.dargen.evoplus.features.stats.combo

import pro.diamondworld.protocol.packet.combo.Combo
import pro.diamondworld.protocol.packet.combo.ComboBlocks
import ru.dargen.evoplus.util.currentMillis

data class ComboData(
    var booster: Double = 1.0, var nextBooster: Double = 1.1,
    var blocks: Int = 0, var requiredBlocks: Int = 1000
) {

    val isMaxed get() = booster == nextBooster

    val isCompleted get() = blocks >= requiredBlocks
    val progress get() = (blocks / requiredBlocks.toDouble()).coerceIn(.0, 1.0)

    var endTimestamp = 0L
    var remain: Long
        set(value) {
            endTimestamp = if (value == 0L) 0 else currentMillis + value * 1000
        }
        get() = if (currentMillis > endTimestamp) 0 else (endTimestamp - currentMillis) / 1000
    val isExpiring get() = remain > 0

    fun fetch(combo: Combo) {
        booster = combo.booster.coerceAtLeast(1.0)
        nextBooster = combo.nextBooster
        blocks = combo.blocks
        requiredBlocks = combo.requiredBlocks
        remain = 0
    }

    fun fetch(blocks: ComboBlocks) {
        this.blocks = blocks.blocks
        remain = 0
    }

}
