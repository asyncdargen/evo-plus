package ru.dargen.evoplus.feature.type.clicker

enum class ClickerMode(val display: String) {
    
    CLICK("Нажатие"),
    HOLD("Удержание"),
    ;
    
    override fun toString() = display
}