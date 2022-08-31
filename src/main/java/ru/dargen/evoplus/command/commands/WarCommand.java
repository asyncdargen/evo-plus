package ru.dargen.evoplus.command.commands;

import lombok.val;
import ru.dargen.evoplus.command.context.CommandContext;
import ru.dargen.evoplus.command.parameter.type.TypeString;
import ru.dargen.evoplus.feature.Feature;
import ru.dargen.evoplus.feature.impl.TeamWarFeature;
import ru.dargen.evoplus.command.Command;
import ru.dargen.evoplus.command.parameter.Parameter;

public class WarCommand extends Command {

    public WarCommand() {
        super("war", "Управление списком врагов", "w");
        subCommand(new TeamListCommand());
        subCommand(new WarAddCommand());
        subCommand(new WarRemoveCommand());
        subCommand(new WarClearCommand());
    }

    private TeamWarFeature getFeature() {
        return Feature.TEAM_WAR_FEATURE;
    }

    @Override
    public void execute(CommandContext ctx) {
        ctx.sendMessage(getUsage());
    }

    public class WarAddCommand extends Command {

        public WarAddCommand() {
            super("add", "Добавить врага");
            parameter(new Parameter<>("Ник", new TypeString()));
        }

        @Override
        public void execute(CommandContext ctx) {
            val name = ctx.<String>getArg(0).toLowerCase();
            val warList = getFeature().getWarList().getValue();
            val teamList = getFeature().getTeamList().getValue();

            if (warList.contains(name)) ctx.sendMessage("§cИгрок уже является врагом.");
            else {
                val team = teamList.remove(name);
                warList.add(name);
                ctx.sendMessage("§aТеперь игрок §e%s§a - ваш враг. %s", name, (team ? "§cПрежде он был вашим союзником!" : ""));
            }
        }
    }

    public class WarRemoveCommand extends Command {

        public WarRemoveCommand() {
            super("remove", "Удалить врага");
            parameter(new Parameter<>("Ник", new TypeString()));
        }

        @Override
        public void execute(CommandContext ctx) {
            val name = ctx.<String>getArg(0).toLowerCase();
            val warList = getFeature().getWarList().getValue();

            if (!warList.contains(name)) ctx.sendMessage("§cИгрок не является врагом.");
            else {
                warList.remove(name);
                ctx.sendMessage("§cИгрок §e%s§c удален из списка врагов.", name);
            }
        }
    }

    public class WarClearCommand extends Command {

        public WarClearCommand() {
            super("clear", "Очистить список врагов");
        }

        @Override
        public void execute(CommandContext ctx) {
            val warList = getFeature().getWarList().getValue();

            warList.clear();
            ctx.sendMessage("§aСписок врагов очищен.");
        }
    }

    public class TeamListCommand extends Command {

        public TeamListCommand() {
            super("list", "Список врагов");
        }

        @Override
        public void execute(CommandContext ctx) {
            val teamList = getFeature().getWarList().getValue();

            if (teamList.isEmpty()) ctx.sendMessage("§cУ вас нет врагов.");
            else ctx.sendMessage("§aСписок ваших врагов - §f%s§a.", String.join("§a, §f", teamList));
        }
    }

}
