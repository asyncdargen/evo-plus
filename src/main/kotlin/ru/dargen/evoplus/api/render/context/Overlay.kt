package ru.dargen.evoplus.api.render.context

import ru.dargen.evoplus.api.event.input.MouseMoveEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.event.render.OverlayRenderEvent
import ru.dargen.evoplus.api.event.window.WindowRescaleEvent
import ru.dargen.evoplus.api.event.window.WindowResizeEvent
import ru.dargen.evoplus.api.render.node.resize
import ru.dargen.evoplus.api.render.node.tick
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.MousePosition
import ru.dargen.evoplus.util.minecraft.Window
import ru.dargen.evoplus.util.minecraft.WindowInitialized

data object Overlay : RenderContext() {

    val ScaleFactor get() = if (!WindowInitialized) 1.0 else Window.scaleFactor
    val WindowSize
        get() = if (!WindowInitialized) v3()
        else v3(Window.scaledWidth.toDouble(), Window.scaledHeight.toDouble(), .0)

    val ScaledMouse get() = MousePosition / Scale
    val ScaledResolution get() = size.clone()
    @get:JvmName("_scale")
    val Scale get() = scale.clone()

    init {
        resize { size = WindowSize }
        tick { mouseMove(MousePosition) }
        resize()
    }

    override fun registerRenderHandlers() {
        on<OverlayRenderEvent> { render(matrices, tickDelta) }
        on<WindowResizeEvent> { resize() }
        on<WindowRescaleEvent> { resize() }
    }

    override fun registerInputHandlers() {
        super.registerInputHandlers()
        on<MouseMoveEvent> { if (allowInput()) mouseMove(mouse) }
    }

    override fun allowInput() = ScreenContext.current()?.transparent != false

}