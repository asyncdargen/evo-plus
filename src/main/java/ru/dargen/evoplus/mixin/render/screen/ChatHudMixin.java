package ru.dargen.evoplus.mixin.render.screen;

import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChatHud.class)
public class ChatHudMixin {

//    @Inject(at = @At("HEAD"),
//            method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V",
//            cancellable = true)
//    private void text(Text message, MessageSignatureData signature, int ticks, MessageIndicator indicator, boolean refresh, CallbackInfo ci) {
//        if (!EventBus.INSTANCE.fireResult(new ChatReceiveEvent(message))) {
//            ci.cancel();
//        }
//    }

}
