package ru.dargen.evoplus.command.parameter.type;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.dargen.evoplus.command.CommandParseException;

@NoArgsConstructor
@AllArgsConstructor
public class TypeDouble implements Type<Double> {

    private Double min = Double.MIN_VALUE;
    private Double max = Double.MAX_VALUE;

    public Double parse(String input) throws CommandParseException {
        try {
            Double out = Double.parseDouble(input);
            if (out > max)
                throw new CommandParseException("Число не должно быть больше §c" + max);
            else if (out < min)
                throw new CommandParseException("Число не должно быть меньше §c" + min);
            else return out;
        } catch (NumberFormatException e) {
            throw new CommandParseException("§c" + input + " §fне является десятичным числом");
        } catch (CommandParseException e) {
            throw e;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new CommandParseException("Произошла неизвестная ошибка, обратитесь к разработчикам");
        }
    }
    
}
