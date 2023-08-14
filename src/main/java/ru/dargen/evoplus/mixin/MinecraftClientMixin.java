package ru.dargen.evoplus.mixin;

import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.api.event.EventBus;
import ru.dargen.evoplus.api.event.game.MinecraftLoadedEvent;
import ru.dargen.evoplus.api.event.game.PostTickEvent;
import ru.dargen.evoplus.api.event.game.PreTickEvent;
import ru.dargen.evoplus.api.event.window.WindowResizeEvent;
import ru.dargen.evoplus.api.render.context.ScreenContext;
import ru.dargen.evoplus.util.MinecraftKt;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow @Nullable public Screen currentScreen;

    @Shadow @Final private static Logger LOGGER;

    @Shadow private Thread thread;

    @Shadow @Nullable public ClientWorld world;

    @Shadow @Nullable public ClientPlayerEntity player;

    @Shadow @Final public Mouse mouse;

    @Shadow @Final private Window window;

    @Shadow public boolean skipGameRender;

    @Shadow @Final private SoundManager soundManager;

    @Shadow public abstract void updateWindowTitle();

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

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void setScreen(Screen screen, CallbackInfo ci) {
        ci.cancel();
        if (SharedConstants.isDevelopment && Thread.currentThread() != this.thread) {
            LOGGER.error("setScreen called from non-game thread");
        }

        if (this.currentScreen != null) {
            this.currentScreen.removed();
        }

        if (screen == null && this.world == null) {
            screen = new TitleScreen();
        } else if (screen == null && this.player.isDead()) {
            if (this.player.showsDeathScreen()) {
                screen = new DeathScreen(null, this.world.getLevelProperties().isHardcore());
            } else {
                this.player.requestRespawn();
            }
        }

        this.currentScreen = screen;
        if (this.currentScreen != null) {
            this.currentScreen.onDisplayed();
        }

        BufferRenderer.reset();
        if (screen != null) {
            this.mouse.unlockCursor();

            if (screen instanceof ScreenContext.Screen customScreen && customScreen.getContext().getUnpressAllKeybindings()) KeyBinding.unpressAll();
            screen.init(((MinecraftClient)(Object) this), this.window.getScaledWidth(), this.window.getScaledHeight());
            this.skipGameRender = false;
        } else {
            this.soundManager.resumeAll();
            this.mouse.lockCursor();
        }

        this.updateWindowTitle();
    }
}
