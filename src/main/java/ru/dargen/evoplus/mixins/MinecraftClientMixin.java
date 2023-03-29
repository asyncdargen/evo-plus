package ru.dargen.evoplus.mixins;

import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.dargen.evoplus.EvoPlus;
import ru.dargen.evoplus.event.server.DisconnectServerEvent;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow public abstract Profiler getProfiler();

    @Inject(at = @At("RETURN"), method = "getWindowTitle", cancellable = true)
    public void getWindowTitle(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue("Minecraft " + SharedConstants.getGameVersion().getName() + " | EvoPlus v" + EvoPlus.version());
    }

    @Inject(at = @At("TAIL"), method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V")
    public void disconnect(CallbackInfo ci) {
        EvoPlus.instance().getEventBus().fireEvent(new DisconnectServerEvent());
    }
}
