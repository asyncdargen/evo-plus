package ru.dargen.evoplus.command.parameter.type;

import ru.dargen.evoplus.command.CommandParseException;

public interface Type<T> {

    T parse(String input) throws CommandParseException;

}
