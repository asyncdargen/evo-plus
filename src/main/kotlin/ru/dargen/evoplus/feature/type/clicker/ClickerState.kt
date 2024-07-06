package ru.dargen.evoplus.feature.type.clicker

import ru.dargen.evoplus.util.minecraft.ClientExtension

enum class ClickerState(val display: String) {

    LEFT("À Ã") {
        override fun invoke() {
            ClientExtension.leftClick()
        }
    },
    RIGHT("œ Ã") {
        override fun invoke() {
            ClientExtension.rightClick()
        }
    };

    abstract operator fun invoke()

    override fun toString() = display
}