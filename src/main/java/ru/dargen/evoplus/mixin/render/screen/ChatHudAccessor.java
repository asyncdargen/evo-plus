package ru.dargen.evoplus.mixin.render.screen;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ChatHud.class)
public interface ChatHudAccessor {

    @Accessor
    List<ChatHudLine.Visible> getVisibleMessages();

    @Invoker("getMessageIndex")
    int getMessageIndexA(double chatLineX, double chatLineY);

    @Invoker("toChatLineY")
    double toChatLineYA(double y);
}
