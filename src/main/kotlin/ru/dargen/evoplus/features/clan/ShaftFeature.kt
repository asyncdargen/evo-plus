package ru.dargen.evoplus.features.clan

import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.decoration.DisplayEntity.ItemDisplayEntity
import net.minecraft.item.Items
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.api.scheduler.scheduleEvery
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.misc.Notifies
import ru.dargen.evoplus.features.stats.info.holder.StatisticHolder
import ru.dargen.evoplus.protocol.EvoPlusProtocol
import ru.dargen.evoplus.util.format.nounEndings
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.WorldEntities
import ru.dargen.evoplus.util.minecraft.customModelData
import ru.dargen.evoplus.util.minecraft.printMessage
import ru.dargen.evoplus.util.minecraft.sendClanMessage

object ShaftFeature : Feature("shart", "Шахта", Items.DIAMOND_PICKAXE) {

    var Worms = 0
        set(value) {
            field = value
            WormsText.text = "Червей: §6$value"
        }
    var RaidShaftLevel = 0
    val RaidBossData = 101218
    val RaidEntityData = intArrayOf(100857, 100880, 100889, RaidBossData)

    val WormsText = text { isShadowed = true }
    val WormsWidget by widgets.widget("Счётчик червей", "worms", enabled = false) {
        origin = Relative.CenterBottom
        align = v3(.5, .9)
        +WormsText
    }

    val WormNotify by settings.boolean("Уведомление о найденных червях", true)
    val WormMessage by settings.boolean("Сообщение о найденных червях")
    val WormClanMessage by settings.boolean("Сообщение о найденных червях в клан чат с указанием шахты")
    val RaidClanMessage by settings.boolean("Сообщение о начатом рейде в клан чат с указанием шахты")

    init {
        scheduleEvery(period = 10) {
            WorldEntities
                .filterIsInstance<ArmorStandEntity>()
                .filter { "Червь" in it.name.string }
                .apply {
                    val previousWorms = Worms
                    Worms = size

                    if (previousWorms < size) {

                        val text = "§6Обнаружен${if (size > 1) "о" else ""} $size ${
                            size.nounEndings("червь", "червя", "червей")
                        }"
                        if (WormNotify) Notifies.showText(text)
                        if (WormMessage) printMessage(text)
                        if (WormClanMessage) sendClanMessage("§8[§e${EvoPlusProtocol.Server}§8] $text &8[&e/mine ${StatisticHolder.Location.shaftLevel}&8]")
                    }
                }

            if (!RaidClanMessage) return@scheduleEvery

            WorldEntities
                .filterIsInstance<ItemDisplayEntity>()
                .filter { it.itemStack.item == Items.LEATHER_HORSE_ARMOR }
                .mapNotNull { it.itemStack.customModelData }
                .filter { it in RaidEntityData }
                .forEach {
                    val previousRaidShaftLevel = RaidShaftLevel
                    RaidShaftLevel = StatisticHolder.Location.shaftLevel

                    if (previousRaidShaftLevel == RaidShaftLevel) return@scheduleEvery

                    val raidBossText = if (it == RaidBossData) "&8[&aСтраж&8] "
                    else ""

                    sendClanMessage("§8[§e${EvoPlusProtocol.Server}§8] &3Обнаружена рейдовая шахта $raidBossText&8[&e/mine $RaidShaftLevel&8]")
                }
        }
    }
}