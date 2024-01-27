package ru.dargen.evoplus.features.stats.pet

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import ru.dargen.evoplus.protocol.registry.PetType

@Serializable
data class PetInfo(private val pet: JsonObject, val level: Int, val exp: Double, val energy: Double) {

    val id get() = pet["id"]?.jsonPrimitive?.content ?: ""
    val type get() = PetType.valueOf(id)!!

    companion object {

        fun getDummy() = PetType.values
            .random()
            .let {
                PetInfo(
                    JsonObject(
                        mapOf("id" to JsonPrimitive(it.id))
                    ),
                    5,
                    512.0,
                    5.0,
                )
            }
    }
}