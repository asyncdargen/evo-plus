package ru.dargen.evoplus.mixin.render.screen;

import lombok.val;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.dargen.evoplus.features.misc.MiscFeature;
import ru.dargen.evoplus.features.potion.PotionFeature;
import ru.dargen.evoplus.protocol.EvoPlusProtocol;
import ru.dargen.evoplus.util.format.TimeKt;

import java.util.List;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;wrapLines(Lnet/minecraft/text/StringVisitable;I)Ljava/util/List;", ordinal = 1))
    private List<OrderedText> render(TextRenderer instance, StringVisitable text, int width) {
        val stringBuilder = new StringBuilder();
        val showServerInTab = MiscFeature.INSTANCE.getShowServerInTab();

        if (PotionFeature.INSTANCE.getEnabledPotionsInTab()) {
            val potionTimers = PotionFeature.INSTANCE.getPotionTimers();
            if (!potionTimers.isEmpty()) {
                stringBuilder.append("\n§e§lАктивные Эффекты §r§8(%s)".formatted(potionTimers.size()));
                potionTimers.forEach((potionType, potionState) ->
                        stringBuilder.append("\n%s (%s%%) §f%s".formatted(
                                potionType.getDisplayName(),
                                potionState.getQuality(),
                                TimeKt.getAsShortTextTime(potionState.getEndTime() - System.currentTimeMillis()))
                        )
                );
                stringBuilder.append("\n");
            }
        }

        if (showServerInTab)
            stringBuilder.append(text.getString())
                    .append("\nТекущий сервер: §e%s".formatted(EvoPlusProtocol.INSTANCE.getServer().toString()));

        text = Text.of(stringBuilder.toString());
        return instance.wrapLines(text, width);
    }

}
