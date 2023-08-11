package ru.dargen.evoplus.feature.type.boss

import net.minecraft.item.Items
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.util.selector.enumSelector
import ru.dargen.evoplus.util.selector.toSelector
import java.util.*

object BossTimerFeature : Feature("boss-timer", "Таймер боссов", Items.CLOCK) {

    val bosses: MutableMap<BossType, BossTimerEntry> = EnumMap(BossType::class.java)

    val widget by widgets.add("bosses", "Таймер боссов") {
//        var
        +text()
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
    val inlineMenuTime by settings.boolean(
        "menu-time",
        "Отображать время до спавна в меню",
        true
    )
    val bossOrder by settings.switcher(
        "render-order",
        "Сортировка боссов",
        enumSelector<BossRenderOrder>()
    )
    val notify by settings.boolean(
        "notify",
        "Уведомления о спавне",
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
    val alertDelay by settings.selector(
        "alert-time",
        "За сколько предупреждать о боссе",
        (0..120 step 15).toList().toSelector()
    ) { "$it сек." }


}