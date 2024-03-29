package ru.dargen.evoplus.util.json

import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import java.lang.reflect.Type

@KotlinOpens
data class DelegateJsonSerializer<T>(
    val serializer: (any: T, ctx: JsonSerializationContext) -> JsonElement
) : JsonSerializer<T> {

    override fun serialize(any: T, type: Type, ctx: JsonSerializationContext) = serializer(any, ctx)

}