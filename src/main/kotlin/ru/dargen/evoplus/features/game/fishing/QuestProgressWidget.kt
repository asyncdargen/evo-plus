package ru.dargen.evoplus.features.game.fishing

import net.minecraft.item.Items
import pro.diamondworld.protocol.packet.fishing.quest.HourlyQuestInfo
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.Tips
import ru.dargen.evoplus.api.render.node.Node
import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.api.render.node.box.vbox
import ru.dargen.evoplus.api.render.node.item
import ru.dargen.evoplus.api.render.node.postRender
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.feature.isWidgetEditor
import ru.dargen.evoplus.feature.widget.WidgetBase
import ru.dargen.evoplus.features.stats.info.holder.HourlyQuestInfoHolder
import ru.dargen.evoplus.protocol.registry.HourlyQuestType
import ru.dargen.evoplus.util.currentMillis
import ru.dargen.evoplus.util.format.asShortTextTime
import ru.dargen.evoplus.util.math.scale
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.customItem

object NormalProgressWidget : QuestProgressWidget("NORMAL") {

    override fun Node.prepare() {
        align = Relative.LeftCenter + v3(y = .15)
        origin = Relative.LeftCenter
    }
}

object NetherProgressWidget : QuestProgressWidget("NETHER") {

    override fun Node.prepare() {
        align = Relative.LeftCenter + v3(.2, .15)
        origin = Relative.LeftCenter
    }
}

open class QuestProgressWidget(val typeName: String) : WidgetBase {

    override val node = vbox {
        space = .0
        indent = v3()
    }

    fun update() {
        val filterQuests = HourlyQuestType.values.filter { it.type == typeName }

        node._children =
            (if (FishingFeature.HourlyQuests.filterValues { it.type == typeName }.isEmpty() && isWidgetEditor)
                filterQuests
                    .take(4)
                    .map { HourlyQuestInfoHolder(it, HourlyQuestInfo.HourlyQuest(it.id, 0, 111111)) }
            else filterQuests
                .mapNotNull { FishingFeature.HourlyQuests[it.id] }
                .filter { it.timestamp > currentMillis })
                .mapIndexed { index, info ->
                    val order = index + 1

                    val isCompleted = info.progress < 0
                    val remainTime = (info.timestamp - currentMillis).coerceAtLeast(0L)

                    val text = buildList {
                        add(" §8§a№$order §7${remainTime.asShortTextTime}")
                        if (isCompleted) return@buildList

                        add(" §9Прогресс: ${info.progress}/${info.needed}")
                    }

                    hbox {
                        space = 1.0
                        indent = v3()

                        +item(customItem(Items.PAPER, if (isCompleted) 374 else 372)) { scale = scale(.7, .7) }
                        +text(text) {
                            isShadowed = true
                        }

                        postRender { matrices, _ ->

                            if (isHovered && !isCompleted && !isWidgetEditor) Tips.draw(matrices, info.lore)
                        }

                        recompose()
                    }
                }.toMutableList()
    }
}