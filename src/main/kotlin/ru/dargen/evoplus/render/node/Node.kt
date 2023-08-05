package ru.dargen.evoplus.render.node

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.render.Colors
import ru.dargen.evoplus.render.Relative
import ru.dargen.evoplus.render.animation.property.proxied
import ru.dargen.evoplus.render.context.OverlayContext
import ru.dargen.evoplus.util.Window
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.kotlin.cast
import ru.dargen.evoplus.util.math.Vector3
import ru.dargen.evoplus.util.render.rotate
import ru.dargen.evoplus.util.render.scale
import ru.dargen.evoplus.util.render.translate
import java.awt.Color

typealias KeyHandler<N> = N.(key: Int, state: Boolean) -> Unit
typealias CharHandler<N> = N.(char: Char, code: Int) -> Unit

typealias MouseWheelHandler<N> = N.(mouse: Vector3, verticalWheel: Double, horizontalWheel: Double) -> Unit
typealias MouseClickHandler<N> = N.(mouse: Vector3, button: Int, state: Boolean) -> Unit
typealias MouseMoveHandler<N> = N.(mouse: Vector3) -> Unit

typealias HoverHandler<N> = N.(mouse: Vector3, state: Boolean) -> Unit
typealias TickHandler<N> = N.() -> Unit

@KotlinOpens
abstract class Node {

    //position
    var position by proxied(Vector3())
    var translation by proxied(Vector3())
    var origin by proxied(Relative.LeftTop)
    var align by proxied(Relative.LeftTop)

    //visual properties
    var size by proxied(Vector3())
    var scale by proxied(Vector3(1.0))
    var rotation by proxied(Vector3())

    var color by proxied<Color>(Colors.Transparent)

    //node properties
    var children = mutableSetOf<Node>()
    val enabledChildren get() = children.asSequence().filter(Node::enabled)
    var parent: Node? = null

    var scissorIndent by proxied(Vector3())
    var isScissor = false
    var enabled = true
    var isHovered = false

    val clickHandlers = mutableSetOf<MouseClickHandler<Node>>()
    val wheelHandlers = mutableSetOf<MouseWheelHandler<Node>>()
    val moveHandlers = mutableSetOf<MouseMoveHandler<Node>>()

    val keyHandlers = mutableSetOf<KeyHandler<Node>>()
    val charHandlers = mutableSetOf<CharHandler<Node>>()

    val hoverHandlers = mutableSetOf<HoverHandler<Node>>()
    val preTickHandlers = mutableSetOf<TickHandler<Node>>()
    val postTickHandlers = mutableSetOf<TickHandler<Node>>()

    //wholes
    val wholePosition
        get() = (parent?._wholePosition() ?: Vector3()).plus(
            (parent?.size?.clone()?.times(align) ?: Vector3())
                .plus(translation).plus((!size).times(origin))
                .times(wholeScale)
        )
    val wholeScale get() = (parent?.scale ?: Vector3(1.0)) * scale
    val wholeRotation get() = (parent?.rotation ?: Vector3()) + rotation
    val initialParent: Node get() = parent?.initialParent ?: this
    val wholeSize get() = size * wholeScale

    //dispatchers
    fun preTick() {
        preTickHandlers.forEach { it() }
        children.forEach { it.preTick() }
    }

    fun postTick() {
        postTickHandlers.forEach { it() }
        children.forEach { it.postTick() }
    }

    fun mouseMove(mouse: Vector3) {
        if (!enabled) return
        updateHover(mouse)
        moveHandlers.forEach { it(mouse) }
        children.forEach { it.mouseMove(mouse) }
    }

    fun mouseClick(mouse: Vector3, button: Int, state: Boolean) {
        if (!enabled) return
        clickHandlers.forEach { it(mouse, button, state) }
        children.forEach { it.mouseClick(mouse, button, state) }
    }

    fun mouseWheel(mouse: Vector3, verticalWheel: Double, horizontalWheel: Double) {
        if (!enabled) return
        wheelHandlers.forEach { it(mouse, verticalWheel, horizontalWheel) }
        children.forEach { it.mouseWheel(mouse, verticalWheel, horizontalWheel) }
    }

    fun changeKey(key: Int, state: Boolean) {
        if (!enabled) return
        keyHandlers.forEach { it(key, state) }
        children.forEach { it.changeKey(key, state) }
    }

    fun typeChar(char: Char, code: Int) {
        if (!enabled) return
        charHandlers.forEach { it(char, code) }
        children.forEach { it.typeChar(char, code) }
    }


    fun updateHover(mouse: Vector3) {
        val positionStart = wholePosition.apply { z = .0 }
        val positionEnd = (positionStart + size * wholeScale).apply { z = .0 }

        val hovered = mouse.isBetween(positionStart, positionEnd)

        if (hovered == isHovered) return
        else hoverHandlers.forEach { it(mouse, hovered) }

        isHovered = hovered

        children.forEach { it.updateHover(mouse) }
    }

    fun render(matrices: MatrixStack, tickDelta: Float) {
        if (!enabled) return
        matrices.push()

        parent?.let { matrices.translate(it.size, align) }
        matrices.translate(translation)
        matrices.translate(position)

        matrices.scale(scale)

        matrices.rotate(rotation)

        matrices.translate(size, !origin)

        if (isScissor) {
            val position = (wholePosition + scissorIndent * wholeScale) * OverlayContext.ScaleFactor
            val size = (wholeSize - scissorIndent * wholeScale) * OverlayContext.ScaleFactor
            RenderSystem.enableScissor(
                position.x.toInt(), (Window.framebufferHeight - position.y - size.y.toInt()).toInt(),
                size.x.toInt(), size.y.toInt()
            )
        }

        renderElement(matrices, tickDelta)
        children.forEach { it.render(matrices, tickDelta) }

        if (isScissor) {
            RenderSystem.disableScissor()
        }

        matrices.pop()
    }

    abstract fun renderElement(matrices: MatrixStack, tickDelta: Float)

    //children
    fun addChildren(vararg children: Node) {
        children.forEach { child ->
            if (child.parent !== this) {
                child.parent?.removeChildren(child)
            }

            child.parent = this
            this.children.add(child)
        }
    }

    fun removeChildren(vararg children: Node) {
        children.forEach {
            it.parent = null
            this.children.remove(it)
        }
    }

    operator fun <N : Node> N.unaryPlus() = apply { this@Node.addChildren(this) }

    operator fun <N : Node> N.unaryMinus() = apply { this@Node.removeChildren(this) }

}

//hover
inline infix fun <N : Node> N.hover(noinline handler: HoverHandler<N>) = apply { hoverHandlers.add(handler.cast()) }

inline infix fun <N : Node> N.hoverIn(noinline handler: HoverHandler<N>) =
    hover { mouse, state -> if (state) handler(mouse, state) }

inline infix fun <N : Node> N.hoverOut(noinline handler: HoverHandler<N>) =
    hover { mouse, state -> if (!state) handler(mouse, state) }

//wheel

inline infix fun <N : Node> N.wheel(noinline handler: MouseWheelHandler<N>) =
    apply { wheelHandlers.add(handler.cast()) }

inline infix fun <N : Node> N.hWheel(noinline handler: N.(mouse: Vector3, wheel: Double) -> Unit) =
    wheel { mouse, _, horizontalWheel -> handler(mouse, horizontalWheel) }

inline infix fun <N : Node> N.vWheel(noinline handler: N.(mouse: Vector3, wheel: Double) -> Unit) =
    wheel { mouse, verticalWheel, _ -> handler(mouse, verticalWheel) }

//click

inline infix fun <N : Node> N.click(noinline handler: MouseClickHandler<N>) =
    apply { clickHandlers.add(handler.cast()) }

inline fun <N : Node> N.click(_button: Int, noinline handler: N.(mouse: Vector3, state: Boolean) -> Unit) =
    click { mouse, button, state -> if (button == _button) handler(this, mouse, state) }

inline infix fun <N : Node> N.rightClick(noinline handler: N.(mouse: Vector3, state: Boolean) -> Unit) =
    click(1, handler)

inline infix fun <N : Node> N.leftClick(noinline handler: N.(mouse: Vector3, state: Boolean) -> Unit) =
    click(0, handler)

//move

inline fun <N : Node> N.drag(_button: Int? = null, noinline handler: N.(delta: Vector3) -> Unit) = apply {
    var startPosition = Vector3()
    var dragged = false

    click { mouse, button, state ->
        if (_button != null && button != _button) return@click

        if (isHovered && state) {
            dragged = true
            startPosition = mouse.clone() * 1.00000001
        } else {
            dragged = false
        }
    }
    mouseMove {
        if (dragged) {
            handler(it - startPosition)
        }
    }
}

inline infix fun <N : Node> N.mouseMove(noinline handler: MouseMoveHandler<N>) =
    apply { moveHandlers.add(handler.cast()) }

//type

inline infix fun <N : Node> N.key(noinline handler: KeyHandler<N>) = apply { keyHandlers.add(handler.cast()) }

inline fun <N : Node> N.key(_key: Int, noinline handler: N.(state: Boolean) -> Unit) =
    key { key, state -> if (key == _key) handler(this, state) }

inline infix fun <N : Node> N.releaseKey(noinline handler: N.(key: Int) -> Unit) =
    key { key, state -> if (!state) handler(key) }

inline fun <N : Node> N.releaseKey(_key: Int, noinline handler: N.() -> Unit) =
    releaseKey { if (it == _key) handler() }

inline infix fun <N : Node> N.typeKey(noinline handler: N.(key: Int) -> Unit) =
    key { key, state -> if (state) handler(key) }

inline fun <N : Node> N.typeKey(_key: Int, noinline handler: N.() -> Unit) =
    typeKey { if (it == _key) handler() }

inline infix fun <N : Node> N.type(noinline handler: CharHandler<N>) =
    apply { charHandlers.add(handler.cast()) }

inline fun <N : Node> N.type(_char: Char, noinline handler: N.() -> Unit) =
    type { char, _ -> if (char == _char) handler() }

//tick

inline infix fun <N : Node> N.preTick(noinline handler: TickHandler<N>) = apply { preTickHandlers.add(handler.cast()) }

inline infix fun <N : Node> N.postTick(noinline handler: TickHandler<N>) =
    apply { postTickHandlers.add(handler.cast()) }

inline infix fun <N : Node> N.tick(noinline handler: TickHandler<N>) = postTick(handler)


private var _wholePosition: Node.() -> Vector3 = { wholePosition }

operator fun Node.plusAssign(node: Node) = addChildren(node)

operator fun Node.minusAssign(node: Node) = removeChildren(node)

operator fun <N : Node> N.plus(node: Node) = node.apply { this@plus.addChildren(this) }

operator fun <N : Node> N.minus(node: Node) = node.apply { this@minus.removeChildren(this) }