package ru.dargen.evoplus.api.render.context

import com.mojang.blaze3d.platform.GlStateManager.DstFactor
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor
import com.mojang.blaze3d.systems.RenderSystem.*
import ru.dargen.evoplus.api.event.input.MouseMoveEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.event.render.WorldRenderEvent
import ru.dargen.evoplus.api.render.node.postTransform
import ru.dargen.evoplus.api.render.node.preTransform
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.Client
import ru.dargen.evoplus.util.minecraft.Player

data object World : RenderContext() {

    init {
        scale = v3(-.025, -.025, .025)
        translationScale = v3(1.0, 1.0, 1.0) / scale
        preTransform { _, tickDelta ->
            val player = Player ?: return@preTransform

            position.set(
                -player.lastRenderX - (player.x - player.lastRenderX) * tickDelta,
                -player.lastRenderY - (player.y - player.lastRenderY) * tickDelta,
                -player.lastRenderZ - (player.z - player.lastRenderZ) * tickDelta
            )

            enableBlend()
            blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA, SrcFactor.ONE, DstFactor.ZERO)
        }

        postTransform { _, _ -> disableBlend() }
    }

    override fun registerRenderHandlers() {
        on<WorldRenderEvent> { render(matrices, tickDelta) }
    }

    override fun registerInputHandlers() {
        super.registerInputHandlers()
        on<MouseMoveEvent> { if (allowInput()) mouseMove(mouse) }
    }

    override fun allowInput() = Client?.currentScreen == null

}