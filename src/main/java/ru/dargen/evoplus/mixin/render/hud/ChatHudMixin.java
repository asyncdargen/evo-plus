package ru.dargen.evoplus.mixin.render.hud;

import lombok.val;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.dargen.evoplus.features.chat.ChatFeature;
import ru.dargen.evoplus.util.mixin.ChatCopyUtil;

import java.util.ArrayList;
import java.util.List;

@Mixin(ChatScreen.class)
public abstract class ChatHudMixin extends Screen {
    protected ChatHudMixin(Text title) {
        super(title);
    }

    @Unique
    private boolean rightClicked = false;

    @Inject(method = "render", at = @At("HEAD"))
    public void onDraw(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!ChatFeature.INSTANCE.getCopyMessages() || !rightClicked) return;
        this.rightClicked = false;

        val chatHudAccessor = ((ChatHudAccessor) getChatHud());
        List<ChatHudLine.Visible> visibleMessages = chatHudAccessor.getVisibleMessages();

        if (visibleMessages.isEmpty()) return;

        double chatLineY = chatHudAccessor.toChatLineYA(mouseY);
        int index = chatHudAccessor.getMessageIndexA(0, chatLineY);

        if (index < 0) return;

        ArrayList<ChatHudLine.Visible> messageParts = new ArrayList<>();
        messageParts.add(visibleMessages.get(index));
        for (int i = index + 1; i < visibleMessages.size(); i++) {
            if (visibleMessages.get(i).endOfEntry()) break;
            messageParts.add(0, visibleMessages.get(i));
        }
        if (messageParts.isEmpty()) return;

        ChatCopyUtil.copyString(messageParts);
    }

    @Inject(method = "mouseClicked", at = @At("RETURN"))
    public void onMouseClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (button != 1) return;
        this.rightClicked = !cir.getReturnValue();
    }

    @Unique
    private ChatHud getChatHud() {
        return this.client.inGameHud.getChatHud();
    }
}

