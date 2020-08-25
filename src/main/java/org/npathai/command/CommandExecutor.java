package org.npathai.command;

public class CommandExecutor {

    private final CommandFactory commandFactory;

    public CommandExecutor(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    public CommandResponse executeCommand(String commandStr) {
        try {
            Command command = commandFactory.createCommand(commandStr);
            return command.executeNew();
        } catch (UnknownCommandException ex) {
            return new CommandResponse("Unknown command");
        }
    }
}
