package ru.dargen.evoplus.mixin.render.screen;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.dargen.evoplus.feature.type.misc.MiscFeature;

import java.util.List;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;wrapLines(Lnet/minecraft/text/StringVisitable;I)Ljava/util/List;", ordinal = 1))
    private List<OrderedText> render(TextRenderer instance, StringVisitable text, int width) {
        if (MiscFeature.INSTANCE.getShowServerInTab()) {
            text = Text.of(text.getString() + "\nТекущий сервер: §e" + MiscFeature.INSTANCE.getCurrentServer());
        }

        return instance.wrapLines(text, width);
    }

}
