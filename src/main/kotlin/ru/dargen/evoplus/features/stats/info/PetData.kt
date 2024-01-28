package ru.dargen.evoplus.features.stats.info

import ru.dargen.evoplus.protocol.registry.PetHolder
import ru.dargen.evoplus.protocol.registry.PetType

data class PetData(val pet: PetHolder, val level: Int, val exp: Double, val energy: Double) {

    val type get() = pet.get()!!

    companion object {

        fun random() = PetType.values.random().let { PetData(it.holder,5,512.0, 5.0) }

    }

}