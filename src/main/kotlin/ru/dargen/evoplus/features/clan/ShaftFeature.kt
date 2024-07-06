package ru.dargen.evoplus.features.clan

import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.decoration.DisplayEntity.ItemDisplayEntity
import net.minecraft.item.Items
import ru.dargen.evoplus.api.event.evo.ChangeLocationEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.event.world.BlockChangeEvent
import ru.dargen.evoplus.api.event.world.ChunkLoadEvent
import ru.dargen.evoplus.api.event.world.ChunkUnloadEvent
import ru.dargen.evoplus.api.event.world.WorldPreLoadEvent
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.api.scheduler.scheduleEvery
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.misc.Notifies
import ru.dargen.evoplus.features.stats.info.holder.StatisticHolder
import ru.dargen.evoplus.protocol.EvoPlusProtocol
import ru.dargen.evoplus.util.evo.isBarrel
import ru.dargen.evoplus.util.format.nounEndings
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.*
import kotlin.math.max

object ShaftFeature : Feature("shaft", "Шахта", Items.DIAMOND_PICKAXE) {

    var RaidShaftLevel = 0
    val RaidBossData = 101218
    val RaidEntityData = intArrayOf(100857, 100880, 100889, RaidBossData)
    val RaidClanMessage by settings.boolean("Сообщение о начатом рейде в клан чат с указанием шахты")

    var Worms = 0
        set(value) {
            field = value
            WormsText.text = "Червей рядом: §6$value"
        }
    val WormsText = text {
        text = "Червей рядом: §6$Worms"
        isShadowed = true
    }
    val WormsWidget by widgets.widget("Счётчик червей", "worms", enabled = false) {
        origin = Relative.CenterBottom
        align = v3(.5, .9)
        +WormsText
    }
    val WormNotify by settings.boolean("Уведомление о найденных червях", true)
    val WormMessage by settings.boolean("Сообщение о найденных червях")
    val WormClanMessage by settings.boolean("Сообщение о найденных червях в клан чат с указанием шахты")

    var Barrels = 0
        set(value) {
            if (field == 0 && value == 1) {
                if (BarrelsClanMessage) sendClanMessage("§8[§e${EvoPlusProtocol.Server}§8] §6Обнаружена бочка §8[§e/mine ${StatisticHolder.Location.shaftLevel}§8]")
                if (BarrelsNotify) Notifies.showText("§6Обнаружена бочка")
                if (BarrelsMessage) printMessage("§6Обнаружена бочка")
            }

            field = value
            BarrelsText.text = "Бочек рядом: §6$value"
        }
    val BarrelsText = text {
        text = "Бочек рядом: §6$Barrels"
        isShadowed = true
    }
    val BarrelsWidget by widgets.widget("Счётчик бочек", "barrels", enabled = false) {
        origin = Relative.CenterTop
        align = v3(.5, .9)
        +BarrelsText
    }
    val BarrelsNotify by settings.boolean("Уведомление о найденных бочках", true)
    val BarrelsMessage by settings.boolean("Сообщение о найденных бочках")
    val BarrelsClanMessage by settings.boolean("Сообщение о найденных бочках в клан")

    init {
        scheduleEvery(period = 10) {
            if (!WormsWidget.enabled) return@scheduleEvery

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
                        if (WormClanMessage) sendClanMessage("§8[§e${EvoPlusProtocol.Server}§8] $text §8[§e/mine ${StatisticHolder.Location.shaftLevel}§8]")
                    }
                }
        }

        scheduleEvery(period = 10) {
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

                    val raidBossText = if (it == RaidBossData) "§8[§aСтраж§8] "
                    else ""

                    sendClanMessage("§8[§e${EvoPlusProtocol.Server}§8] §3Обнаружена рейдовая шахта $raidBossText§8[§e/mine $RaidShaftLevel§8]")
                }
        }

        on<ChangeLocationEvent> { RaidShaftLevel = 0 }
        on<WorldPreLoadEvent> { Barrels = 0 }

        on<ChunkLoadEvent> {
            if (!BarrelsWidget.enabled) return@on

            chunk.forEachBlocks {
                if (it.isBarrel()) ++Barrels
            }
        }
        on<ChunkUnloadEvent> {
            if (!BarrelsWidget.enabled) return@on

            chunk.forEachBlocks {
                if (it.isBarrel()) Barrels = max(Barrels - 1, 0)
            }
        }

        on<BlockChangeEvent> {
            if (!BarrelsWidget.enabled) return@on

            if (newState.isBarrel()) {
                ++Barrels
                return@on
            }

            if (oldState?.isBarrel() == true && !newState.isBarrel()) Barrels = max(Barrels - 1, 0)
        }
    }
}