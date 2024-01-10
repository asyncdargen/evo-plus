package ru.dargen.evoplus.protocol.registry

import pro.diamondworld.protocol.util.ProtocolSerializable
import ru.dargen.evoplus.Logger
import ru.dargen.evoplus.protocol.listen
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

@KotlinOpens
class TypeRegistry<K, V, E : TypeRegistry.TypeRegistryEntry<K>, P : ProtocolSerializable>(
    packetType: KClass<P>,
    extractor: (P) -> Map<K, V>,
    converter: (V) -> E
) : ConcurrentHashMap<K, E>() {

    init {
        listen(packetType) {
            val received = extractor(it).mapValues { (key, value) -> converter(value) }
            Logger.info("Received ${toString()}: ${received.size}")
            putAll(received)
            update(received)
        }
    }

    fun update(received: Map<K, E>) {}

    @KotlinOpens
    abstract class TypeRegistryEntry<T>(val id: T) {

        abstract val link: RegistryLink<T, *>

    }

}

@KotlinOpens
class OrdinalRegistry<V, E : TypeRegistry.TypeRegistryEntry<Int>, P : ProtocolSerializable>(
    packetType: KClass<P>,
    extractor: (P) -> Map<Int, V>,
    converter: (V) -> E
) : TypeRegistry<Int, V, E, P>(packetType, extractor, converter) {

    fun byOrdinal(ordinal: Int) = get(ordinal)

}

@KotlinOpens
class EnumRegistry<V, E : TypeRegistry.TypeRegistryEntry<String>, P : ProtocolSerializable>(
    packetType: KClass<P>,
    extractor: (P) -> Map<String, V>,
    converter: (V) -> E
) : TypeRegistry<String, V, E, P>(packetType, extractor, converter) {

    fun valueOf(name: String) = get(name.lowercase())

}

@KotlinOpens
abstract class RegistryLink<K, V>(val id: K) {

    abstract val isPresent: Boolean

    abstract val ref: V?

    override fun hashCode() = id.hashCode()

}