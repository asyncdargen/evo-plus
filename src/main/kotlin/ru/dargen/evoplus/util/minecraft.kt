package ru.dargen.evoplus.util

import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.Packet
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.sound.SoundEvent
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.dargen.evoplus.ModLabel
import ru.dargen.evoplus.util.math.Vector3

val Client get() = MinecraftClient.getInstance()

val Player get() = Client.player
val PlayerPosition get() = Player?.pos?.run { Vector3(x, y, z) } ?: Vector3()
val PlayerEyePosition get() = Player?.eyePos?.run { Vector3(x, y, z) } ?: Vector3()

val WindowInitialized get() = Client.window != null
val Window get() = Client.window

val MousePosition
    get() = if (!WindowInitialized) Vector3() else Vector3(
        Client.mouse.x * Window.scaledWidth / Window.width,
        Client.mouse.y * Window.scaledHeight / Window.height,
    ).fixNaN()

fun playSound(event: SoundEvent) = Player?.playSound(event, 1f, 1f)
fun playSound(event: RegistryEntry.Reference<SoundEvent>) = playSound(event.value())

fun printMessage(message: String?) = Player?.sendMessage("$ModLabel§8: §a$message".toText, false)

fun printHoveredCommandMessage(message: String, hover: String, command: String) =
    Text.literal("$ModLabel§8: §a$message").run {
        style = style.withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, hover.toText))
            .withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, command))

        Player?.sendMessage(this, false)
    }

fun sendChatMessage(message: String) =
    Player?.networkHandler?.sendChatMessage(message.replace('§', '&'))

fun sendClanMessage(message: String) = sendChatMessage("@$message")

fun sendCommand(command: String) = Player?.networkHandler?.sendChatCommand(command)

fun sendPacket(packet: Packet<*>) = Player?.networkHandler?.sendPacket(packet)
