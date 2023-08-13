package ru.dargen.evoplus.api.render.node

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.animation.property.proxied
import ru.dargen.evoplus.api.render.context.Overlay
import ru.dargen.evoplus.util.Window
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.kotlin.cast
import ru.dargen.evoplus.util.math.Vector3
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.render.rotate
import ru.dargen.evoplus.util.render.scale
import ru.dargen.evoplus.util.render.translate
import java.awt.Color

typealias RenderHandler<N> = N.(matrices: MatrixStack, tickDelta: Float) -> Unit

typealias KeyHandler<N> = N.(key: Int, state: Boolean) -> Unit
typealias CharHandler<N> = N.(char: Char, code: Int) -> Unit

typealias MouseWheelHandler<N> = N.(mouse: Vector3, verticalWheel: Double, horizontalWheel: Double) -> Unit
typealias MouseClickHandler<N> = N.(mouse: Vector3, button: Int, state: Boolean) -> Unit
typealias MouseMoveHandler<N> = N.(mouse: Vector3) -> Unit

typealias ResizeHandler<N> = N.() -> Unit
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
    var _children = mutableSetOf<Node>()
    val children get() = _children.toSet()

    val enabledChildren get() = children.asSequence().filter(Node::enabled)
    var parent: Node? = null

    var scissorIndent by proxied(Vector3())
    var isScissor = false
    var enabled = true
    var isHovered = false

    val preTransformHandlers = mutableSetOf<RenderHandler<Node>>()
    val postTransformHandlers = mutableSetOf<RenderHandler<Node>>()
    val preRenderHandlers = mutableSetOf<RenderHandler<Node>>()
    val postRenderHandlers = mutableSetOf<RenderHandler<Node>>()

    val clickHandlers = mutableSetOf<MouseClickHandler<Node>>()
    val wheelHandlers = mutableSetOf<MouseWheelHandler<Node>>()
    val moveHandlers = mutableSetOf<MouseMoveHandler<Node>>()

    val keyHandlers = mutableSetOf<KeyHandler<Node>>()
    val charHandlers = mutableSetOf<CharHandler<Node>>()

    val resizeHandlers = mutableSetOf<ResizeHandler<Node>>()
    val hoverHandlers = mutableSetOf<HoverHandler<Node>>()
    val preTickHandlers = mutableSetOf<TickHandler<Node>>()
    val postTickHandlers = mutableSetOf<TickHandler<Node>>()

    //wholes
//    val wholePosition
//        get() = (parent?._wholePosition() ?: Vector3()).plus(
//            (parent?.size?.clone()?.times(align) ?: Vector3())
//                .plus(translation).plus(position)
//                .plus((!size).times(origin)).times(wholeScale)
//        )
    val wholePosition
        get() = (parent?._wholePosition() ?: Vector3()).plus(
            (parent?.size?.clone()?.times(align) ?: Vector3())
                .plus(translation).plus(position).times(wholeScale / scale)
                .plus((!size).times(origin).times(wholeScale))
        )
    val wholeScale get() = (parent?._wholeScale() ?: Vector3(1.0)) * scale
    val wholeRotation get() = (parent?.rotation ?: Vector3()) + rotation
    val wholeSize get() = size * wholeScale

    val initialParent: Node get() = parent?.initialParent ?: this

    //dispatchers
    fun preTick() {
        preTickHandlers.forEach { it() }
        children.forEach { it.preTick() }
    }

    fun postTick() {
        postTickHandlers.forEach { it() }
        children.forEach { it.postTick() }
    }

    fun resize() {
        resizeHandlers.forEach { it() }
        children.forEach { it.resize() }
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

        isHovered = hovered
        hoverHandlers.forEach { it(mouse, hovered) }

        children.forEach { it.updateHover(mouse) }
    }

    fun render(matrices: MatrixStack, tickDelta: Float) {
        if (!enabled) return
        matrices.push()
        preTransformHandlers.forEach { it(matrices, tickDelta) }

        parent?.let { matrices.translate(it.size, align) }
        matrices.translate(translation)
        matrices.translate(position)

        matrices.scale(scale)

        matrices.rotate(rotation)

        matrices.translate(size, !origin)

        preRenderHandlers.forEach { it(matrices, tickDelta) }

        renderElement(matrices, tickDelta)

        if (isScissor) {
            val position = (wholePosition + scissorIndent * wholeScale) * Overlay.ScaleFactor
            val size = (wholeSize - scissorIndent * 2.0 * wholeScale) * Overlay.ScaleFactor

            RenderSystem.enableScissor(
                position.x.toInt(), (Window.framebufferHeight - position.y - size.y.toInt()).toInt(),
                size.x.toInt(), size.y.toInt()
            )
        }

        children.forEach { it.render(matrices, tickDelta) }

        postRenderHandlers.forEach { it(matrices, tickDelta) }

        if (isScissor) {
            RenderSystem.disableScissor()
        }

        postTransformHandlers.forEach { it(matrices, tickDelta) }
        matrices.pop()
    }

    abstract fun renderElement(matrices: MatrixStack, tickDelta: Float)

    //children
    fun addChildren(children: Collection<Node>) {
        children.forEach { child ->
            if (child.parent !== this) {
                child.parent?.removeChildren(child)
            }
            child.resize()

            child.parent = this
            this._children.add(child)
        }
    }

    fun addChildren(vararg children: Node) = addChildren(children.toList())

    fun removeChildren(children: Collection<Node>) {
        children.forEach {
            it.parent = null
            it.resize()
            this._children.remove(it)
        }
    }

    fun removeChildren(vararg children: Node) =
        removeChildren(children.toList())

    operator fun <N : Node> N.unaryPlus() = apply { this@Node.addChildren(this) }

    operator fun <N : Node> N.unaryMinus() = apply { this@Node.removeChildren(this) }

}

//hover
infix fun <N : Node> N.hover(handler: HoverHandler<N>) = apply { hoverHandlers.add(handler.cast()) }

infix fun <N : Node> N.hoverIn(handler: N.(mouse: Vector3) -> Unit) =
    hover { mouse, state -> if (state) handler(mouse) }

infix fun <N : Node> N.hoverOut(handler: N.(mouse: Vector3) -> Unit) =
    hover { mouse, state -> if (!state) handler(mouse) }

//wheel

infix fun <N : Node> N.wheel(handler: MouseWheelHandler<N>) =
    apply { wheelHandlers.add(handler.cast()) }

infix fun <N : Node> N.hWheel(handler: N.(mouse: Vector3, wheel: Double) -> Unit) =
    wheel { mouse, _, horizontalWheel -> handler(mouse, horizontalWheel) }

infix fun <N : Node> N.vWheel(handler: N.(mouse: Vector3, wheel: Double) -> Unit) =
    wheel { mouse, verticalWheel, _ -> handler(mouse, verticalWheel) }

//click

infix fun <N : Node> N.click(handler: MouseClickHandler<N>) =
    apply { clickHandlers.add(handler.cast()) }

fun <N : Node> N.click(_button: Int, handler: N.(mouse: Vector3, state: Boolean) -> Unit) =
    click { mouse, button, state -> if (button == _button) handler(this, mouse, state) }

infix fun <N : Node> N.rightClick(handler: N.(mouse: Vector3, state: Boolean) -> Unit) =
    click(1, handler)

infix fun <N : Node> N.leftClick(handler: N.(mouse: Vector3, state: Boolean) -> Unit) =
    click(0, handler)

//move

fun <N : Node> N.drag(
    _button: Int? = null,
    inOutHandler: N.(dragged: Boolean) -> Unit = {},
    handler: N.(startPosition: Vector3, delta: Vector3) -> Unit = { _, _ -> }
) = apply {
    var startPosition = Vector3()
    var dragged = false

    click { mouse, button, state ->
        if (_button != null && button != _button) return@click

        if (isHovered && state) {
            dragged = true
            inOutHandler(dragged)
            handler(startPosition, 0.v3)
            startPosition = mouse.clone()
        } else {
            dragged = false
            inOutHandler(dragged)
        }
    }
    mouseMove {
        if (dragged) {
            handler(startPosition, it - startPosition)
        }
    }
}

infix fun <N : Node> N.mouseMove(handler: MouseMoveHandler<N>) =
    apply { moveHandlers.add(handler.cast()) }

//type

infix fun <N : Node> N.key(handler: KeyHandler<N>) = apply { keyHandlers.add(handler.cast()) }

fun <N : Node> N.key(_key: Int, handler: N.(state: Boolean) -> Unit) =
    key { key, state -> if (key == _key) handler(this, state) }

infix fun <N : Node> N.releaseKey(handler: N.(key: Int) -> Unit) =
    key { key, state -> if (!state) handler(key) }

fun <N : Node> N.releaseKey(_key: Int, handler: N.() -> Unit) =
    releaseKey { if (it == _key) handler() }

infix fun <N : Node> N.typeKey(handler: N.(key: Int) -> Unit) =
    key { key, state -> if (state) handler(key) }

fun <N : Node> N.typeKey(_key: Int, handler: N.() -> Unit) =
    typeKey { if (it == _key) handler() }

infix fun <N : Node> N.type(handler: CharHandler<N>) =
    apply { charHandlers.add(handler.cast()) }

fun <N : Node> N.type(_char: Char, handler: N.() -> Unit) =
    type { char, _ -> if (char == _char) handler() }

//tick

infix fun <N : Node> N.preTick(handler: TickHandler<N>) = apply { preTickHandlers.add(handler.cast()) }

infix fun <N : Node> N.postTick(handler: TickHandler<N>) =
    apply { postTickHandlers.add(handler.cast()) }

infix fun <N : Node> N.tick(handler: TickHandler<N>) = postTick(handler)

infix fun <N : Node> N.resize(handler: ResizeHandler<N>) = apply { resizeHandlers.add(handler.cast()) }

infix fun <N : Node> N.preTransform(handler: RenderHandler<N>) = apply { preTransformHandlers.add(handler.cast()) }

infix fun <N : Node> N.postTransform(handler: RenderHandler<N>) = apply { postTransformHandlers.add(handler.cast()) }

infix fun <N : Node> N.preRender(handler: RenderHandler<N>) = apply { preRenderHandlers.add(handler.cast()) }

infix fun <N : Node> N.postRender(handler: RenderHandler<N>) = apply { postRenderHandlers.add(handler.cast()) }

private var _wholePosition: Node.() -> Vector3 = { wholePosition }
private var _wholeScale: Node.() -> Vector3 = { wholeScale }

operator fun Node.plusAssign(node: Node) = addChildren(node)

operator fun Node.minusAssign(node: Node) = removeChildren(node)

operator fun <N : Node> Node.plus(node: N) = node.apply { this@plus.addChildren(this) }

operator fun <N : Node> Node.minus(node: N) = node.apply { this@minus.removeChildren(this) }