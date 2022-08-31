package ru.dargen.evoplus.mixins;

import lombok.val;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.dargen.evoplus.feature.Feature;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {

    @Shadow protected abstract Text applyGameModeFormatting(PlayerListEntry entry, MutableText name);

    @Inject(at = @At("HEAD"), method = "getPlayerName", cancellable = true)
    public void getPlayerName(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
        val name = entry.getProfile().getName();
        val color = Feature.TEAM_WAR_FEATURE.getTabColorForPlayer(name);
        if (color != null)
            cir.setReturnValue(
                    entry.getDisplayName() != null
                            ? applyGameModeFormatting(entry, entry.getDisplayName().shallowCopy())
                            : this.applyGameModeFormatting(entry, Team.modifyText(entry.getScoreboardTeam(), new LiteralText(color + name)))
            );
    }

}
