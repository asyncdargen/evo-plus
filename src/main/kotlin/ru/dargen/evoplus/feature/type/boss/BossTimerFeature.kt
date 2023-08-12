package ru.dargen.evoplus.feature.type.boss

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import net.minecraft.item.Items
import ru.dargen.evoplus.ModLabel
import ru.dargen.evoplus.api.render.node.input.button
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.feature.misc.Notifies
import ru.dargen.evoplus.util.*
import ru.dargen.evoplus.util.concurrent.every
import ru.dargen.evoplus.util.format.asTextTime
import ru.dargen.evoplus.util.format.fromTextTime
import ru.dargen.evoplus.util.selector.enumSelector
import ru.dargen.evoplus.util.selector.toSelector
import java.util.*

object BossTimerFeature : Feature("boss-timer", "Таймер боссов", Items.CLOCK) {

    val Bosses: MutableMap<BossType, Long> = EnumMap(BossType::class.java)
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
        "Сообщение о спавне в клановый чат",
        false
    )
    val Notify by settings.boolean(
        "notify",
        "Уведомления о спавне",
        false
    )

    private val Long.fixSeconds get() = div(1000).times(1000)
    private val BossType.inLevelBounds get() = level in MinLevel.level..MaxLevel.level

    init {
        every(100) {
            updateBosses()
            fillInventory()
            fillBossData()
        }
    }

    fun notify(text: String, type: BossType) = Notifies.show(text) {
        +button("§aТелепортироваться") { on { sendCommand("boss ${type.level}") } }
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

            val lore = it.lore
            val firstRow = lore[0].string

            it.setLore(
                lore.apply {
                    if ("Возрождение" in firstRow) set(0, resetText)
                    else add(0, resetText)
                }
            )
        }
    }

    private fun updateBosses() {
        Bosses.forEach { (type, timestamp) ->
            val displayName = type.toString()
            val remainTime = (timestamp - System.currentTimeMillis()).fixSeconds

            if (type.inLevelBounds && AlertDelay > 0 && type !in Alerted && remainTime / 1000 <= AlertDelay) {
                val remainTimeText = remainTime.asTextTime.toText
                if (Message) printAlertMessage("Босс §6$displayName §fвозродится через §6$remainTimeText секунд§f.", type)
                if (ClanMessage) sendClanMessage("${ModLabel}§8: §fБосс §6$displayName §fвозродится через §6$remainTimeText секунд§f.")
                if (Notify) notify("Босс §6$displayName §fвозродится\nчерез §f$remainTimeText секунд.", type)

                Alerted.add(type)
            }

            if (remainTime <= 0) {
                Bosses.remove(type)
                Alerted.remove(type)

                if (!type.inLevelBounds) return@forEach
                if (Message) printAlertMessage("Босс §6$displayName §fвозродился.", type)
                if (ClanMessage) sendClanMessage("${ModLabel}§8: §fБосс $displayName §fвозродился.")
                if (Notify) notify("Босс §6$displayName §fвозродился.", type)
            }
        }

        BossesText.lines = Bosses
            .filterKeys { it.inLevelBounds }
            .entries
            .sortedBy { it.value }
            .take(BossesCount)
            .map { (type, timestamp) -> "$type§8: §f${(timestamp - System.currentTimeMillis()).asTextTime}" }
    }

    private fun fillBossData() {
        val (bossType, additionTime) = fetchWorldBossData() ?: return

        val totalTime = System.currentTimeMillis() + additionTime
        val currentTime = Bosses[bossType] ?: 0

        if (totalTime - currentTime < 5000) return

        Bosses[bossType] = totalTime
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
            (BossType[getOrNull(0) ?: return@run null] ?: return@run null) to (getOrNull(1)
                ?: return@run null).fromTextTime
        }

}