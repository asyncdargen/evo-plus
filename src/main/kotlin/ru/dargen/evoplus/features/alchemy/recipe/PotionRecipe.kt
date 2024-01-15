package ru.dargen.evoplus.features.alchemy.recipe

private val actionRegex = "(\\d+)с. - (\\S+) \"?([а-яА-Я ]+)\"?[x ]*(\\d+|)".toRegex()

data class PotionRecipe(val recipeLore: List<String>) {

    private val alerts = recipeLore
        .mapNotNull { actionRegex.find(it) }
        .associate { it.groupValues[1].toInt() to "${it.groupValues[2]} ${it.groupValues[3].trim()} x${it.groupValues.getOrElse(4) { "1" }}" }

    fun getNearestAlert(delay: Double, time: Double) =
        alerts.entries.firstOrNull { (timing, _) -> time in (timing - delay)..timing.toDouble() }?.value
}