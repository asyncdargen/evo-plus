package ru.dargen.evoplus.util.collection

fun <T> List<T>.insertAt(index: Int, vararg items: T) = if (index <= 0) items.toList() + this
else if (index >= size) this + items.toList()
else slice(0..<index) + items.toList() + slice(index..<size)