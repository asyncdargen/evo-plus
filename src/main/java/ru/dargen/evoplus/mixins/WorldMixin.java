package ru.dargen.evoplus.mixins;

import lombok.val;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.dargen.evoplus.feature.Feature;
import ru.dargen.evoplus.util.diamondworld.DiamondWorldUtil;

@Mixin(World.class)
public class WorldMixin {

    @Inject(at = @At("HEAD"), method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z")
    public void setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
        val lastBreak = Feature.STATS_FEATURE.getLatestBlocks().getIfPresent(pos);
        if (lastBreak != null && !state.isAir() && DiamondWorldUtil.isOnPrisonEvo()) {
            Feature.STATS_FEATURE.getBoosterInfo().setLastBreak(lastBreak);
            Feature.STATS_FEATURE.getLatestBlocks().invalidate(pos);
            Feature.STATS_FEATURE.getBoosterInfo().cancelBreak();
        }
    }

}
