package ru.dargen.evoplus.util.formatter;

import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class TimeFormatter {

    private final Map<String, Long> MODIFIERS = new HashMap<>();
    private final Long[] VALUES = {2419200L, 604800L, 86400L, 3600L, 60L, 1L};
    private final String[] FORMATS = {" %s мес.", " %s нед.", " %s дн.", " %s час.", " %s мин.", " %s сек." };

    static {
        MODIFIERS.put("дн.", 86_400_000L);
        MODIFIERS.put("ч.", 3_600_000L);
        MODIFIERS.put("мин.", 60_000L);
        MODIFIERS.put("сек.", 1000L);
    }

    public String formatText(long time) {
        String result = "";
        time /= 1000L;
        if (time == 0) return "сейчас";
        long temp;
        for (int i = 0; i < VALUES.length; i++) {
            if ((temp = time / VALUES[i]) >= 1L) {
                result += String.format(FORMATS[i], temp);
                time -= temp * VALUES[i];
            }
        }
        return result.trim();
    }

    public long parseText(String input) {
        long time = 0L;
        val args = input.split(" ");
        if (args.length % 2 != 0) return 0;

        for (int i = 0; i < args.length; i += 2) {
            val element = Integer.parseInt(args[i]);
            val modifier = MODIFIERS.getOrDefault(args[i + 1], 1L);
            time += element * modifier;
        }

        return time;
    }

}
