package ru.dargen.evoplus.util

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

val Gson = GsonBuilder().setPrettyPrinting().create()

fun JsonElement.asBoolean(default: Boolean = false) =
    if (this is JsonPrimitive && isBoolean) asBoolean else default

fun JsonElement.asInt(default: Int = 0) =
    if (this is JsonPrimitive && isNumber) asInt else default

fun JsonElement.asDouble(default: Double = .0) =
    if (this is JsonPrimitive && isNumber) asDouble else default

fun JsonElement.asObject() = if (isJsonObject) asJsonObject else null

val JsonElement?.isNull get() = this?.isJsonNull != false
