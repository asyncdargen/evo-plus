package ru.dargen.evoplus.mixin.world;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.api.event.EventBus;
import ru.dargen.evoplus.api.event.world.BlockChangeEvent;

@Mixin(World.class)
public abstract class WorldMixin {
    
    @Shadow
    public abstract WorldChunk getWorldChunk(BlockPos pos);
    
    @Inject(at = @At("TAIL"), method = "onBlockChanged")
    public void setBlockState(BlockPos pos, BlockState oldBlock, BlockState newBlock, CallbackInfo ci) {
        EventBus.INSTANCE.fire(new BlockChangeEvent(getWorldChunk(pos), pos, oldBlock, newBlock));
    }
}
