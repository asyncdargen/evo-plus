//package ru.dargen.evoplus.feature.type
//
//import net.minecraft.client.particle.WaterBubbleParticle
//import net.minecraft.item.Items
//import ru.dargen.evoplus.Executor
//import ru.dargen.evoplus.feature.Feature
//import ru.dargen.evoplus.mixin.world.ParticleManagerAccessor
//import ru.dargen.evoplus.mixin.world.particle.ParticleAccessor
//import ru.dargen.evoplus.util.Client
//import ru.dargen.evoplus.util.Player
//import ru.dargen.evoplus.util.PlayerEyePosition
//import ru.dargen.evoplus.util.PlayerPosition
//import ru.dargen.evoplus.util.kotlin.cast
//import ru.dargen.evoplus.util.math.Vector3
//import ru.dargen.evoplus.util.math.v3
//import ru.dargen.evoplus.util.selector.toSelector
//import java.util.concurrent.TimeUnit
//import kotlin.math.*
//
//object AutoFishFeature : Feature("auto-fish", "Авто-рыбалка", Items.PUFFERFISH) {
//
//    var Enabled by settings.boolean("enabled", "Включена авто-рыбалка", false)
//    val Distance by settings.selector(
//        "distance", "Макс. расстояние до источника",
//        (0..12).toList().toSelector()
//    ) { "$it блок." }
//    val CatchDelay by settings.selector(
//        "catch-delay", "Задержка до вытягивания",
//        (0..20).toList().toSelector()
//    ) { "$it тиков" }
//    val ThrowDelay by settings.selector(
//        "throw-delay", "Задержка перед броском",
//        (0..20).toList().toSelector()
//    ) { "$it тиков" }
//    val AngleSpeed by settings.selector(
//        "angle-speed", "Скорость поворота головы",
//        (0..60).toList().toSelector()
//    ) { "$it град." }
//
//    var Source = v3()
//    val WorldParticles
//        get() =
//            Client.particleManager
//                .cast<ParticleManagerAccessor>()
//                .particles
//                .asSequence()
//                .filterIsInstance<WaterBubbleParticle>()
//                .filterIsInstance<ParticleAccessor>()
//                .map { v3(it.x, it.y, it.z) }
//M
//    init {
//        Executor.scheduleAtFixedRate({
//            val points = WorldParticles
//            if (Source.distance(PlayerPosition) > Distance || points.none { Source.distance(it) < .9 }) {
//                Source = points.filter { it.distance(PlayerPosition) <= Distance }.minBy { it.distance(PlayerPosition) }
//            }
//
//            val (yaw, pitch) = (Source - PlayerEyePosition).normalize().toYawPitch()
//            val currentYaw = Player!!.yaw
//            val currentPitch = Player!!.pitch
//
//            val deltaYaw = yaw - currentYaw
//            val deltaPitch = pitch - currentPitch
//
//            Player!!.yaw = currentYaw + deltaYaw.sign * min(deltaYaw.absoluteValue.toFloat(), AngleSpeed.toFloat())
//
//        }, 50, 50, TimeUnit.MILLISECONDS);
//    }
//
//    fun Vector3.toYawPitch(): Pair<Float, Float> {
//        val yaw = Math.toDegrees(atan2(-x, z)).toFloat()
//        val pitch = Math.toDegrees(atan2(-y, sqrt(x * x + z * z))).toFloat()
//        return Pair(yaw, pitch)
//    }
//
//}