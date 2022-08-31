package ru.dargen.evoplus.gui.element;

import lombok.Data;
import lombok.experimental.Accessors;
import net.minecraft.client.util.math.MatrixStack;
import ru.dargen.evoplus.util.minecraft.Render;
import ru.dargen.evoplus.gui.GuiElement;

@Data @Accessors(chain = true)
public class GuiRectangleElement implements GuiElement {

    protected int x, y, width, height, color;
    protected boolean visible = true, hovered, enabled = true;

    public GuiRectangleElement(int color, int x, int y, int width, int height) {
        this.color = color;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public GuiElement setPos(int x, int y) {
        return setX(x).setY(y);
    }

    @Override
    public GuiElement setSize(int width, int height) {
        return setWidth(width).setHeight(height);
    }

    @Override
    public void keyTyped(int i) {

    }

    @Override
    public void draw(MatrixStack matrixStack, int mx, int my) {
        if (!visible)
            hovered = false;
        else Render.fill(matrixStack, x, y, width, height, color);
    }

    @Override
    public void mouseReleased(int button, int mx, int my) {

    }

    @Override
    public void mousePressed(int button, int mx, int my) {

    }

}
