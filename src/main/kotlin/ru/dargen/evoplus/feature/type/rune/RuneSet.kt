package ru.dargen.evoplus.feature.type.rune

data class RuneSet(val id: Int, val name: String, val runes: List<String>) {

    val nextId get() = if (id == 6) 0 else id + 1

}