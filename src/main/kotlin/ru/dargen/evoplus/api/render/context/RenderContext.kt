package ru.dargen.evoplus.api.render.context

import ru.dargen.evoplus.api.event.game.PostTickEvent
import ru.dargen.evoplus.api.event.game.PreTickEvent
import ru.dargen.evoplus.api.event.input.KeyCharEvent
import ru.dargen.evoplus.api.event.input.KeyEvent
import ru.dargen.evoplus.api.event.input.MouseClickEvent
import ru.dargen.evoplus.api.event.input.MouseWheelEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.render.node.RectangleNode
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.v3

@KotlinOpens
abstract class RenderContext : RectangleNode() {

    var translationScale = v3(1.0, 1.0, 1.0)

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