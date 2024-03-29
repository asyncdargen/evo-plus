package ru.dargen.evoplus.mixin.entity.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.dargen.evoplus.util.minecraft.MinecraftKt;

import static net.minecraft.scoreboard.Team.decorateName;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Shadow
    public abstract String getEntityName();

    @Shadow
    @Final
    private GameProfile gameProfile;

    @Shadow
    public abstract Text getName();
    @Inject(at = @At("HEAD"), method = "getDisplayName", cancellable = true)
    public void getDisplayName(CallbackInfoReturnable<Text> cir) {
        var name = getEntityName();

        if (gameProfile.getProperties().containsKey("evo_plus")) {
            cir.setReturnValue(
                    Text.literal("EP ")
                            .append(decorateName(MinecraftKt.getWorld().getScoreboard().getPlayerTeam(name), Text.literal(name)))
            );
        }
    }
}
