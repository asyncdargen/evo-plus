package ru.dargen.evoplus.feature.type

import net.minecraft.item.Items
import net.minecraft.util.Hand
import ru.dargen.evoplus.api.event.game.PostTickEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.util.minecraft.Client
import ru.dargen.evoplus.util.minecraft.Player
import ru.dargen.evoplus.util.minecraft.isInWater
import ru.dargen.evoplus.util.selector.toSelector

object FishingFeature : Feature("fishing", "Рыбалка", Items.FISHING_ROD) {

    var AutoFish by settings.boolean("auto-fish", "Автоматическая удочка", true)
    var HookDelay by settings.selector("auto-fish-delay", "Задержка удочки (тик = 50 мс)", (0..40).toSelector(1))
    var HighlightSources by settings.boolean("ightlight-sources", "Подсветка источников")

    init {
        var fishHookTicks = 0
        on<PostTickEvent> {
            if (AutoFish && Player?.fishHook?.isInWater == true) {
                if (++fishHookTicks >= HookDelay) {
                    fishHookTicks = 0
                    Player?.fishHook?.kill()
                    Client?.interactionManager?.interactItem(Player!!, Hand.MAIN_HAND)
                }
            } else fishHookTicks = 0
         }
    }

}