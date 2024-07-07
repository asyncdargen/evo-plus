package ru.dargen.evoplus.api.keybind

import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import ru.dargen.evoplus.api.event.input.KeyTypeEvent
import ru.dargen.evoplus.api.event.input.MouseClickEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.mixin.input.keybind.KeyBindingAccessor
import ru.dargen.evoplus.util.kotlin.cast
import ru.dargen.evoplus.util.log

object KeyBindings {

    val BindList = mutableSetOf<KeyBinding>()
    val PressHandlers = mutableMapOf<KeyBinding, () -> Unit>()

    init {
        on<KeyTypeEvent> {
            PressHandlers
                .filterKeys { it.boundKey.category !== InputUtil.Type.MOUSE }
                .forEach { (key, handler) ->
                    runCatching {
                        if (key.isPressed) handler()
                    }.exceptionOrNull()?.log("Error while processing key handler ${key.translationKey}")
                }
        }
        on<MouseClickEvent> {
            PressHandlers
                .filterKeys { it.boundKey.category === InputUtil.Type.MOUSE }
                .forEach { (key, handler) ->
                    runCatching {
                        if (key.isPressed) handler()
                    }.exceptionOrNull()?.log("Error while processing key handler ${key.translationKey}")
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

val KeyBinding.boundKey get() = cast<KeyBindingAccessor>().boundKey

fun KeyBinding.on(handler: () -> Unit) = KeyBindings.PressHandlers.put(this, handler)

fun keyBind(name: String, code: Int, category: String, register: Boolean = true) =
    KeyBinding(name, code, category).apply { if (register) KeyBindings.add(this) }