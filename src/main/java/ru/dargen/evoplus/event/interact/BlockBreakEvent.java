package ru.dargen.evoplus.event.interact;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import ru.dargen.evoplus.event.Event;

@Getter
@RequiredArgsConstructor
public class BlockBreakEvent extends Event {

    protected final BlockPos position;
    protected final BlockState blockState;

}
