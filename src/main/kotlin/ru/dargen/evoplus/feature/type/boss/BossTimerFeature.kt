package ru.dargen.evoplus.feature.type.boss

import net.minecraft.item.Items
import ru.dargen.evoplus.Executor
import ru.dargen.evoplus.ModLabel
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.api.render.node.tick
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.util.*
import ru.dargen.evoplus.util.selector.enumSelector
import ru.dargen.evoplus.util.selector.toSelector
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds

object BossTimerFeature : Feature("boss-timer", "Таймер боссов", Items.CLOCK) {

    val bosses: MutableMap<BossType, Long> = EnumMap(BossType::class.java)
    val alerted = mutableSetOf<BossType>()
    val timeRegex = "((\\d+)\\s(ч|мин|сек))".toRegex()

    val widget by widgets.add("bosses", "Таймер боссов") {
        +text {
            tick {
                lines = bosses
                    .filterKeys { it.inLevelBounds }
                    .entries
                    .sortedBy { it.value }
                    .take(bossesCount)
                    .map { (type, timestamp) -> "$type§8: §f${(timestamp - System.currentTimeMillis()).format}" }
            }
        }
    }
    val enabled by settings.boolean(
        "enabled",
        "Отображение таймера боссов",
        true
    ) on { widget.enabled = it }
    val minLevel by settings.selector(
        "min-level",
        "Мин. уровень босса",
        enumSelector<BossType>()
    ) { "${it?.level}" }
    val maxLevel by settings.selector(
        "max-level",
        "Макс. уровень босса",
        enumSelector<BossType>(-1)
    ) { "${it?.level}" }
    val bossesCount by settings.selector(
        "render-count",
        "Кол-во отображаемых боссов",
        (0..<BossType.entries.size).toList().toSelector(-1)
    )
    val alertDelay by settings.selector(
        "alert-time",
        "За сколько предупреждать о боссе",
        (0..120 step 5).toList().toSelector()
    ) { "$it сек." }
    val inlineMenuTime by settings.boolean(
        "menu-time",
        "Отображать время до спавна в меню",
        true
    )
    val message by settings.boolean(
        "message",
        "Сообщение о спавне",
        false
    )
    val clanMessage by settings.boolean(
        "clan-messages",
        "Сообщение о спавне в клановый чат",
        false
    )

    val BossType.inLevelBounds get() = level in minLevel.level..maxLevel.level
    private val String.time
        get() = run {
            val timeData = replace(".", "").split("\\s".toRegex())

            if (timeData.size != 2) return@run 0L

            val multi = timeData[0].toLong()
            val defaultValue = when (timeData[1]) {
                "ч" -> 3_600_000L
                "мин" -> 60_000L
                "сек" -> 1000L
                else -> 0L
            }

            multi * defaultValue
        }

    private val String.totalTime
        get() = timeRegex.findAll(this).fold(0L) { acc, match -> acc + match.groupValues[1].time }

    init {
        Executor.scheduleAtFixedRate({
            bosses
                .asSequence()
                .forEach { (type, timestamp) ->
                    val displayName = type.toString()
                    val remainTime = timestamp - System.currentTimeMillis()

                    if (alertDelay > 0 && type !in alerted && remainTime / 1000 <= alertDelay) {
                        printHoveredCommandMessage("Босс §6$displayName §fвозродится через §f$alertDelay секунд.", "§aНажмите, чтобы начать телепортацию", "/boss ${type.level}")
                        alerted.add(type)
                    }

                    if (remainTime <= 0) {
                        bosses.remove(type)
                        alerted.remove(type)
                        when {
                            message -> printHoveredCommandMessage("Босс §6$displayName §fвозродился.", "§aНажмите, чтобы начать телепортацию", "/boss ${type.level}")
                            clanMessage -> sendChatMessage("@${ModLabel}§8: §fБосс $displayName §fвозродился.")
                        }
                    }
                }

            fillBossData()
        }, 50, 50, TimeUnit.MILLISECONDS)
    }
    fun getBossData() = Client?.world
        ?.entities
        ?.map { it.displayName.string.uncolored.replace("۞", "") }
        ?.sortedByDescending { it.startsWith("Босс") }
        ?.mapNotNull {
            when {
                it.startsWith("Босс") -> it.substring(5)
                "сек." in it || "мин." in it || "ч." in it -> it
                else -> null
            }
        }
        ?.run {
            (BossType[getOrNull(0) ?: return@run null] ?: return@run null) to (getOrNull(1) ?: return@run null).totalTime
        }

    fun fillBossData() {
        val (bossType, additionTime) = getBossData() ?: return

        bosses[bossType] = System.currentTimeMillis() + additionTime
    }

}

private val Long.format
    get() = if (this < 1000L) "сейчас"
    else milliseconds.toComponents { hours, minutes, seconds, _ ->
        "${if (hours > 0) "$hours ч. " else ""}${if (minutes > 0) "$minutes мин. " else ""}${if (seconds > 0) "$seconds сек." else ""}"
    }
