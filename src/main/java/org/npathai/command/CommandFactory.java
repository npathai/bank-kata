package org.npathai.command;

import org.npathai.domain.account.AccountService;

public class CommandFactory {

    private AccountService accountService;

    public CommandFactory(AccountService accountService) {
        this.accountService = accountService;
    }

    public Command createCommand(String command) {
        if (command.startsWith("open account")) {
            return new OpenAccountCommand(command, accountService);
        } else if (command.startsWith("open zero balance account")) {
            return new OpenZeroBalanceAccountCommand(command, accountService);
        } else if (command.contains(" deposit ")) {
            return new DepositCommand(command, accountService);
        } else if (command.contains(" withdraw ")) {
            return new WithdrawCommand(command, accountService);
        } else if (command.contains(" statement")) {
            return new ShowStatementCommand(command, accountService);
        } else if (command.startsWith("transfer")) {
            return new TransferCommand(command, accountService);
        } else if (command.startsWith("close ")) {
            return new CloseCommand(command, accountService);
        } else {
            throw new UnknownCommandException();
        }
    }
}
