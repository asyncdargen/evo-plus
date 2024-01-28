package ru.dargen.evoplus.util.json

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

fun JsonElement.asBoolean(default: Boolean = false) =
    if (this is JsonPrimitive && isBoolean) asBoolean else default

fun JsonElement.asInt(default: Int = 0) =
    if (this is JsonPrimitive && isNumber) asInt else default

fun JsonElement.asDouble(default: Double = .0) =
    if (this is JsonPrimitive && isNumber) asDouble else default

fun JsonElement.asObject() = if (isJsonObject) asJsonObject else null

val JsonElement?.isNull get() = this?.isJsonNull != false

fun json(block: JsonObject.() -> Unit) = JsonObject().apply(block)

fun jsonArray(block: JsonArray.() -> Unit) = JsonArray().apply(block)

val Number.asJsonElement get() = JsonPrimitive(this)

val String.asJsonElement get() = JsonPrimitive(this)

val Boolean.asJsonElement get() = JsonPrimitive(this)

fun <T> JsonElement.json(block: JsonObject.() -> T) = asJsonObject.run(block)

fun JsonObject.forEach(block: (String, JsonElement) -> Unit) = keySet().forEach { block(it, get(it)) }

fun JsonElement.forEachObject(block: (String, JsonElement) -> Unit) = asJsonObject.forEach(block)

fun JsonElement.forEachArray(block: (JsonElement) -> Unit) = asJsonArray.forEach(block)