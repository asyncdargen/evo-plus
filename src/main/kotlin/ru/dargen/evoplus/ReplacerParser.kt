package ru.dargen.evoplus

import ru.dargen.evoplus.api.scheduler.scheduleEvery
import java.io.InputStreamReader
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit

object ReplacerParser {

    private const val PREFIX_PROPERTIES_URL =
        "https://raw.githubusercontent.com/asyncdargen/evo-plus/kotlin/data/prefix.properties"

    val Replacer = Properties()
    var BakedPattern = "^$".toRegex()
        private set
    val ReplaceCache: MutableMap<String, String> = hashMapOf()

    init {
        scheduleEvery(0, 1, unit = TimeUnit.MINUTES) {
            Replacer.clear()
            Replacer.load(
                InputStreamReader(
                    URL(PREFIX_PROPERTIES_URL).openConnection().apply { useCaches = false }.getInputStream(),
                    Charsets.UTF_8
                )
            )

            ReplaceCache.clear()
            bakePattern()
        }
    }

    fun replace(text: String) = text.replace(BakedPattern) {
        val value = it.value
        ReplaceCache.getOrPut(value) { Replacer.getProperty(value).replace("%text%", value) }
    }

    private fun bakePattern() {
        BakedPattern = "(${Replacer.keys().asSequence().joinToString("|")})".toRegex()
    }

}