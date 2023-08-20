package ru.dargen.evoplus.api.event.render

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
class ScreenRenderEvent(val screen: Screen, matrices: MatrixStack, tickDelta: Float) : RenderEvent(matrices, tickDelta) {

    @KotlinOpens
    class Pre(screen: Screen, matrices: MatrixStack, tickDelta: Float) : ScreenRenderEvent(screen, matrices, tickDelta)
    @KotlinOpens
    class Post(screen: Screen, matrices: MatrixStack, tickDelta: Float) : ScreenRenderEvent(screen, matrices, tickDelta)

}