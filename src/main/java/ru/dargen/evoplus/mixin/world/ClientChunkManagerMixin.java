package ru.dargen.evoplus.mixin.world;

import lombok.val;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.api.event.EventBus;
import ru.dargen.evoplus.api.event.world.ChunkUnloadEvent;

@Mixin(ClientChunkManager.class)
public class ClientChunkManagerMixin {

    @Shadow @Final
    ClientWorld world;

    @Inject(at = @At(value = "HEAD", target = "Lnet/minecraft/client/world/ClientChunkManager$ClientChunkMap;compareAndSet(ILnet/minecraft/world/chunk/WorldChunk;Lnet/minecraft/world/chunk/WorldChunk;)Lnet/minecraft/world/chunk/WorldChunk;"), method = "unload")
    private void unloadChunk(int chunkX, int chunkZ, CallbackInfo ci) {
        val chunk = world.getChunk(chunkX, chunkZ);
        EventBus.INSTANCE.fire(new ChunkUnloadEvent(chunk));
    }
}
