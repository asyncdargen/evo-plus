package ru.dargen.evoplus.feature.type.boss


enum class BossRenderOrder(val displayName: String, val timesComparator: Comparator<BossTimerEntry>) {

    TIME("По времени", Comparator.comparingLong { it.type.cooldown - (System.currentTimeMillis() - it.timestamp) }),
    LEVEL("По уровню", Comparator.comparing { it.type.level });

    override fun toString() = displayName

}

