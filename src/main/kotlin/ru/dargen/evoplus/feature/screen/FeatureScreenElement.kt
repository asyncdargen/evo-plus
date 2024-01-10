package ru.dargen.evoplus.feature.screen

import ru.dargen.evoplus.api.render.node.DummyNode
import ru.dargen.evoplus.api.render.node.Node

interface FeatureScreenElement {

    object Dummy : FeatureScreenElement {
        override fun create() = DummyNode
    }

    fun create(): Node

}