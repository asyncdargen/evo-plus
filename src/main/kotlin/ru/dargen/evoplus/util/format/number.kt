package ru.dargen.evoplus.util.format

import java.text.DecimalFormat

val NumberFormat = DecimalFormat("### ###")

fun Int.spacing() = NumberFormat.format(this)