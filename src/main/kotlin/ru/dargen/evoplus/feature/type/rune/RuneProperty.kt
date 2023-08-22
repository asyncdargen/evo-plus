package ru.dargen.evoplus.feature.type.rune

data class RuneProperty(val name: String, val type: Type, var value: Double = .0) {

    val valueColor get() = if (value < 0) "§c" else "§a"
    val formattedValue get() = "$valueColor${type.formatter(this)}".replace(",", ".")

    override fun toString() = "$name: $formattedValue"

    enum class Type(
        val pattern: Regex,
        val appender: RuneProperty.(matcher: MatchResult) -> Unit,
        val formatter: RuneProperty.() -> String
    ) {

        PRESENCE("^\\+$".toRegex(), {}, { "+" }),
        INCREASE(
            "^((?:[-+]|)[.\\d]+)$".toRegex(),
            { value += it.groupValues[1].toDouble() },
            { "${if (value >= 0) "+" else ""}$value" }),
        PERCENTAGE(
            "^((?:[-+]|)[.\\d]+)%$".toRegex(),
            { value += it.groupValues[1].toDouble() },
            { "${if (value >= 0) "+" else ""}$value%" }),
        MULTIPLY(
            "^x([.\\d]+)$".toRegex(),
            { value += it.groupValues[1].toDouble() - 1 },
            { "x${value + 1}" }),
        MINER(
            "^1 к (\\d+)$".toRegex(),
            { value += 1.0 / it.groupValues[1].toDouble() },
            { "1 к ${(1.0 / value).toInt()}" });

    }

}