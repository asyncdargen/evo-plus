package ru.dargen.evoplus.mixins;

import lombok.val;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.EvoPlus;
import ru.dargen.evoplus.event.chat.ChatReceiveEvent;

import java.util.List;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {

    @Shadow
    public abstract void addMessage(Text message, @Nullable MessageSignatureData signature, @Nullable MessageIndicator indicator);

    @Shadow @Final private MinecraftClient client;

    @Shadow
    public abstract void removeMessage(MessageSignatureData message);

    @Shadow public abstract int getWidth();

    @Shadow public abstract double getChatScale();

    @Shadow protected abstract boolean isChatFocused();

    @Shadow @Final private List<ChatHudLine.Visible> visibleMessages;

    @Shadow private int scrolledLines;

    @Shadow public abstract void scroll(int amount);

    @Shadow private boolean hasUnreadNewMessages;

    @Shadow @Final private List<ChatHudLine> messages;

    @Shadow protected abstract void refresh();

    @Inject(at = @At("HEAD"), method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", cancellable = true)
    public void onChat(Text message, MessageSignatureData signature, int ticks, MessageIndicator indicator, boolean refresh, CallbackInfo ci) {
        ci.cancel();
        val event = new ChatReceiveEvent(message);
        if (!EvoPlus.instance().getEventBus().fireEvent(event).isCancelled()) {
            if (signature != null) {
                removeMessage(signature);
            }

            int i = MathHelper.floor((double)getWidth() / getChatScale());
            List<OrderedText> list = ChatMessages.breakRenderedChatMessageLines(message, i, client.textRenderer);
            boolean bl = isChatFocused();

            OrderedText orderedText;

            for (int j = 0; j < list.size(); j++) {
                orderedText = list.get(j);
                if (bl && scrolledLines > 0) {
                    hasUnreadNewMessages = true;
                    scroll(1);
                }

                boolean bl2 = j == list.size() - 1;
                this.visibleMessages.add(0, new ChatHudLine.Visible(ticks, orderedText, indicator, bl2));
            }

            while(visibleMessages.size() > 100) visibleMessages.remove(visibleMessages.size() - 1);

            if (!refresh) {
                messages.add(0, new ChatHudLine(ticks, message, signature, indicator));

                while(messages.size() > 100) messages.remove(messages.size() - 1);
            }
        }
    }

}
