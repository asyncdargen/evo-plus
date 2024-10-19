package ru.dargen.evoplus.mixin.entity.player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.dargen.evoplus.service.EvoPlusService;

import java.util.List;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Shadow public abstract String getEntityName();

    @Inject(at = @At("RETURN"), method = "getDisplayName", cancellable = true)
    private void getDisplayName(CallbackInfoReturnable<Text> cir) {
        if (getEntityName() != null && EvoPlusService.INSTANCE.isIngame(getEntityName())) {
            cir.setReturnValue(Texts.join(List.of(Text.of("EP"), cir.getReturnValue()), Text.literal(" ")));
        }
    }

}
