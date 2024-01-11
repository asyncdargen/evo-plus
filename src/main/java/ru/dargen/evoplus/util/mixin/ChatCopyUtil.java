package ru.dargen.evoplus.util.mixin;

import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.Style;
import ru.dargen.evoplus.util.minecraft.MinecraftKt;

import java.util.List;

@UtilityClass
public class ChatCopyUtil {

    public void copyString(List<ChatHudLine.Visible> lines) {
        val characterHandler = new CharacterHandler();
        for (ChatHudLine.Visible line : lines) {
            line.content().accept(characterHandler);
        }
        MinecraftKt.getClient().keyboard.setClipboard(characterHandler.collect());
    }

    private class CharacterHandler implements CharacterVisitor {

        private final StringBuilder builder = new StringBuilder();

        @Override
        public boolean accept(int index, Style style, int codePoint) {
            builder.append((char) codePoint);
            return true;
        }

        public String collect() {
            return builder.toString();
        }
    }
}
