package org.npathai;

import java.util.Collections;
import java.util.List;

public class CommandExecutor {

    private final CommandFactory commandFactory;

    public CommandExecutor(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    public List<String> executeCommand(String commandStr) {
        Command command = commandFactory.createCommand(commandStr);
        if (command == null) {
            return Collections.emptyList();
        }
        return command.execute(commandStr);
    }
}
