package ru.dargen.evoplus

import ru.dargen.evoplus.util.common.LazyExpiringReference
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit

object PrefixParser {

    private const val PROPERTIES_URL = "https://raw.githubusercontent.com/asyncdargen/evo-plus/kotlin/prefix.properties"

    val Prefixes by LazyExpiringReference(1, TimeUnit.MINUTES) {
        Properties().apply { load(URL(PROPERTIES_URL).openStream()) }
    }
}