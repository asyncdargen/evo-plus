package ru.dargen.evoplus.feature.screen

import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.node.DummyNode
import ru.dargen.evoplus.api.render.node.Node
import ru.dargen.evoplus.api.render.node.scroll.vScrollView
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
class FeatureScreenElements {

    val elements = mutableSetOf<FeatureScreenElement>()
    val elementsSection
        get() = vScrollView {
            box.color = Colors.TransparentBlack
            addElements(
                this@FeatureScreenElements.elements.map(FeatureScreenElement::create).filter { it !== DummyNode })
        }

    fun element(element: FeatureScreenElement) = elements.add(element)

    fun element(block: () -> Node) = object : FeatureScreenElement {
        override fun create() = block()
    }

    fun baseElement(name: String, block: () -> Node) = element(FeatureBaseElement(name, block))

}