package ru.dargen.evoplus.util

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import kotlin.reflect.KClass

val GsonBuilder = GsonBuilder().setPrettyPrinting()

var Gson = GsonBuilder.create()

fun gson(bake: GsonBuilder.() -> Unit) {
    GsonBuilder.bake()
    Gson = GsonBuilder.create()
}

fun JsonElement.asBoolean(default: Boolean = false) =
    if (this is JsonPrimitive && isBoolean) asBoolean else default

fun JsonElement.asInt(default: Int = 0) =
    if (this is JsonPrimitive && isNumber) asInt else default

fun JsonElement.asDouble(default: Double = .0) =
    if (this is JsonPrimitive && isNumber) asDouble else default

fun JsonElement.asObject() = if (isJsonObject) asJsonObject else null

val JsonElement?.isNull get() = this?.isJsonNull != false

inline fun <reified T : Any> GsonBuilder.adapter(
    token: com.google.common.reflect.TypeToken<T>,
    crossinline serializer: (obj: T, ctx: JsonSerializationContext) -> JsonElement,
    crossinline deserializer: (element: JsonElement, ctx: JsonDeserializationContext) -> T
): GsonBuilder {
    val adapter = object : JsonSerializer<T>, JsonDeserializer<T> {

        override fun serialize(obj: T, type: Type, ctx: JsonSerializationContext) =
            serializer(obj, ctx)

        override fun deserialize(element: JsonElement, type: Type, ctx: JsonDeserializationContext) =
            deserializer(element, ctx)

    }
    registerTypeHierarchyAdapter(token.rawType, adapter)
    return registerTypeAdapter(token.type, adapter)
}

inline fun <reified T : Any> GsonBuilder.adapter(
    clazz: KClass<T>,
    crossinline serializer: (obj: T, ctx: JsonSerializationContext) -> JsonElement,
    crossinline deserializer: (element: JsonElement, ctx: JsonDeserializationContext) -> T
) = adapter(object : com.google.common.reflect.TypeToken<T>(clazz.java) {}, serializer, deserializer)

inline fun <reified T> fromJson(json: String) = Gson.fromJson(json, object : TypeToken<T>() {})

fun toJson(any: Any) = Gson.toJson(any)