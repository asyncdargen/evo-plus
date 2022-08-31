package ru.dargen.evoplus.mixins;

import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBrandRetriever;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.dargen.evoplus.EvoPlus;

@Mixin(ClientBrandRetriever.class)
public class ClientBrandRetrieverMixin {

    @Inject(at = @At("RETURN"), method = "getClientModName", cancellable = true, remap = false)
    private static void getClientModName(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue("Minecraft " + SharedConstants.getGameVersion().getName() + " | EvoPlus v" + EvoPlus.version());
    }

}
