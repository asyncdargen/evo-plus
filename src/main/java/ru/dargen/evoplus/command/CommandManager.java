package ru.dargen.evoplus.command;

import lombok.Getter;
import lombok.val;
import ru.dargen.evoplus.EvoPlus;
import ru.dargen.evoplus.event.chat.ChatSendEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
public class CommandManager {

    private final Map<String, Command> registeredCommands = new HashMap<>();

    public CommandManager(EvoPlus mod) {
        mod.getEventBus().register(ChatSendEvent.class, event -> {
            var text = event.getText();
            if (!text.startsWith("/")) return;
            val args = text.split(" ");
            val name = args[0].toLowerCase().substring(1);
            for (Command command : registeredCommands.values()) {
                if (command.getName().equalsIgnoreCase(name) || command.getAliases().contains(name)) {
                    command.handle(args.length == 1 ? new String[0] : Arrays.copyOfRange(args, 1, args.length));
                    event.setCancelled(true);
                    break;
                }
            }
        });
    }

    public void registerCommands(Command... commands) {
        Arrays.stream(commands).forEach(this::registerCommand);
    }

    public void registerCommand(Command command) {
        registeredCommands.put(command.getName().toLowerCase(), command);
    }

}
