package ru.dargen.evoplus.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.event.EventBus;
import ru.dargen.evoplus.event.chat.ChatReceiveEvent;
import ru.dargen.evoplus.event.chat.OverlayEvent;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    @Inject(at = @At("HEAD"), method = "sendMessage(Lnet/minecraft/text/Text;Z)V", cancellable = true)
    public void sendMessage(Text message, boolean overlay, CallbackInfo ci) {
        if (!EventBus.INSTANCE.fireResult(new ChatReceiveEvent(message, overlay))
                || !EventBus.INSTANCE.fireResult(new OverlayEvent(message))
        ) ci.cancel();
    }

}
