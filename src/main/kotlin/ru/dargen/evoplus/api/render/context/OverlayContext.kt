package ru.dargen.evoplus.api.render.context

import net.minecraft.client.gui.screen.ChatScreen
import ru.dargen.evoplus.api.event.input.MouseMoveEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.event.render.OverlayRenderEvent
import ru.dargen.evoplus.api.event.window.WindowRescaleEvent
import ru.dargen.evoplus.api.event.window.WindowResizeEvent
import ru.dargen.evoplus.api.render.node.resize
import ru.dargen.evoplus.api.render.node.tick
import ru.dargen.evoplus.util.Client
import ru.dargen.evoplus.util.MousePosition
import ru.dargen.evoplus.util.Window
import ru.dargen.evoplus.util.WindowInitialized
import ru.dargen.evoplus.util.math.Vector3

data object OverlayContext : RenderContext() {

    val ScaleFactor get() = if (!WindowInitialized) 1.0 else Window.scaleFactor
    val BaseScaleFactor = 2.0

    val WindowSize
        get() = if (!WindowInitialized) Vector3()
        else Vector3(Window.scaledWidth.toDouble(), Window.scaledHeight.toDouble(), .0)
    val Resolution get() = size.clone()

    @get:JvmName("_scale")
    val Scale get() = scale.clone()

    init {
        resize {
            scale = Vector3(BaseScaleFactor / ScaleFactor)
            size = WindowSize * (ScaleFactor / BaseScaleFactor)
        }
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

    override fun allowInput() = Client?.currentScreen == null
            || Client.currentScreen is ChatScreen
            || ScreenContext.current()?.transparent == true

}