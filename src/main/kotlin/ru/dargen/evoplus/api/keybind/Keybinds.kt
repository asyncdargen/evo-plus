package ru.dargen.evoplus.api.keybind

import net.minecraft.client.util.InputUtil

object Keybinds {

    val MenuKey = keyBind("Открыть меню", InputUtil.GLFW_KEY_RIGHT_SHIFT, "EvoPlus")
    val AutoClicker = keyBind("Включить авто-кликер", InputUtil.GLFW_KEY_Z, "EvoPlus")
    val FastSelector = keyBind("Окно быстрого доступа", InputUtil.GLFW_KEY_R, "EvoPlus")
    val FastBossTeleport = keyBind("Быстрый телепорт к боссу", InputUtil.GLFW_KEY_O, "EvoPlus")

    val MoveRuneSetRight = keyBind("Переключить сет рун вправо", InputUtil.GLFW_KEY_D, "EvoPlus")
    val MoveRuneSetLeft = keyBind("Переключить сет рун влево", InputUtil.GLFW_KEY_A, "EvoPlus")
    val SelectRuneSetBinds = (0..<7).map {
        keyBind("Выбрать сет рун ${it + 1}", InputUtil.GLFW_KEY_1 + it, "EvoPlus")
    }

}