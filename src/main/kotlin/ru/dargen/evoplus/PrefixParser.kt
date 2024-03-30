package ru.dargen.evoplus

import ru.dargen.evoplus.api.scheduler.schedule
import java.io.InputStreamReader
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit

object PrefixParser {

    private const val PROPERTIES_URL =
        "https://raw.githubusercontent.com/asyncdargen/evo-plus/kotlin/data/prefix.properties"

    val Prefixes = Properties()

    init {
        schedule(1, TimeUnit.MINUTES) {
            Prefixes.clear()
            Prefixes.load(
                InputStreamReader(
                    URL(PROPERTIES_URL).openConnection().apply { useCaches = false }.getInputStream(),
                    Charsets.UTF_8
                )
            )
        }
    }
}