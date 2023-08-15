package ru.dargen.evoplus.api.render.context.world

import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.event.render.WorldRenderEvent
import ru.dargen.evoplus.api.render.context.RenderContext
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.Client

data object World : RenderContext() {

    override fun registerRenderHandlers() {
        on<WorldRenderEvent> {
            renderInWorld(matrices, tickDelta, camera, positionMatrix, vertexConsumers)
        }
    }

    override fun allowInput() = Client?.currentScreen == null
}
