package org.npathai.command;

import org.npathai.command.Command;
import org.npathai.domain.account.Account;
import org.npathai.domain.account.AccountService;
import org.npathai.domain.account.CreateAccountRequest;

import java.util.List;

public class OpenZeroBalanceAccountCommand implements Command {

    private final String command;
    private final AccountService accountService;

    public OpenZeroBalanceAccountCommand(String command, AccountService accountService) {
        this.command = command;
        this.accountService = accountService;
    }

    @Override
    public CommandResponse execute() {
        String[] parts = command.split(" ");
        String accountHolderName = parts[4];
        Account account = accountService.createAccount(new CreateAccountRequest(accountHolderName, true));
        return new CommandResponse(List.of(account.accountNo()));
    }
}
