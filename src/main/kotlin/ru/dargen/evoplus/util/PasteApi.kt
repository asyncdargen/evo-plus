package ru.dargen.evoplus.util

import com.google.gson.JsonObject
import ru.dargen.evoplus.util.kotlin.cast
import java.net.HttpURLConnection
import java.net.URL

object PasteApi {

    val ApiUrl = "https://paste.xtrafrancyz.net/"

    private fun request(method: String, vararg paths: String) = URL("$ApiUrl${paths.joinToString("/")}")
        .openConnection()
        .cast<HttpURLConnection>()
        .apply { requestMethod = method }

    fun paste(content: String) = request("POST", "documents").runCatching {
        doOutput = true
        doInput = true

        outputStream.use { it.write(content.toByteArray()) }
        val rawResponse = String(inputStream.readAllBytes())
        val response = Gson.fromJson(rawResponse, JsonObject::class.java)
        response["key"].asString
    }.apply { exceptionOrNull()?.log("Error while pasting to paste-service") }.getOrNull()

    fun copy(key: String) = request("GET", "raw", key).runCatching {
        doInput = true
        String(inputStream.readAllBytes())
    }.apply { exceptionOrNull()?.log("Error while parsing $key from paste-service") }.getOrNull()

}