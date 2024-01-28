package ru.dargen.evoplus.features.boss

import ru.dargen.evoplus.api.render.context.receive.receiveScreen
import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.api.render.node.input.button
import ru.dargen.evoplus.api.render.node.input.toggle
import ru.dargen.evoplus.api.render.node.leftClick
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.api.render.node.tick
import ru.dargen.evoplus.features.boss.timer.BossTimerFeature
import ru.dargen.evoplus.protocol.registry.BossType
import ru.dargen.evoplus.util.format.asTextTime
import ru.dargen.evoplus.util.math.v3

object BossReceiveScreen {

    fun open(bosses: Map<BossType, Long>) = receiveScreen {
        val toggles = bosses.keys.associateWith { toggle() }

        toggles.toSortedMap().map { (type, toggle) ->
            hbox {
                indent = v3()
                +toggle
                +text {
                    leftClick { mouse, state -> if (state && isHovered) toggle.toggled = !toggle.toggled }
                    tick {
                        text = "${type.displayName}§f: ${(bosses[type]!! - System.currentTimeMillis()).asTextTime}"
                    }
                }
            }
        }.apply(selector::addElements)

        buttons.apply {
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
            +button("Рейдовые") {
                on {
                    toggles.forEach { it.value.toggled = it.key.isRaid }
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
    }.open()

}