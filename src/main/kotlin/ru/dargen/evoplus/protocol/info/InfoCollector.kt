package ru.dargen.evoplus.protocol.info

import ru.dargen.evoplus.util.json.type
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.kotlin.cast
import java.lang.reflect.Type

@KotlinOpens
class InfoCollector {

    val collectors = mutableMapOf<String, InfoCollectorData<Any>>()

    fun accept(values: Map<String, String>) = collectors.forEach { (name, data) -> values[name]?.let(data::accept) }

}

inline fun <reified T : Any> InfoCollector.collect(
    name: String,
    default: T? = null,
    type: Type = type<T>(),
    noinline block: (T) -> Unit = {}
) = InfoCollectorData(default, type, block).also { collectors[name] = it.cast() }
