package ru.dargen.evoplus.gui;

import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import ru.dargen.evoplus.util.Util;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
public abstract class GuiScreen extends Screen {

    protected final List<GuiElement> elements = new LinkedList<>();
    protected final AtomicBoolean initialized = new AtomicBoolean();

    protected GuiScreen() {
        super(Text.of(""));
    }

    @Override
    public void init(MinecraftClient client, int width, int height) {
        initialized.set(false);
        super.init(client, width, height);
        elements.clear();
        handleInit(width, height);
        initialized.set(true);
    }

    public void addElement(GuiElement element) {
        elements.add(element);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        if (!initialized.get()) return;

        try {
            handleDraw(matrixStack);

            for (GuiElement element : elements)
                element.draw(matrixStack, mouseX, mouseY);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public boolean keyPressed(int code, int ___, int modifiers) {
        if (!initialized.get()) return true;
        try {
            if (code == 256) close();
            else for (GuiElement element : elements) element.keyTyped(code);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!initialized.get()) return true;
        try {
            for (GuiElement element : elements)
                element.mousePressed(button, (int) mouseX, (int) mouseY);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!initialized.get()) return true;
        try {
            for (GuiElement element : elements)
                element.mouseReleased(button, (int) mouseX, (int) mouseY);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return true;
    }

    @Override
    public void removed() {
        handleClose();
    }

    abstract public void handleClose();

    abstract public void handleInit(int width, int height);

    public void handleDraw(MatrixStack matrixStack) {

    }

    public void display() {
        Util.getClient().openScreen(this);
    }

    public void close() {
        onClose();
    }

}
