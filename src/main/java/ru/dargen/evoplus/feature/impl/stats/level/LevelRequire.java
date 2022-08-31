package ru.dargen.evoplus.feature.impl.stats.level;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

@Getter
@RequiredArgsConstructor
public class LevelRequire {

    protected final String name;
    protected final RequireType type;
    protected final double requiredAmount;

    public double getCurrentAmount() {
        return type.getCurrentAmount(this);
    }

    @Override
    public String toString() {
        val current = type.getCurrentAmount(this);
        return name + ": " + (current >= requiredAmount ? "§a" : "§c") + type.formatNumber(current) + "/" + type.formatNumber(requiredAmount);
    }
}