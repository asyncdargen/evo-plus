package ru.dargen.evoplus.command.parameter.type;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.dargen.evoplus.command.CommandParseException;

@AllArgsConstructor
@NoArgsConstructor
public class TypeTime implements Type<Long> {

    private Long min = Long.MIN_VALUE;
    private Long max = Long.MAX_VALUE;

    public Long /*in seconds*/ parse(String input) throws CommandParseException {
        long out;
        try {
            out =  Long.parseLong(input);
        } catch (NumberFormatException e) {
            int mp = parseMp(input.substring(input.length() - 1));
            try {
                out = Long.parseLong(input.substring(0, input.length() - 1)) * mp;
            } catch (NumberFormatException exc) {
                throw new CommandParseException("§c" + input + " §fне является числом");
            } catch (CommandParseException exc) {
                throw e;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            throw new CommandParseException("Произошла неизвестная ошибка, обратитесь к разработчикам");
        }
        if (out > max)
            throw new CommandParseException("Время не должно быть меньше §c" + max + "§f сек.");
        if (out < min)
            throw new CommandParseException("Время не должно быть больше §c" + min + "§f сек.");

        return out;
    }

    public int parseMp(String mp) {
        switch (mp) {
            case "s": return 1;
            case "m": return 60;
            case "h": return 3600;
            case "d":
            case "D": return 86400;
            case "M": return 2592000;
            case "Y": return 31536000;
            default:
                throw new CommandParseException("Неизвезтный множитель §c" + mp);
        }
    }

}
