package ru.dargen.evoplus.feature.impl.stats.booster;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoosterType {

    NONE(1.0, 0),
    FIRST(1.1, 1_000),
    SECOND(1.15, 10_000),
    THIRD(1.2, 100_000),
    ;

    private final double booster;
    private final int blocks;

    public static BoosterType getByMultiplier(double multiplier) {
        for (BoosterType booster : values())
            if (booster.getBooster() == multiplier) return booster;
        return null;
    }
}
