package ru.dargen.evoplus.util.minecraft

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import net.minecraft.network.packet.Packet
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.sound.SoundEvent
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.Text
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import ru.dargen.evoplus.ModLabel
import ru.dargen.evoplus.mixin.MinecraftClientAccesor
import ru.dargen.evoplus.util.kotlin.cast
import ru.dargen.evoplus.util.kotlin.safeCast
import ru.dargen.evoplus.util.math.Vector3

val Client get() = MinecraftClient.getInstance()
val Player get() = Client?.player
val InteractionManager get() = Client?.interactionManager

val TargetEntity get() = Client?.crosshairTarget?.safeCast<EntityHitResult>()?.entity
val TargetBlock get() = Client?.crosshairTarget?.safeCast<BlockHitResult>()?.blockPos

val CurrentScreenHandler get() = Player?.currentScreenHandler
val CurrentScreen get() = Client?.currentScreen
val CurrentContainer get() = CurrentScreen?.safeCast<GenericContainerScreen>()

val WindowInitialized get() = Client.window != null
val Window get() = Client.window

val MousePosition
    get() = if (!WindowInitialized) Vector3() else Vector3(
        Client.mouse.x * Window.scaledWidth / Window.width,
        Client.mouse.y * Window.scaledHeight / Window.height,
    ).fixNaN()

fun postToMainThread(runnable: () -> Unit) = Client.cast<MinecraftClientAccesor>().renderTaskQueue.add(runnable)

fun playSound(event: SoundEvent) = Player?.playSound(event, 1f, 1f)
fun playSound(event: RegistryEntry.Reference<SoundEvent>) = playSound(event.value())

fun printMessage(message: String?) = Player?.sendMessage("$ModLabel§8: §f$message".asText(), false)

fun printHoveredCommandMessage(message: String, hover: String, command: String) =
    Text.literal("$ModLabel§8: §a$message").run {
        style = style.withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, hover.asText()))
            .withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, command))

        Player?.sendMessage(this, false)
    }

fun sendChatMessage(message: String) =
    Player?.networkHandler?.sendChatMessage(message.replace('§', '&'))

fun sendClanMessage(message: String) = sendChatMessage("@$message")

fun sendCommand(command: String) = Player?.networkHandler?.sendChatCommand(command)

fun sendPacket(packet: Packet<*>) = Player?.networkHandler?.sendPacket(packet)
