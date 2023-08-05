package ru.dargen.evoplus.event.chat

import net.minecraft.text.Text

class OverlayEvent(message: Text) : ChatReceiveEvent(message, true)