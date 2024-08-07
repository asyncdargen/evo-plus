package ru.dargen.evoplus.features.clicker

enum class ClickerMode(val display: String) {
    
    CLICK("Нажатие"),
    HOLD("Удержание"),
    ;
    
    override fun toString() = display
}