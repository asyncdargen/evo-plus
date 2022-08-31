package ru.dargen.evoplus.gui.element;

import lombok.Data;
import lombok.experimental.Accessors;
import net.minecraft.client.util.math.MatrixStack;
import ru.dargen.evoplus.gui.GuiElement;
import ru.dargen.evoplus.util.minecraft.Render;

@Data @Accessors(chain = true)
public class GuiLabelElement implements GuiElement {

    protected String text;
    protected float scale = 1f;
    protected int x, y;
    protected boolean visible = true, hovered, enabled = true;

    public GuiLabelElement(String text, int x, int y, float scale) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.scale = scale;
    }

    public GuiLabelElement(String text, int x, int y) {
        this(text, x, y, 1f);
    }

    @Override
    public GuiElement setPos(int x, int y) {
        return setX(x).setY(y);
    }

    @Override
    public GuiElement setSize(int width, int height) {
        return this;
    }

    @Override
    public void draw(MatrixStack matrixStack, int mx, int my) {
        if (!visible)
            hovered = false;
        else {
            hovered = isInside(mx, my);
            Render.scaledRunner(scale, (__, ___) -> Render.drawStringWithShadow(matrixStack, text, (int) (x / scale), (int) (y / scale), -1));
        }
    }

    @Override
    public void keyTyped(int i) {

    }

    @Override
    public void mouseReleased(int button, int mx, int my) {

    }

    @Override
    public void mousePressed(int button, int mx, int my) {

    }

    @Override
    public GuiElement setWidth(int width) {
        return this;
    }

    @Override
    public GuiElement setHeight(int height) {
        return this;
    }

    @Override
    public int getWidth() {
        return Render.getStringWidth(text == null ? "" : text);
    }

    @Override
    public int getHeight() {
        return Render.getStringHeight();
    }

}
