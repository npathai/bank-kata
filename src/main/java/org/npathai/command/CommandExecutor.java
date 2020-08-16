package org.npathai.command;

import java.util.List;

public class CommandExecutor {

    private final CommandFactory commandFactory;

    public CommandExecutor(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    public List<String> executeCommand(String commandStr) {
        try {
            Command command = commandFactory.createCommand(commandStr);
            return command.execute();
        } catch (UnknownCommandException ex) {
            return List.of("Unknown command");
        }
    }
}
