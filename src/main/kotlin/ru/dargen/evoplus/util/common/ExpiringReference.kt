package ru.dargen.evoplus.util.common

import ru.dargen.evoplus.util.currentMillis
import java.util.concurrent.TimeUnit
import kotlin.reflect.KProperty

class LazyExpiringReference<V : Any>(val time: Long, val unit: TimeUnit, val supplier: () -> V) {

    var endTimestamp: Long = 0

    var backingRef: V? = null
    var value: V
        set(value) {
            backingRef = value
            updateTimestamp()
        }
        get() {
            if (backingRef == null || currentMillis > endTimestamp) {
                backingRef = supplier()
                updateTimestamp()
            }

            return backingRef!!
        }

    fun updateTimestamp() {
        endTimestamp = currentMillis + unit.toMillis(time)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = value

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: V?) = value?.let { this.value = it }


}