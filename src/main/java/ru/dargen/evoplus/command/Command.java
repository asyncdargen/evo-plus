package ru.dargen.evoplus.command;

import lombok.Getter;
import ru.dargen.evoplus.command.context.CommandContext;
import ru.dargen.evoplus.command.parameter.Parameter;
import ru.dargen.evoplus.EvoPlus;
import ru.dargen.evoplus.util.Util;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public abstract class Command {

    protected final List<Command> subCommands = new LinkedList<>();
    protected final List<Parameter<?>> parameters = new LinkedList<>();

    protected final String name;
    protected String prefix = EvoPlus.PREFIX;
    protected String description;
    protected List<String> aliases;

    public Command(String name) {
        this(name, "Неизвестная команда");
    }

    public Command(String name, String description, String... aliases) {
        this.name = name;
        description(description).aliases(aliases);
    }

    public abstract void execute(CommandContext ctx);

    //Config methods

    public final Command aliases(List<String> aliases) {
        this.aliases = aliases == null ? Collections.emptyList() : aliases;
        return this;
    }

    public final Command aliases(String... aliases) {
        return aliases(Arrays.asList(aliases));
    }

    public final Command description(String description) {
        this.description = description == null ? "" : description;
        return this;
    }

    public final Command prefix(String prefix) {
        this.prefix = prefix == null ? "" : prefix;
        return this;
    }

    public final Command subCommand(Command subCommand) {
        subCommands.add(subCommand);
        return this;
    }

    public final Command parameter(Parameter<?> parameter) {
        if (!parameters.isEmpty() && !parameters.get(parameters.size() - 1).isRequired() && parameter.isRequired())
            throw new IllegalStateException("can`t add required parameter after non-required");
        parameters.add(parameter);
        return this;
    }

    //Minecraft handlers

    protected final void handle(String[] args) {
        if (args.length > 0) {
            String subCommand = args[0];
            for (Command command : subCommands) {
                if (command.getName().equalsIgnoreCase(subCommand)
                        || command.getAliases().stream().anyMatch(a -> a.equalsIgnoreCase(subCommand))) {
                    command.handle(Arrays.copyOfRange(args, 1, args.length));
                    return;
                }
            }
        }

        List<Object> parsedParameters = new LinkedList<>();
        try {
            for (int i = 0; i < parameters.size(); i++) {
                Parameter<?> param = parameters.get(i);
                if (args.length - 1 < i) {
                    if (!param.isRequired())
                        continue;
                    throw new CommandParseException("Укажите §c" + param.getName());
                }
                parsedParameters.add(param.getType().parse(args[i]));
            }
            execute(
                    CommandContext.builder(this)
                            .args(parsedParameters)
                            .originalArgs(Arrays.asList(args))
                            .build()
            );
        } catch (CommandParseException e) {
            sendMessage(e.getMessage());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    //Util methods

    public String getUsage() {
        StringBuilder builder = new StringBuilder("§f/").append(name).append(" ");

        getParameters().stream()
                .map(parameter ->
                        "§7" + (parameter.isRequired() ? "<" : "[")
                                + "§a" + parameter.getName().toUpperCase()
                                + "§7" + (parameter.isRequired() ? "> " : "] ")
                )
                .forEach(builder::append);

        builder.append("§7- ").append(description);

        if (!subCommands.isEmpty()) {
            builder.append("\n").append(prefix).append("§f/").append(name);
            builder.append(" §7[§a");
            builder.append(
                    subCommands.stream()
                            .map(Command::getName)
                            .collect(Collectors.joining("§7/§a"))
            );
            builder.append("§7]");
        }

        return builder.toString();
    }

    public Command sendMessage(String message, Object... params) {
        Util.printMessage(String.format(prefix + message, params).replace("&", "§"));
        return this;
    }

}
