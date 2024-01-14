package ru.dargen.evoplus.mixin.entity.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Shadow
    public abstract String getEntityName();

    @Shadow
    @Final
    private GameProfile gameProfile;

    @Shadow
    public abstract Text getName();

//    @Inject(at = @At("HEAD"), method = "getDisplayName", cancellable = true)
//    public void getDisplayName(CallbackInfoReturnable<Text> cir) {
//        if (gameProfile.getProperties().containsKey("evo_plus")) {
//            var name = getEntityName();
//            cir.setReturnValue(
//                    literal("EP ")
//                            .append(decorateName(getWorld().getScoreboard().getPlayerTeam(name), literal(name)))
//            );
//        }
//    }

}
