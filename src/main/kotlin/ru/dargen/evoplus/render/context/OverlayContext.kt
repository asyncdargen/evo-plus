package ru.dargen.evoplus.render.context

import net.minecraft.client.gui.screen.ChatScreen
import ru.dargen.evoplus.event.input.MouseMoveEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.event.render.OverlayRenderEvent
import ru.dargen.evoplus.event.window.WindowResizeEvent
import ru.dargen.evoplus.render.node.tick
import ru.dargen.evoplus.util.Client
import ru.dargen.evoplus.util.Window
import ru.dargen.evoplus.util.WindowInitialized
import ru.dargen.evoplus.util.kotlin.safeCast

data object OverlayContext : RenderContext() {

    val ScaleFactor get() = if (!WindowInitialized) 1.0 else Window.scaleFactor
    val BaseScaleFactor = 2.0

    val Resolution get() = size.clone()

    @get:JvmName("_scale")
    val Scale get() = scale.clone()

    override fun registerRenderHandlers() {
        tick { scale.set(BaseScaleFactor / ScaleFactor) }
        on<OverlayRenderEvent> { render(matrices, tickDelta) }

        on<WindowResizeEvent> {
            size.set(width * (ScaleFactor / BaseScaleFactor), height * (ScaleFactor / BaseScaleFactor), .0)
        }
    }

    override fun registerInputHandlers() {
        super.registerInputHandlers()
        on<MouseMoveEvent> { if (allowInput()) mouseMove(mouse) }
    }

    override fun allowInput() = Client?.currentScreen == null
            || Client.currentScreen is ChatScreen
            || Client?.currentScreen.safeCast<ScreenContext>()?.transparent == true

}