package ru.dargen.evoplus.mixins;

import lombok.val;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.EvoPlus;
import ru.dargen.evoplus.event.chat.ChatSendEvent;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    @Shadow @Final public ClientPlayNetworkHandler networkHandler;

    @Inject(at = @At("HEAD"), method = "sendChatMessage", cancellable = true)
    public void sendMessage(String message, CallbackInfo ci) {
        val event = new ChatSendEvent(message);
        if (EvoPlus.instance().getEventBus().fireEvent(event).isCancelled())
            ci.cancel();
//            networkHandler.sendPacket(new ChatMessageC2SPacket(event.getText()));
    }

}
