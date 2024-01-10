package ru.dargen.evoplus.util.format

import java.io.InputStream
import java.io.Reader

fun InputStream.readCSV() = bufferedReader().use(Reader::readLines).toCSV()

fun String.toCSV() = lines().toCSV()

fun Iterable<String>.toCSV() = map { it.split(",") }