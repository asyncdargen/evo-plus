package ru.dargen.evoplus.protocol.registry

import net.minecraft.item.Items
import pro.diamondworld.protocol.packet.staff.StaffTypes
import ru.dargen.evoplus.util.minecraft.colored
import ru.dargen.evoplus.util.minecraft.customItem

data class StaffType(val data: StaffTypes.StaffType) : TypeRegistry.TypeRegistryEntry<Int>(data.modelId) {

    override val holder = StaffHolder(id)

    val displayName = data.name.colored()

    val displayItem = customItem(Items.WOODEN_HOE, data.modelId)

    companion object : OrdinalRegistry<StaffTypes.StaffType, StaffType, StaffTypes>(
        StaffTypes::class, StaffTypes::getTypes, ::StaffType
    )

}

class StaffHolder(key: Int) : RegistryHolder<Int, StaffType>(key) {

    override val isPresent get() = StaffType.containsKey(id)
    override fun get() = StaffType.byOrdinal(id)

}