package ru.dargen.evoplus.features.boss.timer

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import net.minecraft.item.Items
import pro.diamondworld.protocol.packet.boss.BossTimers
import ru.dargen.evoplus.api.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.render.node.input.button
import ru.dargen.evoplus.api.render.node.leftClick
import ru.dargen.evoplus.api.schduler.scheduleEvery
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.boss.timer.BossTimerFeature.MaxLevel
import ru.dargen.evoplus.features.boss.timer.BossTimerFeature.MinLevel
import ru.dargen.evoplus.features.misc.Notifies
import ru.dargen.evoplus.protocol.listen
import ru.dargen.evoplus.protocol.registry.BossLink
import ru.dargen.evoplus.protocol.registry.BossType
import ru.dargen.evoplus.util.currentMillis
import ru.dargen.evoplus.util.format.asTextTime
import ru.dargen.evoplus.util.format.fromTextTime
import ru.dargen.evoplus.util.minecraft.*
import ru.dargen.evoplus.util.selector.toSelector
import kotlin.math.absoluteValue

object BossTimerFeature : Feature("boss-timer", "Таймер боссов", itemStack(Items.CLOCK)) {

    private val BossMenuPattern = "[\uE910\uE911]".toRegex()

    val AlertedBosses = mutableSetOf<String>()
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

    val MinLevel by settings.selector("Мин. уровень босса", (0..450).toSelector())
    val MaxLevel by settings.selector("Макс. уровень босса", (0..450).toSelector(-1))
    val BossesCount by settings.selector("Кол-во отображаемых боссов", (0..60).toSelector(-1))

    val PreSpawnAlertTime by settings.selector("Предупреждать о боссе за", (0..120 step 5).toSelector()) { "$it сек." }
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
        listen<BossTimers> {
            if (PremiumTimers) it.timers
                .filter { it.value > 6000 }
                .mapKeys { BossType.valueOf(it.key)?.link ?: return@listen }
                .mapValues { it.value + currentMillis }
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
                && type.id !in AlertedBosses
                && remainTime / 1000 == PreSpawnAlertTime.toLong()
            ) {
                val timeText = remainTime.asTextTime

                AlertedBosses.add(type.id)

                if (type.inLevelBounds) {
                    if (PreSpawnMessage) message("§aБосс §6$displayName §aвозродится через §6$timeText", type)
                    if (PreSpawnClanMessage) sendClanMessage("§aБосс §6$displayName §aвозродится через §6$timeText")
                    if (PreSpawnNotify) notify(type, "Босс §6$displayName", "§fчерез §6$timeText")
                }
            }

            if (remainTime < 0) {
                Bosses.remove(type.id)
                AlertedBosses.remove(type.id)

                if (!type.inLevelBounds || remainTime !in -2000..0) return@forEach
                if (SpawnMessage) message("§aБосс §6$displayName §aвозродился.", type)
                if (SpawnClanMessage) sendClanMessage("§aБосс $displayName §aвозродился.")
                if (SpawnNotify) notify(type, "Босс §6$displayName §fвозродился")
            }
        }
    }

    private fun fillInventory() {
        if (!InlineMenuTime) return

        val screen = Client.currentScreen

        if (screen !is GenericContainerScreen || !BossMenuPattern.containsMatchIn(screen.title.string.uncolored())) return

        screen.screenHandler.stacks.forEach {
            val type = BossType.valueOfName(it.displayName?.string ?: return@forEach) ?: return@forEach
            val timeText = ((Bosses[type.id] ?: return@forEach) - currentMillis).asTextTime

            val resetText = "§fВозрождение: §e$timeText".asText()

            var lore = it.lore.toMutableList()

            if (lore.getOrNull(1)?.string?.contains("Возрождение") == true) lore[1] = resetText
            else lore = (listOf(lore.first(), resetText) + lore.drop(1)).toMutableList()

            it.lore = lore
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
                ?.let { if ('۞' in get(1)) (it / 1.5).toLong() else it }
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

val BossLink?.inLevelBounds get() = this?.ref?.level in MinLevel..MaxLevel
val BossType?.inLevelBounds get() = this?.level in MinLevel..MaxLevel

private val Long.fixSeconds get() = (this / 1000) * 1000