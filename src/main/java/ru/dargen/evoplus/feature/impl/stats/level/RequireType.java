package ru.dargen.evoplus.feature.impl.stats.level;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.ItemStack;
import ru.dargen.evoplus.EvoPlus;
import ru.dargen.evoplus.util.Util;
import ru.dargen.evoplus.util.formatter.DoubleFormatter;
import ru.dargen.evoplus.util.minecraft.ItemUtil;

@Getter
@RequiredArgsConstructor
public enum RequireType {

    ITEMS("") {
        @Override
        double getCurrentAmount(LevelRequire levelRequire) {
            if (Util.getPlayer() == null) return 0;
            return Util.getPlayer().getInventory().main
                    .stream()
                    .filter(item -> item != null && Util.stripColor(ItemUtil.getDisplayName(item)).equalsIgnoreCase(levelRequire.getName()))
                    .mapToInt(ItemStack::getCount)
                    .sum();
        }
    },
    BLOCKS("Блоки") {
        @Override
        double getCurrentAmount(LevelRequire levelRequire) {
            return EvoPlus.instance().getStats().getBlocks();
        }

        @Override
        public String formatNumber(double number) {
            return String.valueOf(((int) number));
        }
    },
    MONEY("Деньги") {
        @Override
        double getCurrentAmount(LevelRequire levelRequire) {
            return EvoPlus.instance().getStats().getMoney();
        }
    };

    private final String name;

    abstract double getCurrentAmount(LevelRequire levelRequire);

    public String formatNumber(double number) {
        return DoubleFormatter.format(number);
    }

    public static RequireType getByName(String name) {
        for (RequireType type : values())
            if (type.name.equalsIgnoreCase(name))
                return type;
        return ITEMS;
    }

}
