package ru.dargen.evoplus.feature.type.boss

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import net.minecraft.item.Items
import ru.dargen.evoplus.api.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.render.node.leftClick
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.feature.FeaturesScreen
import ru.dargen.evoplus.feature.type.misc.Notifies
import ru.dargen.evoplus.feature.type.share.ShareFeature
import ru.dargen.evoplus.util.Gson
import ru.dargen.evoplus.util.concurrent.every
import ru.dargen.evoplus.util.format.asTextTime
import ru.dargen.evoplus.util.format.fromTextTime
import ru.dargen.evoplus.util.fromJson
import ru.dargen.evoplus.util.minecraft.*
import ru.dargen.evoplus.util.selector.enumSelector
import ru.dargen.evoplus.util.selector.toSelector
import kotlin.math.absoluteValue

object BossFeature : Feature("boss-timer", "Таймер боссов", Items.CLOCK) {

    private val BossCapturePattern = "^Босс (.*) захвачен кланом (.*)!\$".toRegex()

    val Bosses: MutableMap<BossType, Long> by config("bosses", mutableMapOf())
    private val Alerted = mutableSetOf<BossType>()

    private val BossesText = text()
    val Widget by widgets.add("timer", "Таймер боссов") { +BossesText }

    val EnabledTimer by settings.boolean("Отображение таймера боссов", true) on { Widget.enabled = it }
    val MinLevel by settings.selector("Мин. уровень босса", enumSelector<BossType>()) { "${it?.level}" }
    val MaxLevel by settings.selector("Макс. уровень босса", enumSelector<BossType>(-1)) { "${it?.level}" }
    val BossesCount by settings.selector("Кол-во отображаемых боссов", (0..<BossType.entries.size).toSelector(-1))

    val PreSpawnAlertTime by settings.selector("Предупреждать о боссе за", (0..120 step 5).toSelector()) { "$it сек." }
    val InlineMenuTime by settings.boolean("Отображать время до спавна в меню", true)

    val SpawnMessage by settings.boolean("Сообщение о спавне", true)
    val PreSpawnMessage by settings.boolean("Сообщение до спавна", true)

    val SpawnClanMessage by settings.boolean("Сообщение о спавне в клановый чат", false)
    val PreSpawnClanMessage by settings.boolean("Сообщение до спавна в клановый чат", false)

    val PreSpawnNotify by settings.boolean("Уведомление до спавна", true)
    val SpawnNotify by settings.boolean("Уведомление о спавне", true)
    val UpdateNotify by settings.boolean("Уведомление об обновлении времени", true)

    val NotifyCapture by settings.boolean("Уведомление о захватах боссов", true)
    val AutoReset by settings.boolean("Автоматический сброс таймеров при рестарте", true)
    val Reset by settings.action("Сбросить таймеры") { Bosses.clear() }

    private val Long.fixSeconds get() = (this / 1000) * 1000
    private val BossType.inLevelBounds get() = level in MinLevel.level..MaxLevel.level

    init {
        ShareFeature.create(
            "bosses", "Таймеры боссов",
            { Gson.toJson(Bosses.mapValues { it.value - System.currentTimeMillis() }) }
        ) { nick, data ->
            val shared = fromJson<Map<BossType, Long>>(data)
                .mapValues { it.value + System.currentTimeMillis() }

            Notifies.showText(
                "§6$nick §fотправил вам боссов §7(${shared.size}).",
                "Нажмите, чтобы принять.",
                delay = 10.0
            ) {
                leftClick { _, state -> if (isHovered && state) BossReceiveScreen.open(shared) }
            }
        }

        on<ChatReceiveEvent> {
            if (AutoReset && text == "Перезагрузка сервера") Bosses.clear()
            BossCapturePattern.find(text)?.run {
                val type = BossType[groupValues[1]] ?: return@run
                val clan = groupValues[2]

                if (NotifyCapture) {
                    Notifies.showText("Босс ${type.displayName}§f захвачен", "кланом $clan.")
                }

                if (type in Bosses) {
                    Bosses[type] = Bosses[type]!! + 600_000
                }
            }
        }

        every(100) {
            updateBosses()
            fillInventory()
            fillBossData()
        }
    }

    fun notify(type: BossType, vararg text: String) = Notifies.showText(*text) {
        leftClick { _, state -> if (isHovered && state) sendCommand("boss ${type.level}") }
    }

    private fun printMessage(text: String, type: BossType) =
        printHoveredCommandMessage(text, "§aНажмите, чтобы начать телепортацию", "/boss ${type.level}")

    private fun fillInventory() {
        if (!InlineMenuTime) return

        val screen = Client.currentScreen

        if (screen !is GenericContainerScreen) return

        screen.screenHandler.stacks.forEach {
            val type = BossType[it.displayName?.string ?: return@forEach] ?: return@forEach
            val timeText = ((Bosses[type] ?: return@forEach) - System.currentTimeMillis()).asTextTime

            val resetText = "§fВозрождение: §e$timeText".asText()

            var lore = it.lore.toMutableList()

            if (lore.getOrNull(1)?.string?.contains("Возрождение") == true) lore[1] = resetText
            else lore = (listOf(lore.first(), resetText) + lore.drop(1)).toMutableList()

            it.lore = lore
        }
    }

    private fun updateBosses() {
        val currentTime = System.currentTimeMillis().fixSeconds
        Bosses.forEach { (type, timestamp) ->
            val displayName = type.displayName
            val remainTime = timestamp - currentTime

            if (type.inLevelBounds && PreSpawnAlertTime > 0 && type !in Alerted && remainTime / 1000 == PreSpawnAlertTime.toLong()) {
                val timeText = remainTime.asTextTime

                Alerted.add(type)

                if (type.inLevelBounds) {
                    if (PreSpawnMessage) printMessage("§aБосс §6$displayName §aвозродится через §6$timeText", type)
                    if (PreSpawnClanMessage) sendClanMessage("§aБосс §6$displayName §aвозродится через §6$timeText")
                    if (PreSpawnNotify) notify(type, "Босс §6$displayName", "§fчерез §6$timeText")
                }
            }

            if (remainTime < 0) {
                Bosses.remove(type)
                Alerted.remove(type)

                if (!type.inLevelBounds || remainTime !in -2000..0) return@forEach
                if (SpawnMessage) printMessage("§aБосс §6$displayName §aвозродился.", type)
                if (SpawnClanMessage) sendClanMessage("§aБосс $displayName §aвозродился.")
                if (SpawnNotify) notify(type, "Босс §6$displayName §fвозродился")
            }
        }

        BossesText.lines =
            (if (FeaturesScreen.isInWidgetEditor() && Bosses.isEmpty())
                BossType.entries.take(3).associateWith { currentTime + 13245L } else Bosses)
                .filterKeys { it.inLevelBounds }
                .entries
                .sortedBy { it.value }
                .take(BossesCount)
                .map { (type, timestamp) -> "${type.displayName}§8: §f${(timestamp - currentTime).asTextTime}" }
    }

    private fun fillBossData() {
        val (bossType, additionTime) = fetchWorldBossData() ?: return

        val spawnTime = System.currentTimeMillis() + additionTime
        val currentSpawnTime = Bosses[bossType] ?: 0

        if ((spawnTime - currentSpawnTime).absoluteValue < 13000) return

        if (UpdateNotify) Notifies.showText(
            "Босс §6${bossType.displayName} §fобновлен",
            "Возрождение через §6${additionTime.asTextTime}"
        )
        Bosses[bossType] = spawnTime.fixSeconds
    }

    private fun fetchWorldBossData() = Client?.world?.entities
        ?.map { it.displayName.string.uncolored() }
        ?.sortedByDescending { it.startsWith("Босс") }
        ?.mapNotNull {
            when {
                it.startsWith("Босс") -> it.substring(5)
                "сек." in it || "мин." in it || "ч." in it -> it
                else -> null
            }
        }
        ?.run {
            val type = BossType[getOrNull(0) ?: return@run null] ?: return@run null
            val delay = getOrNull(1)?.replace("۞", "")?.fromTextTime
                ?.let { if ('۞' in get(1)) (it / 1.5).toLong() else it }
                ?.takeIf { it > 6000 }
                ?: return@run null

            type to delay
        }

}