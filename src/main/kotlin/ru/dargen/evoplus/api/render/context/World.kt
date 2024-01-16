package ru.dargen.evoplus.api.render.context

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gl.VertexBuffer
import net.minecraft.client.render.Camera
import net.minecraft.client.render.VertexConsumerProvider.Immediate
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.event.render.WorldRenderEvent
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.Client

data object World : RenderContext() {

    lateinit var Camera: Camera
    lateinit var VertexConsumers: Immediate

//    override var rotation = v3(y = 180.0).radians()
    override var scale = v3(-.025, -.025, .025)
    override var translationScale = v3(1.0, 1.0, 1.0) / scale

    override fun registerRenderHandlers() {
        on<WorldRenderEvent> {
            Camera = camera
            VertexConsumers = vertexConsumers

            matrices.push()
            RenderSystem.disableDepthTest()
            RenderSystem.enableBlend()
            RenderSystem.defaultBlendFunc()
            RenderSystem.disableCull()

            camera.pos.run { matrices.translate(-x, -y, -z) }

            render(matrices, tickDelta)

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F)
            VertexBuffer.unbind()
            RenderSystem.enableDepthTest()
            RenderSystem.enableCull()
            RenderSystem.disableBlend()
            matrices.pop()
        }
    }

    override fun allowInput() = Client?.currentScreen == null

}
