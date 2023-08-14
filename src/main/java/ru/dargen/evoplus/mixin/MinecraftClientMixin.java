package ru.dargen.evoplus.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.api.event.EventBus;
import ru.dargen.evoplus.api.event.game.MinecraftLoadedEvent;
import ru.dargen.evoplus.api.event.game.PostTickEvent;
import ru.dargen.evoplus.api.event.game.PreTickEvent;
import ru.dargen.evoplus.api.event.window.WindowResizeEvent;
import ru.dargen.evoplus.util.minecraft.MinecraftKt;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

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

}
