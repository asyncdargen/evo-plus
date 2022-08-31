package ru.dargen.evoplus.mixins;

import lombok.val;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.dargen.evoplus.feature.Feature;
import ru.dargen.evoplus.util.Util;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Shadow
    public abstract String getEntityName();

    @Inject(
            at = @At("HEAD"),
            method = "getDisplayName",
            cancellable = true
    )
    public void getDisplayName(CallbackInfoReturnable<Text> cir) {
        val name = getEntityName();
        val color = Feature.TEAM_WAR_FEATURE.getTagColorForPlayer(name);
        if (color != null) cir.setReturnValue(Team.modifyText(
                Util.getClient().world.getScoreboard().getPlayerTeam(name),
                Text.of(color + name))
        );
    }

}
