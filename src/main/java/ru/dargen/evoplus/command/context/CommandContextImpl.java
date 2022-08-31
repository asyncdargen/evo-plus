package ru.dargen.evoplus.command.context;

import lombok.*;
import lombok.experimental.Accessors;
import ru.dargen.evoplus.command.Command;

import java.util.List;

@Getter
@AllArgsConstructor
public class CommandContextImpl implements CommandContext {

    private final Command command;

    private final List<String> originalArgs;
    private final List<Object> args;

    public boolean hasArg(int index) {
        return args.size() - 1 >= index;
    }

    public <T> T getArg(int index) {
        return hasArg(index) ? (T) args.get(index) : null;
    }

    public void sendMessage(String message, Object... obj) {
        command.sendMessage(message, obj);
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ContextBuilder {

        private final Command command;
        private List<String> originalArgs;
        private List<Object> args;

        public CommandContext build() {
            return new CommandContextImpl(command, originalArgs, args);
        }

    }

}
