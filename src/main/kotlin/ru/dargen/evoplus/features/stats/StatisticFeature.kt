package ru.dargen.evoplus.features.stats

import com.google.common.cache.CacheBuilder
import net.minecraft.item.Items
import net.minecraft.util.math.BlockPos
import pro.diamondworld.protocol.packet.combo.Combo
import pro.diamondworld.protocol.packet.combo.ComboBlocks
import pro.diamondworld.protocol.packet.game.GameEvent
import pro.diamondworld.protocol.packet.game.LevelInfo
import pro.diamondworld.protocol.packet.statistic.StatisticInfo
import ru.dargen.evoplus.api.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.api.event.evo.GameEventChangeEvent
import ru.dargen.evoplus.api.event.fire
import ru.dargen.evoplus.api.event.interact.BlockBreakEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.api.render.node.input.button
import ru.dargen.evoplus.api.render.node.item
import ru.dargen.evoplus.api.render.node.preRender
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.api.scheduler.scheduleEvery
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.misc.Notifies
import ru.dargen.evoplus.features.stats.combo.ComboWidget
import ru.dargen.evoplus.features.stats.info.holder.StatisticHolder
import ru.dargen.evoplus.features.stats.info.holder.StatisticHolder.Combo
import ru.dargen.evoplus.features.stats.info.holder.StatisticHolder.Data
import ru.dargen.evoplus.features.stats.level.LevelWidget
import ru.dargen.evoplus.features.stats.pet.PetInfoWidget
import ru.dargen.evoplus.protocol.listen
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.itemStack
import ru.dargen.evoplus.util.minecraft.uncolored
import java.util.concurrent.TimeUnit
import kotlin.math.max

object StatisticFeature : Feature("statistic", "Статистика", Items.PAPER) {
    
    private val ComboTimerPattern =
        "Комбо закончится через (\\d+) секунд\\. Продолжите копать, чтобы не потерять его\\.".toRegex()
    
    val ComboCounterWidget by widgets.widget("Счетчик комбо", "combo-counter", widget = ComboWidget)
    val ActivePetsWidget by widgets.widget("Активные питомцы", "active-pets", widget = PetInfoWidget)
    val LevelRequireWidget by widgets.widget("Требования на уровень", "level-require", widget = LevelWidget)
    
    val NotifyCompleteLevelRequire by settings.boolean("Уведомлять при выполнении требований", true)
    val ComboProgressBarEnabled by settings.boolean("Шкала прогресса комбо") on {
        ComboWidget.ProgressBar.enabled = it
    }
    val LevelProgressBarEnabled by settings.boolean("Шкала прогресса уровня") on {
        LevelWidget.ProgressBar.enabled = it
    }
    
    var BlocksCount = 0
        set(value) {
            field = value
            BlocksCounterText.text = "${max(Data.blocks - field, 0)}"
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
    
    var BlocksPerSecondCounter = CacheBuilder
        .newBuilder()
        .expireAfterWrite(1, TimeUnit.SECONDS)
        .build<BlockPos, Int>()
    val BlocksPerSecondWidget by widgets.widget("Счетчик блоков за секунду", "blocks-per-second-counter") {
        origin = Relative.LeftCenter
        align = v3(.87, .60)
        +hbox {
            space = .0
            indent = v3()
            
            +text("0") {
                isShadowed = true
                preRender { _, _ -> text = "${BlocksPerSecondCounter.size()}" }
            }
            +item(itemStack(Items.WOODEN_PICKAXE)) {
                scale = v3(.7, .7, .7)
            }
        }
    }
    
    init {
        ComboWidget.ProgressBar.enabled = ComboProgressBarEnabled
        LevelWidget.ProgressBar.enabled = LevelProgressBarEnabled
        
        screen.baseElement("Сбросить счетчик блоков") { button("Сбросить") { on { BlocksCount = Data.blocks } } }
        StatisticHolder
        
        scheduleEvery(unit = TimeUnit.SECONDS) {
            PetInfoWidget.update()
            ComboWidget.update(Combo)
        }
        
        on<BlockBreakEvent> {
            BlocksPerSecondCounter.put(blockPos, 0)
        }
        
        on<ChatReceiveEvent> {
            ComboTimerPattern.find(text.uncolored())?.let {
                val remain = it.groupValues[1].toIntOrNull() ?: return@on
                Combo.remain = remain.toLong()
                ComboWidget.update(Combo)
            }
        }
        
        listen<Combo> {
            Combo.fetch(it)
            ComboWidget.update(Combo)
        }
        listen<ComboBlocks> {
            Combo.fetch(it)
            ComboWidget.update(Combo)
        }
        
        listen<LevelInfo> {
            val previousCompleted = Data.blocks >= Data.nextLevel.blocks
                && Data.money >= Data.nextLevel.money
            
            Data.fetch(it)
            LevelWidget.update(Data)
            
            val isCompleted = Data.blocks >= Data.nextLevel.blocks
                && Data.money >= Data.nextLevel.money
            
            if (NotifyCompleteLevelRequire && isCompleted && !previousCompleted) {
                Notifies.showText("§aВы можете повысить уровень!")
            }
            
            if (BlocksCount == 0) BlocksCount = it.blocks
            BlocksCounterText.text = "${max(it.blocks - BlocksCount, 0)}"
        }
        
        listen<GameEvent> {
            if (StatisticHolder.Event != it.type) {
                GameEventChangeEvent(StatisticHolder.Event, it.type).fire()
            }
            StatisticHolder.Event = it.type
        }
        
        listen<StatisticInfo> {
            StatisticHolder.accept(it.data)
        }
    }
    
}