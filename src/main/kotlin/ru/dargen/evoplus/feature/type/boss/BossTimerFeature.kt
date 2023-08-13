package ru.dargen.evoplus.feature.type.boss

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import net.minecraft.item.Items
import ru.dargen.evoplus.ModLabel
import ru.dargen.evoplus.api.render.node.leftClick
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.feature.misc.Notifies
import ru.dargen.evoplus.util.*
import ru.dargen.evoplus.util.concurrent.every
import ru.dargen.evoplus.util.format.asTextTime
import ru.dargen.evoplus.util.format.fromTextTime
import ru.dargen.evoplus.util.selector.enumSelector
import ru.dargen.evoplus.util.selector.toSelector
import kotlin.math.abs

object BossTimerFeature : Feature("boss-timer", "Таймер боссов", Items.CLOCK) {

    val Bosses: MutableMap<BossType, Long> by config("bosses", mutableMapOf())
    private val Alerted = mutableSetOf<BossType>()

    private val BossesText = text()
    val Widget by widgets.add("bosses", "Таймер боссов") { +BossesText }
    val Enabled by settings.boolean(
        "enabled",
        "Отображение таймера боссов",
        true
    ) on { Widget.enabled = it }
    val MinLevel by settings.selector(
        "min-level",
        "Мин. уровень босса",
        enumSelector<BossType>()
    ) { "${it?.level}" }
    val MaxLevel by settings.selector(
        "max-level",
        "Макс. уровень босса",
        enumSelector<BossType>(-1)
    ) { "${it?.level}" }
    val BossesCount by settings.selector(
        "render-count",
        "Кол-во отображаемых боссов",
        (0..<BossType.entries.size).toList().toSelector(-1)
    )
    val AlertDelay by settings.selector(
        "alert-time",
        "За сколько предупреждать о боссе",
        (0..120 step 5).toList().toSelector()
    ) { "$it сек." }
    val InlineMenuTime by settings.boolean(
        "menu-time",
        "Отображать время до спавна в меню",
        true
    )
    val Message by settings.boolean(
        "message",
        "Сообщение о спавне",
        false
    )
    val ClanMessage by settings.boolean(
        "clan-messages",
        "Сообщения в клановый чат",
        false
    )
    val Notify by settings.boolean(
        "notify",
        "Уведомления",
        false
    )

    private val Long.fixSeconds get() = (this / 1000) * 1000
    private val BossType.inLevelBounds get() = level in MinLevel.level..MaxLevel.level

    init {
        every(100) {
            updateBosses()
            fillInventory()
            fillBossData()
        }
    }

    fun notify(type: BossType, vararg text: String) = Notifies.showText(*text) {
        leftClick { _, state -> if (isHovered && state) sendCommand("boss ${type.level}") }
    }

    fun printAlertMessage(text: String, type: BossType) =
        printHoveredCommandMessage(text, "§aНажмите, чтобы начать телепортацию", "/boss ${type.level}")

    private fun fillInventory() {
        if (!InlineMenuTime) return

        val screen = Client.currentScreen

        if (screen !is GenericContainerScreen) return

        screen.screenHandler.stacks.forEach {
            val type = BossType[it.displayName ?: return@forEach] ?: return@forEach
            val timeText = ((Bosses[type] ?: return@forEach) - System.currentTimeMillis()).asTextTime

            val resetText = "§fВозрождение: §e$timeText".toText

            var lore = it.lore

            if (lore.getOrNull(1)?.string?.contains("Возрождение") == true) lore[1] = resetText
            else lore = (listOf(lore.first(), resetText) + lore.drop(1)).toMutableList()

            it.lore = lore
        }
    }

    private fun updateBosses() {
        val currentTime = System.currentTimeMillis()
        Bosses.forEach { (type, timestamp) ->
            val displayName = type.displayName
            val remainTime = timestamp - currentTime

            if (type.inLevelBounds && AlertDelay > 0 && type !in Alerted && remainTime / 1000 == AlertDelay.toLong()) {
                val timeText = remainTime.asTextTime
                if (Message) printAlertMessage("Босс §6$displayName §fвозродится через §6$timeText", type)
                if (ClanMessage) sendClanMessage("${ModLabel}§8: §fБосс §6$displayName §fвозродится через §6$timeText")
                if (Notify) notify(type, "Босс §6$displayName", "через §6$timeText")

                Alerted.add(type)
            }

            if (remainTime < 0) {
                Bosses.remove(type)
                Alerted.remove(type)

                if (!type.inLevelBounds || remainTime !in -2000..0) return@forEach
                if (Message) printAlertMessage("Босс §6$displayName §fвозродился.", type)
                if (ClanMessage) sendClanMessage("${ModLabel}§8: §fБосс $displayName §fвозродился.")
                if (Notify) notify(type, "Босс §6$displayName §fвозродился.")
            }
        }

        BossesText.lines = Bosses
            .filterKeys { it.inLevelBounds }
            .entries
            .sortedBy { it.value }
            .take(BossesCount)
            .map { (type, timestamp) -> "${type.displayName}§8: §f${(timestamp - currentTime).fixSeconds.asTextTime}" }
    }

    private fun fillBossData() {
        val (bossType, additionTime) = fetchWorldBossData() ?: return

        val currentTime = System.currentTimeMillis() + additionTime
        val cachedTime = Bosses[bossType] ?: 0

        if (cachedTime > 0 && abs(currentTime - cachedTime) < 5000) return

        Bosses[bossType] = currentTime
    }

    private fun fetchWorldBossData() = Client?.world?.entities
        ?.map { it.displayName.string.uncolored().replace("۞", "") }
        ?.sortedByDescending { it.startsWith("Босс") }
        ?.mapNotNull {
            when {
                it.startsWith("Босс") -> it.substring(5)
                "сек." in it || "мин." in it || "ч." in it -> it
                else -> null
            }
        }
        ?.run {
            (BossType[getOrNull(0) ?: return@run null] ?: return@run null) to (getOrNull(1)?.fromTextTime?.takeIf { it > 6000 }
                ?: return@run null)
        }

}