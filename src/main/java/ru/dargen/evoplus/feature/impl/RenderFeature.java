package ru.dargen.evoplus.feature.impl;

import lombok.Getter;
import ru.dargen.evoplus.feature.Feature;
import ru.dargen.evoplus.feature.setting.BooleanSetting;

@Getter
public class RenderFeature extends Feature {

    protected BooleanSetting fullBright = BooleanSetting.builder()
            .id("full-bright")
            .name("Максимальная яркость")
            .build();

//    protected BooleanSetting blindness = BooleanSetting.builder()
//            .id("blindness")
//            .name("Ослепление")
//            .value(false)
//            .build();

    protected BooleanSetting scores = BooleanSetting.builder()
            .id("scores")
            .name("Номера линий в скорборде")
            .value(false)
            .build();

    protected BooleanSetting scoreboard = BooleanSetting.builder()
            .id("scoreboard")
            .name("Отображение скорборда")
            .build();

    protected BooleanSetting foodAirArmor = BooleanSetting.builder()
            .id("food-armor")
            .name("Отображение еды, воздуха и брони")
            .value(false)
            .build();

    protected BooleanSetting healthBar = BooleanSetting.builder()
            .id("health-bar")
            .name("Кастомная строка здоровья")
            .value(false)
            .build();

    public RenderFeature() {
        super("Рендер", "render");
        register();
    }

}
