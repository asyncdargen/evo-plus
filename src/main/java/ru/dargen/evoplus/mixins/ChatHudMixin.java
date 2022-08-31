package ru.dargen.evoplus.mixins;

import lombok.val;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.EvoPlus;
import ru.dargen.evoplus.event.chat.ChatReceiveEvent;

import java.util.Iterator;
import java.util.List;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {

    @Shadow protected abstract void addMessage(Text message, int messageId, int timestamp, boolean refresh);

    @Shadow @Final private MinecraftClient client;

    @Shadow @Final private static Logger LOGGER;

    @Shadow protected abstract void removeMessage(int messageId);

    @Shadow public abstract int getWidth();

    @Shadow public abstract double getChatScale();

    @Shadow protected abstract boolean isChatFocused();

    @Shadow @Final private List<ChatHudLine<OrderedText>> visibleMessages;

    @Shadow private int scrolledLines;

    @Shadow public abstract void scroll(double amount);

    @Shadow private boolean hasUnreadNewMessages;

    @Shadow @Final private List<ChatHudLine<Text>> messages;

    @Inject(at = @At("HEAD"), method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", cancellable = true)
    public void onChat(Text message, int messageId, int timestamp, boolean refresh, CallbackInfo ci) {
        ci.cancel();
        val event = new ChatReceiveEvent(message);
        if (!EvoPlus.instance().getEventBus().fireEvent(event).isCancelled()) {
            if (messageId != 0) {
                removeMessage(messageId);
            }

            int i = MathHelper.floor((double)getWidth() / getChatScale());
            List<OrderedText> list = ChatMessages.breakRenderedChatMessageLines(message, i, client.textRenderer);
            boolean bl = isChatFocused();

            OrderedText orderedText;
            for(Iterator var8 = list.iterator(); var8.hasNext(); visibleMessages.add(0, new ChatHudLine(timestamp, orderedText, messageId))) {
                orderedText = (OrderedText)var8.next();
                if (bl && scrolledLines > 0) {
                    hasUnreadNewMessages = true;
                    scroll(1.0);
                }
            }

            while(visibleMessages.size() > 100) visibleMessages.remove(visibleMessages.size() - 1);

            if (!refresh) {
                messages.add(0, new ChatHudLine(timestamp, message, messageId));

                while(messages.size() > 100) messages.remove(messages.size() - 1);
            }
        }
    }

}
