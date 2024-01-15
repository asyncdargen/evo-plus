package ru.dargen.evoplus.api.event.bossbar

import net.minecraft.client.gui.hud.ClientBossBar
import ru.dargen.evoplus.api.event.Event

data class BossBarRenderEvent(val bossBar: ClientBossBar) : Event