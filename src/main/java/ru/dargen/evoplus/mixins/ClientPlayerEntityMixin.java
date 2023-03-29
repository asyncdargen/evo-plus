package ru.dargen.evoplus.mixins;

import lombok.val;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
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

    @Inject(at = @At("HEAD"), method = "sendMessage(Lnet/minecraft/text/Text;Z)V", cancellable = true)
    public void sendMessage(Text message, boolean overlay, CallbackInfo ci) {
        val event = new ChatSendEvent(message.getString());
        if (EvoPlus.instance().getEventBus().fireEvent(event).isCancelled())
            ci.cancel();
//            networkHandler.sendPacket(new ChatMessageC2SPacket(event.getText()));
    }

}
