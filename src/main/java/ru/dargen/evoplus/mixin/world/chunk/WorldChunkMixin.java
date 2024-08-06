package ru.dargen.evoplus.mixin.world.chunk;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.dargen.evoplus.api.event.EventBus;
import ru.dargen.evoplus.api.event.world.block.BlockChangeEvent;
import ru.dargen.evoplus.api.event.world.block.BlockEntityLoadEvent;
import ru.dargen.evoplus.api.event.world.block.BlockEntityUnloadEvent;
import ru.dargen.evoplus.api.event.world.block.BlockEntityUpdateEvent;

@Mixin(WorldChunk.class)
public class WorldChunkMixin {

    @Inject(method = "addBlockEntity", at = @At("HEAD"), cancellable = true)
    private void addBlockEntity(BlockEntity blockEntity, CallbackInfo ci) {
        if (!EventBus.INSTANCE.fireResult(new BlockEntityLoadEvent((WorldChunk) ((Object) this), blockEntity))) {
            ci.cancel();
        }
    }

    @Inject(method = "removeBlockEntity", at = @At("HEAD"), cancellable = true)
    private void removeBlockEntity(BlockPos pos, CallbackInfo ci) {
        if (!EventBus.INSTANCE.fireResult(new BlockEntityUnloadEvent((WorldChunk) ((Object) this), pos))) {
            ci.cancel();
        }
    }

    @Inject(method = "setBlockEntity", at = @At("HEAD"), cancellable = true)
    private void setBlockEntity(BlockEntity blockEntity, CallbackInfo ci) {
        if (!EventBus.INSTANCE.fireResult(new BlockEntityUpdateEvent((WorldChunk) ((Object) this), blockEntity))) {
            ci.cancel();
        }
    }

    @Inject(method = "setBlockState", at = @At("HEAD"), cancellable = true)
    public void setBlockState(BlockPos pos, BlockState newBlock, boolean moved,
                              CallbackInfoReturnable<BlockState> ci) {
        var oldBlock = ((WorldChunk) ((Object) this)).getBlockState(pos); //TODO: make with util shadowed mixin method
        if (!EventBus.INSTANCE.fireResult(new BlockChangeEvent((WorldChunk) ((Object) this), pos, oldBlock, newBlock, moved))) {
            ci.setReturnValue(oldBlock);
        }
    }

}
