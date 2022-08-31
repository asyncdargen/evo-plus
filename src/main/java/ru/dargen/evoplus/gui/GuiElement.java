package ru.dargen.evoplus.gui;

import net.minecraft.client.util.math.MatrixStack;

public interface GuiElement {

    int getX();

    int getY();

    GuiElement setX(int x);

    GuiElement setY(int y);

    GuiElement setPos(int x, int y);

    boolean isVisible();

    GuiElement setVisible(boolean visible);

    boolean isEnabled();

    GuiElement setEnabled(boolean enabled);

    boolean isHovered();

    void keyTyped(int i);

    void draw(MatrixStack matrixStack, int mx, int my);

    void mouseReleased(int button, int mx, int my);

    void mousePressed(int button, int mx, int my);

    GuiElement setWidth(int width);

    GuiElement setHeight(int height);

    int getWidth();

    int getHeight();

    GuiElement setSize(int width, int height);

    default boolean isInside(int mx, int my) {
        return mx >= getX() && my >= getY() && mx <= getX() + getWidth() && my <= getY() + getHeight();
    }

}
