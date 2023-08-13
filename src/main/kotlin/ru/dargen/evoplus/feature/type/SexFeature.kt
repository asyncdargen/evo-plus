package ru.dargen.evoplus.feature.type

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import net.minecraft.item.Items
import ru.dargen.evoplus.api.event.inventory.InventoryOpenEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.util.Client
import ru.dargen.evoplus.util.concurrent.every
import ru.dargen.evoplus.util.minecraft.Inventories
import ru.dargen.evoplus.util.sendCommand
import java.util.concurrent.TimeUnit

object SexFeature : Feature("sex", "Сексы", Items.END_ROD) {

    var autoSell by settings.boolean("auto-sell", "Авто продажа", false) on {
        if (!it) {
            requestedMenu = false
        }
    }
    var requestedMenu = false

    init {
        every(7, TimeUnit.SECONDS){
            if (autoSell && Client?.currentScreen !is GenericContainerScreen) {
                requestedMenu = true
                sendCommand("menu")
            }
        }
        on<InventoryOpenEvent> {
            if (requestedMenu) {
                isHidden = true
                requestedMenu = false
                Inventories.click(syncId, 21)
            }
        }
    }

}