package ru.dargen.evoplus.mixin.render.entity;

import lombok.val;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.dargen.evoplus.util.minecraft.MinecraftKt;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void shouldRender(T entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (isCosmeticStand(entity) && MinecraftKt.getClient().options.getPerspective().isFirstPerson()) cir.cancel();
    }

    @Unique
    private boolean isCosmeticStand(T entity) {
        if (entity.getType() != EntityType.ARMOR_STAND) return false;
        val stand = (ArmorStandEntity) entity;
        val equippedStack = stand.getEquippedStack(EquipmentSlot.HEAD);

        if (equippedStack == null) return false;

        val nbt = equippedStack.getNbt();

        if (nbt == null) return false;
        return nbt.contains("accessory_owner") && nbt.getString("accessory_owner").equalsIgnoreCase(MinecraftKt.getPlayerName());
    }
}
