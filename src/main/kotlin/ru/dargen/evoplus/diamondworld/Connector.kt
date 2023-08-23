package ru.dargen.evoplus.diamondworld

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket
import net.minecraft.util.Identifier
import pro.diamondworld.protocol.ProtocolRegistry
import pro.diamondworld.protocol.packet.LevelInfo
import pro.diamondworld.protocol.packet.ServerInfo
import pro.diamondworld.protocol.util.BufUtil
import pro.diamondworld.protocol.util.ProtocolSerializable
import ru.dargen.evoplus.Logger
import ru.dargen.evoplus.api.event.EventBus
import ru.dargen.evoplus.api.event.evo.EvoJoinEvent
import ru.dargen.evoplus.api.event.evo.EvoQuitEvent
import ru.dargen.evoplus.api.event.game.ChangeServerEvent
import ru.dargen.evoplus.api.event.game.CustomPayloadEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.util.concurrent.every
import ru.dargen.evoplus.util.minecraft.Client

typealias Handler<T> = (T) -> Unit
typealias RawHandler = Handler<ByteBuf>

object Connector {

    val Registry = ProtocolRegistry()
    private val Handlers = mutableMapOf<String, RawHandler>()

    var Server = ServerInfo.EMPTY
        set(value) {
            if (value.serverName != field.serverName && "PRISONEVO" in value.serverName) {
                EventBus.fire(EvoJoinEvent)
            } else if ("PRISONEVO" in field.serverName) {
                EventBus.fire(EvoQuitEvent)
            }

            if (value != ServerInfo.EMPTY) {
                Logger.info("Connected to DiamondWorld ($field)")
            }

            field = value
        }

    init {
        every(1000) {
            if (Server == ServerInfo.EMPTY) {
                sendDummy("handshake")
            }
        }

        on<ChangeServerEvent> { Server = ServerInfo.EMPTY }
        on<CustomPayloadEvent> {
            if (!channel.startsWith("dw")) return@on
            val channel = channel.drop(3)

            Handlers[channel]?.invoke(payload)

            cancel()
        }

        on<ServerInfo> { Server = it }

        on<LevelInfo> {
//            printMessage("Next level requirements is $it")
        }
    }

    //listen
    fun onRaw(channel: String, handler: RawHandler) = Handlers.put(channel, handler)

    inline fun <reified P : ProtocolSerializable> on(
        channel: String = Registry.lookupOrRegisterChannel(P::class.java),
        crossinline handler: Handler<P>
    ) = onRaw(channel) { handler(BufUtil.readObject(it, P::class.java)) }

    //send
    inline fun <reified P : ProtocolSerializable> send(
        channel: String = Registry.lookupOrRegisterChannel(P::class.java),
        packet: P
    ) = sendRaw(channel) { BufUtil.writeObject(this, packet) }

    fun sendDummy(channel: String) = sendRaw(channel, Unpooled.buffer())

    fun sendRaw(channel: String, block: ByteBuf.() -> Unit) = sendRaw(channel, Unpooled.buffer().apply(block))

    fun sendRaw(channel: String, buf: ByteBuf) {
        Client?.networkHandler?.sendPacket(CustomPayloadC2SPacket(Identifier("dw",  channel), PacketByteBuf(buf)))
    }

}