package ru.dargen.evoplus.features.alchemy.recipe

private val actionRegex = "^(\\d+)с. - (\\p{L}+) \"?([\\p{L}\\s]+.+?(?= x))\"?(?: x\\s?(\\d+))?".toRegex()

data class PotionRecipe(val recipeLore: List<String>) {

    private val alerts = recipeLore
        .mapNotNull { actionRegex.find(it) }
        .associate { it.groupValues[1].toInt() to "${it.groupValues[2]} ${it.groupValues[3]} x${it.groupValues.getOrElse(4) { "1" }}" }

    fun getNearestAlert(delay: Double, time: Double) =
        alerts.entries.firstOrNull { (timing, _) -> time in (timing - delay)..timing.toDouble() }?.value
}