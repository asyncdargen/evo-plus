package ru.dargen.evoplus.render.animation.target

import ru.dargen.evoplus.render.animation.property.AnimationProperty

data class AnimationTarget<C : Any>(
    val property: AnimationProperty<C>,
    val initial: C,
    val destination: C,
    val type: AnimationTargetType<C>
) {

    fun progressTo(progress: Double) {
        property.value = type.progressTo(initial, destination, progress)
    }

}