package org.npathai;

public class CommandFactory {

    private AccountService accountService;

    public CommandFactory(AccountService accountService) {
        this.accountService = accountService;
    }

    public Command createCommand(String command) {
        if (command.startsWith("open account")) {
            return new OpenAccountCommand(command, accountService);
        } else if (command.contains(" deposit ")) {
            return new DepositCommand(command, accountService);
        } else if (command.contains(" withdraw ")) {
            return new WithdrawCommand(command, accountService);
        } else if (command.endsWith("statement")) {
            return new ShowStatementCommand(command, accountService);
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
