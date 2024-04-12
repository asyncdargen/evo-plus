package ru.dargen.evoplus.mixin.entity.player;

import com.mojang.authlib.GameProfile;
import lombok.val;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.dargen.evoplus.PrefixParser;
import ru.dargen.evoplus.util.minecraft.MinecraftKt;

import static net.minecraft.scoreboard.Team.decorateName;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Shadow
    public abstract String getEntityName();

    @Shadow
    public abstract Text getName();

    @Inject(at = @At("RETURN"), method = "getDisplayName", cancellable = true)
    public void getDisplayName(CallbackInfoReturnable<Text> cir) {
        val prefix = PrefixParser.INSTANCE.getPrefixes().getProperty(getEntityName());

        if (prefix == null || prefix.isEmpty()) return;

        cir.setReturnValue(Text.literal(prefix).append(cir.getReturnValue()));
    }
}
