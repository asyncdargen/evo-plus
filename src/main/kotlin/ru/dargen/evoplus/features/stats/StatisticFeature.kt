package ru.dargen.evoplus.features.stats

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import net.minecraft.item.Items
import pro.diamondworld.protocol.packet.combo.Combo
import pro.diamondworld.protocol.packet.combo.ComboBlocks
import pro.diamondworld.protocol.packet.game.GameEvent
import pro.diamondworld.protocol.packet.game.LevelInfo
import pro.diamondworld.protocol.packet.statistic.StatisticInfo
import ru.dargen.evoplus.api.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.api.render.node.input.button
import ru.dargen.evoplus.api.render.node.item
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.api.schduler.scheduleEvery
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.misc.Notifies
import ru.dargen.evoplus.features.stats.combo.ComboData
import ru.dargen.evoplus.features.stats.combo.ComboWidget
import ru.dargen.evoplus.features.stats.level.LevelWidget
import ru.dargen.evoplus.features.stats.pet.PetInfo
import ru.dargen.evoplus.features.stats.pet.PetInfoWidget
import ru.dargen.evoplus.protocol.listen
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.itemStack
import ru.dargen.evoplus.util.minecraft.uncolored
import java.util.concurrent.TimeUnit

object StatisticFeature : Feature("statistic", "Статистика", Items.PAPER) {

    private val ComboTimerPattern =
        "Комбо закончится через (\\d+) секунд\\. Продолжите копать, чтобы не потерять его\\.".toRegex()

    val ComboCounterWidget by widgets.widget("Счетчик комбо", "combo-counter", widget = ComboWidget)
    val ComboData = ComboData()

    val Statistic = Statistic()
    val ActivePetsWidget by widgets.widget("Активные питомцы", "active-pets", widget = PetInfoWidget)
    val LevelRequireWidget by widgets.widget("Требования на уровень", "level-require", widget = LevelWidget)

    val NotifyCompleteLevelRequire by settings.boolean("Уведомлять при выполнении требований", true)
    val ActivePets = mutableListOf<PetInfo>()

    var BlocksCount = 0
        set(value) {
            field = value
            BlocksCounterText.text = "${Statistic.blocks - field}"
        }
    val BlocksCounterText = text("0") { isShadowed = true }
    val BlocksCounterWidget by widgets.widget("Счетчик блоков", "block-counter") {
        origin = Relative.LeftCenter
        align = v3(.87, .54)
        +hbox {
            space = .0
            indent = v3()

            +BlocksCounterText
            +item(itemStack(Items.DIAMOND_PICKAXE)) {
                scale = v3(.7, .7, .7)
            }
        }
    }

    var CurrentEvent = GameEvent.EventType.NONE

    init {
        screen.baseElement("Сбросить счетчик блоков") { button("Сбросить") { on { BlocksCount = Statistic.blocks } } }

        listen<StatisticInfo> { statisticInfo ->
            if (!statisticInfo.data.containsKey("pets")) return@listen

            val petsData = statisticInfo.data["pets"] as String
            val petsJsons = Json.decodeFromString<Array<JsonElement>>(petsData)
            val pets = petsJsons.map { Json.decodeFromJsonElement<PetInfo>(it) }

            ActivePets.clear()
            ActivePets.addAll(pets)
        }

        scheduleEvery(unit = TimeUnit.SECONDS) { ComboWidget.update(ComboData) }
        listen<Combo> {
            ComboData.fetch(it)
            ComboWidget.update(ComboData)
        }
        listen<ComboBlocks> {
            ComboData.fetch(it)
            ComboWidget.update(ComboData)
        }
        on<ChatReceiveEvent> {
            ComboTimerPattern.find(text.uncolored())?.let {
                val remain = it.groupValues[1].toIntOrNull() ?: return@on
                ComboData.remain = remain.toLong()
                ComboWidget.update(ComboData)
            }
        }

        listen<LevelInfo> {
            val previousCompleted = Statistic.blocks >= Statistic.nextLevel.blocks
                    && Statistic.money >= Statistic.nextLevel.money
            Statistic.fetch(it)
            LevelWidget.update(Statistic)
            val isCompleted = Statistic.blocks >= Statistic.nextLevel.blocks
                    && Statistic.money >= Statistic.nextLevel.money

            if (NotifyCompleteLevelRequire && isCompleted && !previousCompleted) {
                Notifies.showText("§aВы можете повысить уровень!")
            }

            if (BlocksCount == 0) BlocksCount = it.blocks
            BlocksCounterText.text = "${it.blocks - BlocksCount}"
        }
        listen<GameEvent> {
            CurrentEvent = it.type
        }
    }

}