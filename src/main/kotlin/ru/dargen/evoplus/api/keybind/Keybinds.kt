package ru.dargen.evoplus.api.keybind

import net.minecraft.client.util.InputUtil

object Keybinds {

    val MenuKey = keyBind("Открыть меню", InputUtil.GLFW_KEY_RIGHT_SHIFT, "EvoPlus")
    val FastSelector = keyBind("Окно быстрого доступа", InputUtil.GLFW_KEY_R, "EvoPlus")
    val Clicker = keyBind("Авто-кликер", InputUtil.GLFW_KEY_X, "EvoPlus")

}