package ru.dargen.evoplus.api.keybind

import net.minecraft.client.option.KeyBinding
import ru.dargen.evoplus.api.event.input.KeyTypeEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.mixin.input.keybind.KeyBindingAccessor

object KeyBindings {

    val BindList = mutableSetOf<KeyBinding>()
    val PressHandlers = mutableMapOf<KeyBinding, () -> Unit>()

    init {
        on<KeyTypeEvent> {
            PressHandlers.forEach { (key, handler) ->
                if (key.isPressed) handler()
            }
        }
    }

    fun add(keyBinding: KeyBinding) {
        BindList.add(keyBinding)

        KeyBindingAccessor.categoryMap().apply {
            if (keyBinding.category in this) return@apply

            this[keyBinding.category] = (values.maxOrNull() ?: 0) + 1
        }
    }

    fun interceptKeys(keys: Array<KeyBinding>) =
        (keys.toList() + BindList).toTypedArray()

}

fun KeyBinding.on(handler: () -> Unit) = KeyBindings.PressHandlers.put(this, handler)

fun keyBind(name: String, code: Int, category: String, register: Boolean = true) =
    KeyBinding(name, code, category).apply { if (register) KeyBindings.add(this) }