package ru.dargen.evoplus.util.minecraft

import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.registry.tag.FluidTags
import ru.dargen.evoplus.mixin.entity.EntityAccessor
import ru.dargen.evoplus.mixin.entity.player.PlayerInventoryAccessor
import ru.dargen.evoplus.util.kotlin.cast

val Entity.fluidHeight get() = cast<EntityAccessor>().fluidHeight

val Entity.isInWater get() = fluidHeight.getDouble(FluidTags.WATER) > .0

val PlayerInventory.items get() = cast<PlayerInventoryAccessor>().combinedInventory.flatten()

val AbstractClientPlayerEntity.isNPC get() = '§' in gameProfile.name || gameProfile.name.isBlank()