package ru.dargen.evoplus.features.misc

import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.item.Items
import org.joml.Math
import ru.dargen.evoplus.api.event.entity.EntityRemoveEvent
import ru.dargen.evoplus.api.event.entity.EntitySpawnEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.context.World
import ru.dargen.evoplus.api.render.node.*
import ru.dargen.evoplus.api.render.node.state.hbar
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.Player
import ru.dargen.evoplus.util.selector.enumSelector
import ru.dargen.evoplus.util.selector.toSelector
import java.util.*

object RenderFeature : Feature("render", "Визуализация", Items.REDSTONE) {

    private val renderedHeatlhBars = mutableMapOf<UUID, Node>()

    val FullBright by settings.boolean("Полная яркость", true)

    val NoBlockParticles by settings.boolean("Отключение эффектов блока")
    val NoFire by settings.boolean("Отключение огня")
    val NoStrikes by settings.boolean("Отключение молний")
    val NoFalling by settings.boolean("Отключение падающих блоков")
    val NoDamageShake by settings.boolean("Отключение покачивания камеры, при ударе")
    val NoHandShake by settings.boolean("Отключение покачивания руки")
    val NoExcessHud by settings.boolean("Отключение ненужных элементов HUD", true)
    val NoExpHud by settings.boolean("Отключение отрисовки опыта и его уровня", true)
    val NoScoreboardNumbers by settings.boolean("Отключение нумерации скорборда", true)
    val HealthBarsRender by settings.boolean("Отображать здоровья игроков", true) on { state ->
        if (!state) renderedHeatlhBars.entries.removeAll {
            World - it.value
            true
        }
    }
    val HealthBarsRenderYOffset by settings.selector("Смещение по Y", (5..25).toSelector())
    val HealthRender by settings.switcher("Режим отображения здоровья", enumSelector<HealthRenderMode>())

    init {
        on<EntitySpawnEvent> {
            if (!HealthBarsRender) return@on
            val entity = entity
            if (entity !is AbstractClientPlayerEntity || entity.isMainPlayer || entity.isInvisibleTo(Player)) return@on

            World + hbar {
                origin = Relative.Center
                size = v3(54.0, 4.0)

                interpolationTime = .51

                backgroundColor = Colors.Red
                progressColor = Colors.Green
                preTransform { _, tickDelta ->
                    val entityPos = entity.getLerpedPos(tickDelta)
                    position = entityPos.run { v3(x, y + entity.height + (HealthBarsRenderYOffset / 10.0), z) }
                    rotation.y = Math.toRadians(Player!!.yaw.toDouble())
                    rotation.x = Math.toRadians(-Player!!.pitch.toDouble())
                }
                tick { progress = (entity.health / entity.maxHealth).toDouble().coerceIn(.0, 1.0) }
                renderedHeatlhBars[entity.uuid] = this
            }
        }

        on<EntityRemoveEvent> {
            if (!HealthBarsRender) return@on
            renderedHeatlhBars.remove(entity.uuid)?.let { World - it }
        }
    }

    enum class HealthRenderMode(val displayName: String, val isDefaultHearts: Boolean = true) {

        DEFAULT("Обычный"),
        LONG("Удлинненный");

        override fun toString() = displayName

    }

}