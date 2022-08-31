package ru.dargen.evoplus.gui.element;

import lombok.Data;
import lombok.experimental.Accessors;
import net.minecraft.client.util.math.MatrixStack;
import ru.dargen.evoplus.gui.GuiElement;
import ru.dargen.evoplus.util.minecraft.Render;
import ru.dargen.evoplus.util.Util;

import java.util.function.Consumer;

@Data @Accessors(chain = true)
public class GuiButtonElement implements GuiElement {

    public static int
            DEFAULT_COLOR = Util.rgb(23, 24, 31),
            HOVERED_COLOR = Util.rgb(56, 57, 65),
            DISABLED_COLOR = Util.rgb(38, 72, 140);

    protected String text;
    protected Consumer<GuiButtonElement> clickHandler;
    protected int x, y, width, height;
    protected boolean visible = true, hovered, enabled = true;

    public GuiButtonElement(String text, int x, int y, int width, int height, Consumer<GuiButtonElement> clickHandler) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.clickHandler = clickHandler;
    }

    public GuiButtonElement(String text, int x, int y, int width, int height) {
        this(text, x, y, width, height, null);
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
    public void draw(MatrixStack matrixStack, int mx, int my) {
        if (!visible)
            hovered = false;
        else {
            hovered = isInside(mx, my);
            Render.fill(matrixStack, x, y, width, height, enabled ? hovered ? HOVERED_COLOR : DEFAULT_COLOR : DISABLED_COLOR);
            Render.drawCenteredStringWithShadow(matrixStack, text, x + width / 2, y + height / 2 - Render.getStringHeight() / 2, -1);
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
        if (clickHandler != null && hovered && enabled)
            clickHandler.accept(this);
    }

}
