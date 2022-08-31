package ru.dargen.evoplus.command.parameter.type;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.dargen.evoplus.command.CommandParseException;

@AllArgsConstructor
@NoArgsConstructor
public class TypeLong implements Type<Long> {

    private Long min = Long.MIN_VALUE;
    private Long max = Long.MAX_VALUE;

    public Long parse(String input) throws CommandParseException {
        try {
            Long out = Long.parseLong(input);
            if (out > max)
                throw new CommandParseException("Число не должно быть больше §c" + max);
            else if (out < min)
                throw new CommandParseException("Число не должно быть меньше §c" + min);
            else return out;
        } catch (NumberFormatException e) {
            throw new CommandParseException("§c" + input + " §fне является числом");
        } catch (CommandParseException e) {
            throw e;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new CommandParseException("Произошла неизвестная ошибка, обратитесь к разработчикам");
        }
    }
}

