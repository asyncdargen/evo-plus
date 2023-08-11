package ru.dargen.evoplus.mixin.world;

import lombok.experimental.Accessors;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Queue;

@Mixin(ParticleManager.class)
public interface ParticleManagerAccessor {

    @Accessor("newParticles")
    Queue<Particle> getParticles();

}
