package ru.dargen.evoplus.feature.impl.boss;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum BossRenderOrder {

    TIME("По времени", Comparator.comparingLong(info -> info.getKey().getTime() - (System.currentTimeMillis() - info.getValue()))),
    LEVEL("По уровню", Comparator.comparingLong(info -> info.getKey().getLevel()));

    private final String name;
    private final Comparator<Map.Entry<BossType, Long>> comparator;

    @Override
    public String toString() {
        return name;
    }
}
