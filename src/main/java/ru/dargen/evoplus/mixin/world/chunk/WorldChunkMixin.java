package ru.dargen.evoplus.mixin.world.chunk;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.dargen.evoplus.api.event.EventBus;
import ru.dargen.evoplus.api.event.world.BlockChangeEvent;

@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin {

    @Shadow @Final
    World world;

    @Inject(at = @At("TAIL"), method = "setBlockState")
    public void setBlockState(BlockPos pos, BlockState newState, boolean moved, CallbackInfoReturnable<BlockState> cir) {
        if (world.isClient)
            EventBus.INSTANCE.fire(new BlockChangeEvent((Chunk) (Object) this, pos, cir.getReturnValue(), newState));
    }
}
