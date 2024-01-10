package ru.dargen.evoplus.features.boss

import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.context.Overlay
import ru.dargen.evoplus.api.render.context.screen
import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.api.render.node.box.vbox
import ru.dargen.evoplus.api.render.node.input.button
import ru.dargen.evoplus.api.render.node.input.toggle
import ru.dargen.evoplus.api.render.node.leftClick
import ru.dargen.evoplus.api.render.node.scroll.vScrollView
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.api.render.node.tick
import ru.dargen.evoplus.features.boss.timer.BossTimerFeature
import ru.dargen.evoplus.protocol.registry.BossType
import ru.dargen.evoplus.util.format.asTextTime
import ru.dargen.evoplus.util.math.v3
import java.lang.System.currentTimeMillis

object BossReceiveScreen {

    fun open(bosses: Map<BossType, Long>) = screen {
        val toggles = bosses.keys.associateWith { toggle() }
        +vbox {
            align = Relative.Center
            origin = Relative.Center

            space = 1.0

            val selector = +vScrollView {
                box.color = Colors.TransparentBlack

                indent = v3(2.0, 2.0)
                space = 1.0

                toggles.toSortedMap().forEach { (type, toggle) ->
                    addElements(hbox {
                        indent = v3()
                        +toggle
                        +text {
                            leftClick { mouse, state ->  if (state && isHovered) toggle.toggled = !toggle.toggled }
                            tick {
                                text = "${type.displayName}§f: ${(bosses[type]!! - currentTimeMillis()).asTextTime}"
                            }
                        }
                    })
                }
            }
            val buttons = +hbox {
                indent = v3(2.0, 2.0)

                +button("Принять") {
                    on {
                        BossTimerFeature.Bosses.putAll(bosses.filter { toggles[it.key]!!.toggled }.mapKeys { it.key.id })
                        close()
                    }
                }
                +button("Все") {
                    on {
                        toggles.forEach { it.value.toggled = true }
                    }
                }
                +button("Отсутствующие") {
                    on {
                        toggles.forEach { it.value.toggled = it.key.id !in BossTimerFeature.Bosses }
                    }
                }
                +button("Убрать все") {
                    on {
                        toggles.forEach { it.value.toggled = false }
                    }
                }
            }
            tick {
                selector.size = v3(buttons.size.x, Overlay.ScaledResolution.y * .5)
            }
        }
    }.open()

}