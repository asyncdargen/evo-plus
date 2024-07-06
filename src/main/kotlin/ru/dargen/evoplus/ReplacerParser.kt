package ru.dargen.evoplus

import ru.dargen.evoplus.api.scheduler.scheduleEvery
import java.io.InputStreamReader
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit

object ReplacerParser {

    private const val PREFIX_PROPERTIES_URL = "https://raw.githubusercontent.com/asyncdargen/evo-plus/kotlin/data/prefix.properties"

    val Replacer = Properties()

    init {
        scheduleEvery(0, 1, unit = TimeUnit.MINUTES) {
            Replacer.clear()
            Replacer.load(
                InputStreamReader(
                    URL(PREFIX_PROPERTIES_URL).openConnection().apply { useCaches = false }.getInputStream(),
                    Charsets.UTF_8
                )
            )
        }
    }
}