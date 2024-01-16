package ru.dargen.evoplus.mixin.world;

import lombok.val;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.api.event.EventBus;
import ru.dargen.evoplus.api.event.entity.EntityRemoveEvent;
import ru.dargen.evoplus.api.event.entity.EntitySpawnEvent;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {

    @Shadow @Nullable public abstract Entity getEntityById(int id);

    @Inject(at = @At("HEAD"), method = "addEntityPrivate", cancellable = true)
    private void addEntity(int id, Entity entity, CallbackInfo ci) {
        if (!EventBus.INSTANCE.fireResult(new EntitySpawnEvent(entity))) ci.cancel();
    }

    @Inject(at = @At("HEAD"), method = "removeEntity")
    private void removeEntity(int entityId, Entity.RemovalReason removalReason, CallbackInfo ci) {
        val entity = getEntityById(entityId);

        if (entity == null) return;

        EventBus.INSTANCE.fire(new EntityRemoveEvent(entity));
    }
}
