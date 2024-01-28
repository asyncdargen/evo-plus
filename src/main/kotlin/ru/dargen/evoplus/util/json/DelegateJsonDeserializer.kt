package ru.dargen.evoplus.util.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import java.lang.reflect.Type

@KotlinOpens
data class DelegateJsonDeserializer<T>(
    val deserializer: (element: JsonElement, ctx: JsonDeserializationContext) -> T
) : JsonDeserializer<T> {

    override fun deserialize(element: JsonElement, type: Type, ctx: JsonDeserializationContext) =
        deserializer(element, ctx)

}