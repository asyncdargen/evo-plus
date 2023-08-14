package ru.dargen.evoplus.util.minecraft

import net.minecraft.entity.Entity
import net.minecraft.registry.tag.FluidTags
import ru.dargen.evoplus.mixin.entity.EntityAccessor
import ru.dargen.evoplus.util.kotlin.cast

val Entity.fluidHeight get() = cast<EntityAccessor>().fluidHeight

val Entity.isInWater get() = fluidHeight.getDouble(FluidTags.WATER) > .0