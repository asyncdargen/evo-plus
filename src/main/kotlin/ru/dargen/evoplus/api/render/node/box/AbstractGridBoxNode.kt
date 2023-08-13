package ru.dargen.evoplus.api.render.node.box

import ru.dargen.evoplus.api.render.animation.property.proxied
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.Vector3

@KotlinOpens
abstract class AbstractGridBoxNode : BoxNode() {

    var dependSize = true
    var fixChildSize = false

    var childrenRelative by proxied(.0)
    var space by proxied(5.0)
    var indent by proxied(Vector3(5.0))

    abstract override fun recompose()

}