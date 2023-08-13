package ru.dargen.evoplus.api.render.context

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.api.render.node.resize
import ru.dargen.evoplus.util.Client
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.kotlin.safeCast
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.toText

@KotlinOpens
class ScreenContext(id: String, title: String) : RenderContext() {

    val id = id
    val screen = Screen(title)
    var transparent = false
    var displayHandler: ScreenContext.() -> Unit = {}
    var closeHandler: ScreenContext.() -> Unit = {}

    override fun registerRenderHandlers() {
    }

    override fun registerInputHandlers() {

    }

    override fun registerTickHandlers() {

    }

    companion object {

        fun current() = Client?.currentScreen?.safeCast<Screen>()?.context

    }

    inner class Screen(title: String) : net.minecraft.client.gui.screen.Screen(title.toText) {

        val context = this@ScreenContext

        init {
            resize {
                size = Overlay.ScaledResolution
                scale = Overlay.Scale
            }
        }

        override fun init() {
            resize()
        }

        override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
            render(matrices, delta)
        }

        override fun mouseMoved(mouseX: Double, mouseY: Double) {
            mouseMove(v3(mouseX, mouseY))
            super.mouseMoved(mouseX, mouseY)
        }

        override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
            mouseClick(v3(mouseX, mouseY), button, true)
            return super.mouseClicked(mouseX, mouseY, button)
        }

        override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
            mouseClick(v3(mouseX, mouseY), button, false)
            return super.mouseReleased(mouseX, mouseY, button)
        }

        override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
            changeKey(keyCode, true)
            return super.keyPressed(keyCode, scanCode, modifiers)
        }

        override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
            changeKey(keyCode, false)
            return super.keyReleased(keyCode, scanCode, modifiers)
        }

        override fun charTyped(chr: Char, modifiers: Int): Boolean {
            typeChar(chr, 0)
            return super.charTyped(chr, modifiers)
        }

        override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
            mouseWheel(v3(mouseX, mouseY), amount, .0)
            return super.mouseScrolled(mouseX, mouseY, amount)
        }

        override fun tick() {
            preTick()
            postTick()
        }

        override fun onDisplayed() {
            displayHandler()
        }

        override fun removed() {
            closeHandler()
        }

        override fun resize(client: MinecraftClient, width: Int, height: Int) {
            resize()
            super.resize(client, width, height)
        }

    }

    fun display(handler: ScreenContext.() -> Unit) = apply { displayHandler = handler }

    fun destroy(handler: ScreenContext.() -> Unit) = apply { closeHandler = handler }

    fun open() {
        Client?.setScreen(screen)
    }

    fun close() {
        if (Client?.currentScreen == screen) {
            screen.close()
        }
    }

}

fun screen(id: String = "", title: String = "", block: ScreenContext.() -> Unit) =
    ScreenContext(id, title).apply(block)