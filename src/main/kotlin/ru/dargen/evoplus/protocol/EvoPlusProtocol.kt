package ru.dargen.evoplus.protocol

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket
import net.minecraft.util.Identifier
import pro.diamondworld.protocol.ProtocolRegistry
import pro.diamondworld.protocol.packet.ServerInfo
import pro.diamondworld.protocol.util.BufUtil
import pro.diamondworld.protocol.util.ProtocolSerializable
import ru.dargen.evoplus.Logger
import ru.dargen.evoplus.api.event.evo.EvoJoinEvent
import ru.dargen.evoplus.api.event.evo.EvoQuitEvent
import ru.dargen.evoplus.api.event.fire
import ru.dargen.evoplus.api.event.network.ChangeServerEvent
import ru.dargen.evoplus.api.event.network.CustomPayloadEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.scheduler.scheduleEvery
import ru.dargen.evoplus.protocol.registry.AbilityType
import ru.dargen.evoplus.protocol.registry.BossType
import ru.dargen.evoplus.protocol.registry.FishingSpot
import ru.dargen.evoplus.protocol.registry.HourlyQuestType
import ru.dargen.evoplus.protocol.registry.PetType
import ru.dargen.evoplus.protocol.registry.PotionType
import ru.dargen.evoplus.protocol.registry.StaffType
import ru.dargen.evoplus.util.minecraft.Client
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass


typealias Handler<T> = (T) -> Unit
typealias RawHandler = Handler<ByteBuf>

private val Handlers = mutableMapOf<String, RawHandler>()

object EvoPlusProtocol {

    val Registry = ProtocolRegistry()

    var Server = ServerInfo.EMPTY
        set(value) {
            if (value.serverName != field.serverName && "PRISONEVO" in value.serverName) EvoJoinEvent.fire()
            else if ("PRISONEVO" in field.serverName) EvoQuitEvent.fire()

            if (value != ServerInfo.EMPTY) Logger.info("Connected to DiamondWorld ($field)")

            field = value
        }

    init {
        scheduleEvery(unit = TimeUnit.SECONDS) {
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

        listen<ServerInfo> { Server = it }

        initRegistries()
    }

    fun initRegistries() {
        BossType
        StaffType
        PotionType
        PetType
        FishingSpot
        AbilityType
        HourlyQuestType
    }
    
    fun isOnPrisonEvo() = "PRISONEVO" in EvoPlusProtocol.Server.serverName
}

//listen
fun onRaw(channel: String, handler: RawHandler) = Handlers.put(channel, handler)

inline fun <P : ProtocolSerializable> listen(
    packetType: KClass<P>,
    channel: String = EvoPlusProtocol.Registry.lookupOrRegisterChannel(packetType.java),
    crossinline handler: Handler<P>
) = onRaw(channel) { handler(BufUtil.readObject(it, packetType.java)) }

inline fun <reified P : ProtocolSerializable> listen(
    channel: String = EvoPlusProtocol.Registry.lookupOrRegisterChannel(P::class.java),
    crossinline handler: Handler<P>
) = listen(P::class, channel, handler)

//send
inline fun <reified P : ProtocolSerializable> send(
    channel: String = EvoPlusProtocol.Registry.lookupOrRegisterChannel(P::class.java),
    packet: P
) = sendRaw(channel) { BufUtil.writeObject(this, packet) }

fun sendDummy(channel: String) = sendRaw(channel, Unpooled.buffer())

fun sendRaw(channel: String, block: ByteBuf.() -> Unit) = sendRaw(channel, Unpooled.buffer().apply(block))

fun sendRaw(channel: String, buf: ByteBuf) {
    Client?.networkHandler?.sendPacket(CustomPayloadC2SPacket(Identifier("dw", channel), PacketByteBuf(buf)))
}