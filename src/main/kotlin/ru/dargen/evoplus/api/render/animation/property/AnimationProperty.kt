package ru.dargen.evoplus.api.render.animation.property

import ru.dargen.evoplus.api.render.animation.target.AnimationTargetType
import ru.dargen.evoplus.api.render.animation.AnimationContext
import ru.dargen.evoplus.api.render.animation.target.AnimationTarget
import ru.dargen.evoplus.api.render.node.Node
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@KotlinOpens
class AnimationProperty<T : Any>(
    val targetType: AnimationTargetType<T>,
    initial: T
) : ReadWriteProperty<Node, T> {

    var value: T = initial

    final override fun getValue(thisRef: Node, property: KProperty<*>): T {
        return value
    }

    final override fun setValue(thisRef: Node, property: KProperty<*>, value: T) {
        update(value)
    }

    fun update(value: T) {
        if (AnimationContext.hasContext()) {
            AnimationContext.current().target(buildTargetTo(value))
        } else this.value = value
    }

    fun buildTargetTo(value: T) = AnimationTarget(this, this.value, value, targetType)

}

inline fun <reified T : Any> proxied(initial: T) =
    AnimationProperty(AnimationTargetType.forClass(T::class.java), initial)