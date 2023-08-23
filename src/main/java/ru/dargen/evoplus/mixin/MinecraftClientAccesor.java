package ru.dargen.evoplus.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Queue;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccesor {

    @Accessor("renderTaskQueue")
    Queue<Runnable> getRenderTaskQueue();

}
