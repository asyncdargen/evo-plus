package ru.dargen.evoplus.util.formatter;

import lombok.experimental.UtilityClass;
import lombok.val;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

@UtilityClass
public class DoubleFormatter {

    public final DecimalFormat FORMAT = new DecimalFormat("###.##");
    public final Map<String, Double> MODIFIERS = new LinkedHashMap<>();

    static {
        MODIFIERS.put("T", 1_000_000_000_000.0);
        MODIFIERS.put("B", 1_000_000_000.0);
        MODIFIERS.put("M", 1_000_000.0);
        MODIFIERS.put("K", 1_000.0);
    }

    public String format(double number) {
        String suffix = "";
        for (Map.Entry<String, Double> modifierEntry : MODIFIERS.entrySet()) {
            if (number / modifierEntry.getValue() >= 1) {
                suffix = modifierEntry.getKey();
                number /= modifierEntry.getValue();
                break;
            }
        }

        String formatted = FORMAT.format(number);

        while (formatted.contains(".") && (formatted.endsWith("0") || formatted.endsWith(".")))
            formatted = formatted.substring(0, formatted.length() - 1);

        return formatted + suffix;
    }

    public double parse(String number) {
        val end = number.charAt(number.length() - 1);

        if (Character.isDigit(end))
            return Double.parseDouble(number);

        val modifier = MODIFIERS.getOrDefault(String.valueOf(end).toUpperCase(), 1.0);
        val amount = Double.parseDouble(number.substring(0, number.length() - 1));

        return amount * modifier;
    }

}
