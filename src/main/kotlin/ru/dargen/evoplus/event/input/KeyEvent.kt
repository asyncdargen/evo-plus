package ru.dargen.evoplus.event.input

import ru.dargen.evoplus.event.Event
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
data class KeyEvent(val key: Int, val state: Boolean) : Event

class KeyReleaseEvent(code: Int) : KeyEvent(code, false)

class KeyTypeEvent(code: Int) : KeyEvent(code, true)

class KeyCharEvent(val char: Char, code: Int) : KeyEvent(code, true)