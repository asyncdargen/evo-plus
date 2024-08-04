package ru.dargen.evoplus.features.chat.market

import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.api.render.node.tick
import ru.dargen.evoplus.feature.widget.WidgetBase
import ru.dargen.evoplus.util.currentMillis
import ru.dargen.evoplus.util.format.asShortTextTime
import ru.dargen.evoplus.util.math.v3

object MarketChatTimerWidget : WidgetBase {
    
    var RemainingTime = 0L
    
    override val node = hbox {
        space = .0
        indent = v3()
        
        +text {
            tick {
                text = "§6Ⓜ§f" + if (RemainingTime < currentMillis) "§a✔"
                else (RemainingTime - currentMillis).asShortTextTime
            }
        }
    }
}