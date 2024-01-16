package ru.dargen.evoplus.features.misc.render

import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.entity.Entity
import org.joml.Math
import ru.dargen.evoplus.api.event.entity.EntityRemoveEvent
import ru.dargen.evoplus.api.event.entity.EntitySpawnEvent
import ru.dargen.evoplus.api.event.evo.ServerChangeEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.animation.animate
import ru.dargen.evoplus.api.render.context.World
import ru.dargen.evoplus.api.render.node.*
import ru.dargen.evoplus.api.render.node.box.vbox
import ru.dargen.evoplus.api.render.node.state.hbar
import ru.dargen.evoplus.features.misc.RenderFeature
import ru.dargen.evoplus.util.collection.concurrentHashMapOf
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.Player
import ru.dargen.evoplus.util.minecraft.WorldEntities
import ru.dargen.evoplus.util.minecraft.isNPC
import ru.dargen.evoplus.util.render.TextRenderer
import ru.dargen.evoplus.util.render.progressTo
import java.util.*

object HealthBars {

    private val renderedHealthBars = concurrentHashMapOf<UUID, Node>()

    init {
        on<ServerChangeEvent> { clearHealthBars() }
        on<EntitySpawnEvent> {
            if (!RenderFeature.HealthBarsRender) return@on
            entity.createHealthBar()
        }
        on<EntityRemoveEvent> {
            if (!RenderFeature.HealthBarsRender) return@on
            renderedHealthBars.remove(entity.uuid)?.let { World - it }
        }
    }

    fun updateRender(state: Boolean) {
        if (state) fillHealthBars()
        else clearHealthBars()
    }

    fun fillHealthBars() = WorldEntities.filterIsInstance<AbstractClientPlayerEntity>().forEach { it.createHealthBar() }

    fun clearHealthBars() = renderedHealthBars.values.onEach { World - it }.clear()

    fun Entity.createHealthBar() {
        if (this !is AbstractClientPlayerEntity || isNPC || isMainPlayer || isInvisibleTo(Player)) return

        World + vbox {
            indent = v3(1.5, 1.5)

            origin = Relative.CenterBottom
            color = Colors.TransparentBlack

            preTransform { _, tickDelta ->
                val entityPos = getLerpedPos(tickDelta)
                position = entityPos.run { v3(x, y + height + .55, z) }
                rotation.y = Math.toRadians(Player!!.yaw.toDouble())
                rotation.x = Math.toRadians(-Player!!.pitch.toDouble())
            }

            +hbar {
                translation = v3(z = -.01)
                size = v3(54.0, TextRenderer.fontHeight * .8)
                progressRectangle.size.y -= 0.02

                progressRectangle.translation = v3(z = -.01)

                backgroundColor = Colors.Transparent

                val healthText = +text("") {
                    origin = Relative.Center
                    align = Relative.Center

                    translation = v3(z = -.02)
                    scale = v3(.8, .8)
                }

                tick {
                    healthText.text = if (RenderFeature.HealthCountRender) "${health.toInt()} HP" else ""

                    progress = (health / maxHealth).toDouble()
                    animate("color", interpolationTime, interpolationEasing) {
                        progressRectangle.color = Colors.Green.progressTo(Colors.Red, 1 - progress)
                    }
                }
            }

            renderedHealthBars[uuid] = this
        }
    }

}