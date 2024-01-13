package ru.dargen.evoplus.api.event.screen

import net.minecraft.client.gui.screen.Screen
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
class ScreenOpenEvent(screen: Screen, val oldScreen: Screen?) : ScreenEvent(screen)