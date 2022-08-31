package ru.dargen.evoplus.command.parameter.type;

import ru.dargen.evoplus.command.CommandParseException;

public class TypeBoolean implements Type<Boolean> {

    public Boolean parse(String input) throws CommandParseException {
        return input.equals("1") || input.equalsIgnoreCase("t") || Boolean.parseBoolean(input);
    }

}
