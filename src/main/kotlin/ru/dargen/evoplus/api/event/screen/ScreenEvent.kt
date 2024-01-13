package ru.dargen.evoplus.api.event.screen

import net.minecraft.client.gui.screen.Screen
import ru.dargen.evoplus.api.event.CancellableEvent
import ru.dargen.evoplus.api.render.context.ScreenContext
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.kotlin.safeCast

@KotlinOpens
class ScreenEvent(val screen: Screen) : CancellableEvent() {

    val ctx get() = screen.safeCast<ScreenContext.Screen>()?.context

}