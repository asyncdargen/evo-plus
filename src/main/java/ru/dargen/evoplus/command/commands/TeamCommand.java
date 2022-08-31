package ru.dargen.evoplus.command.commands;

import lombok.val;
import ru.dargen.evoplus.command.context.CommandContext;
import ru.dargen.evoplus.feature.impl.TeamWarFeature;
import ru.dargen.evoplus.command.Command;
import ru.dargen.evoplus.command.parameter.Parameter;
import ru.dargen.evoplus.command.parameter.type.TypeString;
import ru.dargen.evoplus.feature.Feature;

public class TeamCommand extends Command {

    public TeamCommand() {
        super("team", "Управление списком союзников", "t");
        subCommand(new TeamListCommand());
        subCommand(new TeamAddCommand());
        subCommand(new TeamRemoveCommand());
        subCommand(new TeamClearCommand());
    }

    private TeamWarFeature getFeature() {
        return Feature.TEAM_WAR_FEATURE;
    }

    @Override
    public void execute(CommandContext ctx) {
        ctx.sendMessage(getUsage());
    }

    public class TeamAddCommand extends Command {

        public TeamAddCommand() {
            super("add", "Добавить союзника");
            parameter(new Parameter<>("Ник", new TypeString()));
        }

        @Override
        public void execute(CommandContext ctx) {
            val name = ctx.<String>getArg(0).toLowerCase();
            val warList = getFeature().getWarList().getValue();
            val teamList = getFeature().getTeamList().getValue();

            if (teamList.contains(name)) ctx.sendMessage("§cИгрок уже является союзником.");
            else {
                val war = warList.remove(name);
                teamList.add(name);
                ctx.sendMessage("§aТеперь игрок §e%s§a - ваш союзник. %s", name, (war ? "§cПрежде он был вашим врагом!" : ""));
            }
        }
    }

    public class TeamRemoveCommand extends Command {

        public TeamRemoveCommand() {
            super("remove", "Удалить союзника");
            parameter(new Parameter<>("Ник", new TypeString()));
        }

        @Override
        public void execute(CommandContext ctx) {
            val name = ctx.<String>getArg(0).toLowerCase();
            val teamList = getFeature().getTeamList().getValue();

            if (!teamList.contains(name)) ctx.sendMessage("§cИгрок не является союзником.");
            else {
                teamList.remove(name);
                ctx.sendMessage("§cИгрок §e%s§c удален из списка союзников.", name);
            }
        }
    }

    public class TeamClearCommand extends Command {

        public TeamClearCommand() {
            super("clear", "Очистить список союзников");
        }

        @Override
        public void execute(CommandContext ctx) {
            val teamList = getFeature().getTeamList().getValue();

            teamList.clear();
            ctx.sendMessage("§aСписок союзников очищен.");
        }
    }

    public class TeamListCommand extends Command {

        public TeamListCommand() {
            super("list", "Список союзников");
        }

        @Override
        public void execute(CommandContext ctx) {
            val teamList = getFeature().getTeamList().getValue();

            if (teamList.isEmpty()) ctx.sendMessage("§cУ вас нет союзников.");
            else ctx.sendMessage("§aСписок ваших союзников - §f%s§a.", String.join("§a, §f", teamList));
        }
    }

}
