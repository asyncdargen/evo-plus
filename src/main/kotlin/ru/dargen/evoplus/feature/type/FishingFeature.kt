package ru.dargen.evoplus.feature.type

import net.minecraft.item.Items
import net.minecraft.util.Hand
import ru.dargen.evoplus.api.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.api.event.game.PostTickEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.feature.type.misc.Notifies
import ru.dargen.evoplus.util.minecraft.InteractionManager
import ru.dargen.evoplus.util.minecraft.Player
import ru.dargen.evoplus.util.minecraft.isInWater
import ru.dargen.evoplus.util.minecraft.uncolored
import ru.dargen.evoplus.util.selector.toSelector

object FishingFeature : Feature("fishing", "Рыбалка", Items.FISHING_ROD) {

    val HigherBitingPattern = "^На локации \"([\\S\\s]+)\" повышенный клев\\.\$".toRegex()

    var AutoFish by settings.boolean("Автоматическая удочка", true)
    var HookDelay by settings.selector("Задержка удочки (тик = 50 мс)", (0..40).toSelector(1))
    var HigherBitingNotify by settings.boolean("Уведомления о повышенном клеве", true)
//    var HighlightSources by settings.boolean("hightlight-sources", "Подсветка источников")

    init {
        var fishHookTicks = 0
        on<PostTickEvent> {
            if (AutoFish && Player?.fishHook?.isInWater == true) {
                if (++fishHookTicks >= HookDelay) {
                    fishHookTicks = 0
                    Player?.fishHook?.kill()
                    InteractionManager?.interactItem(Player!!, Hand.MAIN_HAND)
                }
            } else fishHookTicks = 0
        }
        on<ChatReceiveEvent> {
            if (HigherBitingNotify) HigherBitingPattern.find(text.uncolored())?.run {
                Notifies.showText("Локация §6${groupValues[1]}", "повышенный клев.")
            }
        }
    }
}