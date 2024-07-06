package ru.dargen.evoplus.mixin.render.hud;

import lombok.val;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.api.event.EventBus;
import ru.dargen.evoplus.api.event.chat.ChatReceiveEvent;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {

    @Unique
    private boolean skipOnAddMessage;

    @Shadow public abstract void addMessage(Text message, @Nullable MessageSignatureData signature, @Nullable MessageIndicator indicator);

    @Inject(at = @At("HEAD"), method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", cancellable = true)
    public void onAddMessage(Text message, MessageSignatureData signature, MessageIndicator indicator, CallbackInfo ci) {
        if (skipOnAddMessage) return;

        val event = EventBus.INSTANCE.fire(new ChatReceiveEvent(message));

        if (event.isCancelled()) {
            ci.cancel();
            return;
        }

        if (message == event.getMessage()) return;

        ci.cancel();

        skipOnAddMessage = true;
        addMessage(event.getMessage(), signature, indicator);
        skipOnAddMessage = false;
    }
}
