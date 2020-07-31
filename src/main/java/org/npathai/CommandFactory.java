package org.npathai;

public class CommandFactory {
    public Command createCommand(String command) {
        if (command.startsWith("open account")) {
            return new OpenAccountCommand(command);
        } else if (command.contains(" deposit ")) {
            return new DepositCommand(command);
        } else if (command.contains(" withdraw ")) {
            return new WithdrawCommand(command);
        } else if (command.endsWith("statement")) {
            return new ShowStatementCommand(command);
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
