package ru.dargen.evoplus.feature;

import lombok.val;
import lombok.var;
import ru.dargen.evoplus.EvoPlus;
import ru.dargen.evoplus.feature.setting.Setting;
import ru.dargen.evoplus.gui.GuiScreen;
import ru.dargen.evoplus.gui.element.GuiButtonElement;
import ru.dargen.evoplus.gui.element.GuiLabelElement;
import ru.dargen.evoplus.gui.element.GuiRectangleElement;
import ru.dargen.evoplus.util.Util;
import ru.dargen.evoplus.util.minecraft.Render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class FeaturesGuiScreen extends GuiScreen {

    private static int selectorCursor;

    @Override
    public void handleInit(int width, int height) {

        addElement(new GuiLabelElement(EvoPlus.NAME, 4, 4, 1.5f));
        val features = EvoPlus.instance().getFeatureManager().getFeatures();

        drawSelector(width, features);

        val feature = ((Feature) features.toArray()[selectorCursor]);

        drawSettingsWindow(width, getSettingsStartY(features), feature);
//        Collections.reverse(elements);
    }

    private void drawSettingsWindow(int width, int startY, Feature feature) {
        val settings = feature.settings.stream().filter(Setting::isDrawable).collect(Collectors.toList());
        addElement(new GuiRectangleElement(Util.rgb(0, 0, 0, 100), width / 2 - 215, startY, 430, settings.size() * 30 + 10));

        for (int i = 0; i < settings.size(); i++) {
            val setting = settings.get(i);
            val name = setting.getName();
            val element = setting.getElement();
            addElement(new GuiLabelElement(name, width / 2 - 205, startY + 20 + i * 30 - Render.getStringHeight() / 2));
            addElement(element.setPos(width / 2 + 205 - element.getWidth(), startY + 10 + 30 * i));
        }
    }

    private void drawSelector(int width, Set<Feature> features) {
        val index = new AtomicInteger();
        val cunked = new ArrayList<Map<Integer, Feature>>();
        Map<Integer, Feature> map = new HashMap<>();
        for (Feature feature : features) {
            map.put(index.getAndIncrement(), feature);
            if (index.get() % 4 == 0 || index.get() == features.size())
                cunked.add(map);

            if (index.get() % 4 == 0)
                map = new HashMap<>();
        }
        index.set(0);
        val elementIndex = new AtomicInteger();
        cunked.forEach(sectionLine -> {
            sectionLine.forEach((i, feature) -> {
                addElement(
                        new GuiButtonElement(feature.name, width / 2 - (sectionLine.size() * 110 - 10) / 2 + elementIndex.getAndIncrement() * 110, 20 + index.get() * 30, 100, 20)
                                .setClickHandler(element -> updateSelector(i))
                                .setEnabled(i != selectorCursor)
                );
            });
            index.getAndIncrement();
            elementIndex.set(0);
        });
    }

    private int getSettingsStartY(Set<Feature> features) {
        return 30 + (features.size() / 4 + (features.size() % 4 != 0 ? 1 : 0)) * 30;
    }

    private void updateSelector(int selectorCursor) {
        FeaturesGuiScreen.selectorCursor = selectorCursor;
        new FeaturesGuiScreen().display();
    }

    @Override
    public void handleClose() {
        EvoPlus.instance().getFeatureManager().saveSettings();
    }

}
