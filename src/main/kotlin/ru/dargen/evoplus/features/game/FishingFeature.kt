package ru.dargen.evoplus.features.game

import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.item.Items
import net.minecraft.util.Hand
import ru.dargen.evoplus.api.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.api.event.game.PostTickEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.event.render.ScreenRenderEvent
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.misc.Notifies
import ru.dargen.evoplus.util.format.spacing
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.*
import ru.dargen.evoplus.util.render.drawText
import ru.dargen.evoplus.util.selector.toSelector

object FishingFeature : Feature("fishing", "Рыбалка", Items.FISHING_ROD) {

    val PetExpPattern = "^Опыта дает питомцу: (\\d+)\$".toRegex()
    val HigherBitingPattern = "^На локации \"([\\S\\s]+)\" повышенный клёв!\$".toRegex()

    val AutoFish by settings.boolean("Автоматическая удочка", true)
    val HookDelay by settings.selector("Задержка удочки (тик = 50 мс)", (0..40).toSelector(1))
    val HigherBitingNotify by settings.boolean("Уведомления о повышенном клёве", true)
    val ShowFishExpInInventory by settings.boolean("Отображение опыта рыбы в инвентаре", true)

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
                Notifies.showText("На локации §6${groupValues[1]}", "повышенный клёв.")
            }
        }

        on<ScreenRenderEvent.Post> {
            if (ShowFishExpInInventory && screen is InventoryScreen) {
                val exp = Player!!.inventory.items.mapNotNull { item ->
                    item.lore.getOrNull(2)
                        ?.string
                        ?.let { PetExpPattern.find(it.trim())?.groupValues?.getOrNull(1)?.toIntOrNull()?.times(item.count) }
                }.sum().takeIf { it > 0 } ?: return@on

                val scale = .9f
                val x = screen.width / 2.0 + 89.0
                val y = screen.height / 2.0 - 6.0

                matrices.push()

                matrices.translate(x, y, .0)
                matrices.scale(scale, scale, scale)

                matrices.drawText("Опыт питомцам: ${exp.spacing()}", v3(), -1)

                matrices.pop()
            }
        }
    }

}