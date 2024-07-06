package ru.dargen.evoplus.features.boss.timer

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import net.minecraft.item.Items
import pro.diamondworld.protocol.packet.boss.BossTimers
import pro.diamondworld.protocol.packet.game.GameEvent.EventType.MYTHICAL_EVENT
import ru.dargen.evoplus.api.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.api.event.evo.GameEventChangeEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.render.node.input.button
import ru.dargen.evoplus.api.render.node.leftClick
import ru.dargen.evoplus.api.scheduler.scheduleEvery
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.boss.BossFeature
import ru.dargen.evoplus.features.boss.timer.BossTimerFeature.MaxLevel
import ru.dargen.evoplus.features.boss.timer.BossTimerFeature.MinLevel
import ru.dargen.evoplus.features.misc.Notifies
import ru.dargen.evoplus.features.stats.info.holder.StatisticHolder
import ru.dargen.evoplus.protocol.listen
import ru.dargen.evoplus.protocol.registry.BossType
import ru.dargen.evoplus.util.currentMillis
import ru.dargen.evoplus.util.format.asTextTime
import ru.dargen.evoplus.util.format.fromTextTime
import ru.dargen.evoplus.util.minecraft.*
import ru.dargen.evoplus.util.selector.toSelector
import kotlin.math.absoluteValue

object BossTimerFeature : Feature("boss-timer", "Таймер боссов", itemStack(Items.CLOCK)) {

    val AlertedBosses = mutableSetOf<String>()
    val PreAlertedBosses = mutableSetOf<String>()
    val Bosses: MutableMap<String, Long> by config("bosses", hashMapOf())
    val ComparedBosses
        get() = Bosses
            .mapKeys { BossType.valueOf(it.key) }
            .filter { it.key.inLevelBounds }
            .mapKeys { it.key!! }
            .asSequence()
            .sortedBy { it.value }

    val TimerWidget by widgets.widget("Таймер боссов", "boss-timer", widget = BossTimerWidget)

    val WidgetTeleport by settings.boolean("Телепорт по клику в виджете")
    val PremiumTimers by settings.boolean("Покупной таймер")

    val MinLevel by settings.selector("Мин. уровень босса", (0..520).toSelector())
    val MaxLevel by settings.selector("Макс. уровень босса", (0..520).toSelector(-1))
    val BossesCount by settings.selector("Кол-во отображаемых боссов", (0..60).toSelector(-1))

    val ShortName by settings.boolean("Сокращение имени босса")
    val ShortTimeFormat by settings.boolean("Сокращенный формат времени")

    val PreSpawnAlertTime by settings.selector("Предупреждать о боссе за", (0..360 step 5).toSelector()) { "$it сек." }
    val PostSpawnShowTime by settings.selector(
        "Сохранять в таймере после спавна",
        (0..360 step 5).toSelector()
    ) { "$it сек." }
    val OnlyRaidBosses by settings.boolean("Отображать только рейдовых боссов")
    val InlineMenuTime by settings.boolean("Отображать время до спавна в меню", true)

    val SpawnMessage by settings.boolean("Сообщение о спавне", true)
    val PreSpawnMessage by settings.boolean("Сообщение до спавна", true)

    val SpawnClanMessage by settings.boolean("Сообщение о спавне в клановый чат", false)
    val PreSpawnClanMessage by settings.boolean("Сообщение до спавна в клановый чат", false)

    val PreSpawnNotify by settings.boolean("Уведомление до спавна", true)
    val SpawnNotify by settings.boolean("Уведомление о спавне", true)
    val UpdateNotify by settings.boolean("Уведомление об обновлении времени", true)

    val AutoReset by settings.boolean("Автоматический сброс таймеров при рестарте", true)

    init {
        screen.baseElement("Сбросить таймеры") { button("Сбросить") { on { Bosses.clear() } } }

        on<ChatReceiveEvent> {
            if (AutoReset && text == "Перезагрузка сервера") Bosses.clear()
        }

        on<GameEventChangeEvent> {
            if (old == MYTHICAL_EVENT || new == MYTHICAL_EVENT) Bosses.replaceAll { bossId, spawn ->
                val bossType = BossType.valueOf(bossId) ?: return@replaceAll spawn

                if (!bossType.isRaid) return@replaceAll spawn

                (if (old == MYTHICAL_EVENT) spawn * 1.5 else spawn / 1.5).toLong()
            }
        }

        listen<BossTimers> {
            if (PremiumTimers) it.timers
                .mapKeys { BossType.valueOf(it.key) ?: return@listen }
                .mapValues { (it.value + currentMillis * if (StatisticHolder.Event === MYTHICAL_EVENT && it.key.isRaid) 1.5 else 1.0).toLong() }
                .mapKeys { it.key.id }
                .let(Bosses::putAll)
        }

        scheduleEvery(period = 10) {
            if (!PremiumTimers) fillBossData()

            fillInventory()
            updateBosses()

            BossTimerWidget.update()
        }
    }

    fun updateBosses() {
        ComparedBosses.forEach { (type, timestamp) ->
            val displayName = type.displayName
            val remainTime = timestamp - currentMillis

            if (type.inLevelBounds
                && PreSpawnAlertTime > 0
                && type.id !in PreAlertedBosses
                && remainTime / 1000 == PreSpawnAlertTime.toLong()
            ) {
                val timeText = remainTime.asTextTime

                PreAlertedBosses.add(type.id)

                if (type.inLevelBounds) {
                    if (PreSpawnMessage) message("§aБосс §6$displayName §aвозродится через §6$timeText", type)
                    if (PreSpawnClanMessage) sendClanMessage("§aБосс §6$displayName §aвозродится через §6$timeText")
                    if (PreSpawnNotify) notify(type, "Босс §6$displayName", "§fчерез §6$timeText")
                }
            }

            if (remainTime <= 0) {
                if (remainTime <= -PostSpawnShowTime * 1000) {
                    Bosses.remove(type.id)
                    PreAlertedBosses.remove(type.id)
                    AlertedBosses.remove(type.id)
                }

                if (!type.inLevelBounds || type.id in AlertedBosses || remainTime !in -2000..0) return@forEach

                AlertedBosses.add(type.id)
                if (SpawnMessage) message("§aБосс §6$displayName §aвозродился.", type)
                if (SpawnClanMessage) sendClanMessage("§aБосс $displayName §aвозродился.")
                if (SpawnNotify) notify(type, "Босс §6$displayName §fвозродился")
            }
        }
    }

    private fun fillInventory() {
        if (!InlineMenuTime) return

        val screen = CurrentScreen

        if (screen !is GenericContainerScreen || !BossFeature.BossMenuPattern.containsMatchIn(screen.title.string.uncolored())) return

        screen.screenHandler.stacks.forEach {
            val type = BossType.valueOfName(it.displayName?.string ?: return@forEach) ?: return@forEach
            val timeText = ((Bosses[type.id] ?: return@forEach) - currentMillis).asTextTime

            val resetText = "§fВозрождение: §e$timeText".asText()

            it.lore = it.lore.toMutableList().run {
                if (getOrNull(1)?.string?.contains("Возрождение") == true) set(1, resetText)
                else return@run (listOf(first(), resetText) + drop(1))
                this
            }
        }
    }

    private fun fillBossData() {
        val (type, additionTime) = fetchWorldBossData() ?: return

        val spawnTime = currentMillis + additionTime
        val currentSpawnTime = Bosses[type.id] ?: 0

        if ((spawnTime - currentSpawnTime).absoluteValue < 13000) return

        if (UpdateNotify) Notifies.showText(
            "Босс §6${type.displayName} §fобновлен",
            "Возрождение через §6${additionTime.asTextTime}"
        )
        Bosses[type.id] = spawnTime.fixSeconds
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
            val type = BossType.valueOfName(getOrNull(0) ?: return@run null) ?: return@run null
            val delay = getOrNull(1)?.replace("۞", "")?.fromTextTime
                ?.let { if (StatisticHolder.Event === MYTHICAL_EVENT && type.isRaid) (it / 1.5).toLong() else it }
                ?.takeIf { it > 6000 }
                ?: return@run null

            type to delay
        }

    fun message(text: String, type: BossType) =
        printHoveredCommandMessage(text, "§aНажмите, чтобы начать телепортацию", "/boss ${type.level}")

    fun notify(type: BossType, vararg text: String) = Notifies.showText(*text) {
        leftClick { _, state -> if (isHovered && state) sendCommand("boss ${type.level}") }
    }

}

val BossType?.inLevelBounds get() = this?.level in MinLevel..MaxLevel

private val Long.fixSeconds get() = (this / 1000) * 1000