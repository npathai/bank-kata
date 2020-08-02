package org.npathai;

public class CommandFactory {
    public Command createCommand(String command) {
        if (command.startsWith("open account")) {
            return new OpenAccountCommand(command, new AccountService());
        } else if (command.contains(" deposit ")) {
            return new DepositCommand(command, new AccountService());
        } else if (command.contains(" withdraw ")) {
            return new WithdrawCommand(command, new AccountService());
        } else if (command.endsWith("statement")) {
            return new ShowStatementCommand(command, new AccountService());
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
