package ru.dargen.evoplus.event.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.util.math.MatrixStack;
import ru.dargen.evoplus.event.Event;

@Getter
@AllArgsConstructor
public class HudRenderEvent extends Event {

    protected MatrixStack matrixStack;
    protected float tickDelta;


}
