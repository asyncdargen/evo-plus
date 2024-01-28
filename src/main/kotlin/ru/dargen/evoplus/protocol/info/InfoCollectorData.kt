package ru.dargen.evoplus.protocol.info

import ru.dargen.evoplus.util.json.Gson
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import java.lang.reflect.Type
import kotlin.reflect.KProperty

@KotlinOpens
class InfoCollectorData<T>(default: T?, val type: Type, val consumer: (T) -> Unit) {

    var lastValue = default

    fun accept(value: String) {
        val deserialized = Gson.fromJson<T>(value, type) ?: return
        consumer(deserialized)
        lastValue = deserialized
    }

    operator fun getValue(thisRef: Any, property: KProperty<*>) = lastValue!!

}