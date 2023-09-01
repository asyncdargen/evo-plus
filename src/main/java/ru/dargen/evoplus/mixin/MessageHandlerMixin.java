package ru.dargen.evoplus.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.dargen.evoplus.api.event.EventBus;
import ru.dargen.evoplus.api.event.chat.ChatReceiveEvent;
import ru.dargen.evoplus.api.event.chat.GameMessageEvent;

import java.time.Instant;

@Mixin(MessageHandler.class)
public class MessageHandlerMixin {

    @Inject(method = "onGameMessage", at = @At("HEAD"), cancellable = true)
    private void onGameMessage(Text message, boolean overlay, CallbackInfo ci) {
        if (!EventBus.INSTANCE.fireResult(new GameMessageEvent(message, overlay))) {
            ci.cancel();
        }
    }

}