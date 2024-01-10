package ru.dargen.evoplus.mixin.world;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.api.event.EventBus;
import ru.dargen.evoplus.api.event.entity.EntitySpawnEvent;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {

    @Inject(at = @At("HEAD"), method = "addEntity", cancellable = true)
    private void addEntity(int id, Entity entity, CallbackInfo ci) {
        if (!EventBus.INSTANCE.fireResult(new EntitySpawnEvent(entity))) ci.cancel();
    }

}
