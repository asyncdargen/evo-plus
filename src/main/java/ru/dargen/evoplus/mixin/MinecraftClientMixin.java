package ru.dargen.evoplus.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.util.Window;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.api.event.EventBus;
import ru.dargen.evoplus.api.event.game.MinecraftLoadedEvent;
import ru.dargen.evoplus.api.event.game.PostTickEvent;
import ru.dargen.evoplus.api.event.game.PreTickEvent;
import ru.dargen.evoplus.api.event.window.WindowResizeEvent;
import ru.dargen.evoplus.feature.type.RenderFeature;
import ru.dargen.evoplus.util.minecraft.MinecraftKt;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow
    protected int attackCooldown;

    @Shadow
    @Nullable
    public ClientPlayerInteractionManager interactionManager;

    @Inject(method = "onResolutionChanged", at = @At("RETURN"))
    private void onResolutionChanged(CallbackInfo ci) {
        Window window = MinecraftKt.getWindow();
        EventBus.INSTANCE.fire(new WindowResizeEvent(window.getScaledWidth(), window.getScaledHeight()));
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onStartTick(CallbackInfo info) {
        EventBus.INSTANCE.fire(PreTickEvent.INSTANCE);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void onEndTick(CallbackInfo info) {
        EventBus.INSTANCE.fire(PostTickEvent.INSTANCE);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CallbackInfo info) {
        EventBus.INSTANCE.fire(MinecraftLoadedEvent.INSTANCE);
    }

    @Redirect(method = "handleBlockBreaking", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;swingHand(Lnet/minecraft/util/Hand;)V"))
    private void handleBlockBreaking(ClientPlayerEntity instance, Hand hand) {
        if (RenderFeature.INSTANCE.getNoHandShake()) {
            instance.networkHandler.sendPacket(new HandSwingC2SPacket(hand));
        } else instance.swingHand(hand);
    }

}
