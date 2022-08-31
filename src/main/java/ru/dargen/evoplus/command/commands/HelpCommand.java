package ru.dargen.evoplus.command.commands;

import ru.dargen.evoplus.command.context.CommandContext;
import ru.dargen.evoplus.EvoPlus;
import ru.dargen.evoplus.command.Command;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", "Помощь по командам");
    }

    @Override
    public void execute(CommandContext ctx) {
        EvoPlus.instance().getCommandManager().getRegisteredCommands().values().forEach(command -> sendMessage(command.getUsage()));
    }

}
