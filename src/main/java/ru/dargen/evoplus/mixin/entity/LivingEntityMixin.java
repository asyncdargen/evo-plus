package ru.dargen.evoplus.mixin.entity;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.feature.type.RenderFeature;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    public boolean handSwinging;

    @SuppressWarnings("all")
    @Inject(method = "tickHandSwing", at = @At("HEAD"))
    private void tickHandSwing(CallbackInfo ci) {
        if (((LivingEntity) (Object) this) instanceof ClientPlayerEntity && RenderFeature.INSTANCE.getNoHandShake()) {
            handSwinging = false;
        }
    }

}
