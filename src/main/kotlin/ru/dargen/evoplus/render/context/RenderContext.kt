package ru.dargen.evoplus.render.context

import ru.dargen.evoplus.event.game.PostTickEvent
import ru.dargen.evoplus.event.game.PreTickEvent
import ru.dargen.evoplus.event.input.KeyCharEvent
import ru.dargen.evoplus.event.input.KeyEvent
import ru.dargen.evoplus.event.input.MouseClickEvent
import ru.dargen.evoplus.event.input.MouseWheelEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.render.node.RectangleNode
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
abstract class RenderContext : RectangleNode() {

    init {
        registerInputHandlers()
        registerTickHandlers()
        registerRenderHandlers()
    }

    abstract fun registerRenderHandlers()

    fun registerInputHandlers() {
        on<KeyEvent> { if (allowInput()) changeKey(key, state) }
        on<KeyCharEvent> { if (allowInput()) typeChar(char, key) }

        on<MouseClickEvent> { if (allowInput()) mouseClick(mouse, button, state) }
        on<MouseWheelEvent> { if (allowInput()) mouseWheel(mouse, vWheel, hWheel) }
    }

    fun registerTickHandlers() {
        on<PreTickEvent> { preTick() }
        on<PostTickEvent> { postTick() }
    }

    fun allowInput(): Boolean = true

}