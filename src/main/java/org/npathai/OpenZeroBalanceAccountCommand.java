package org.npathai;

import java.util.List;

public class OpenZeroBalanceAccountCommand implements Command {

    private final String command;
    private final AccountService accountService;

    public OpenZeroBalanceAccountCommand(String command, AccountService accountService) {
        this.command = command;
        this.accountService = accountService;
    }

    @Override
    public List<String> execute() {
        String[] parts = command.split(" ");
        String accountHolderName = parts[4];
        Account account = accountService.createAccount(new CreateAccountRequest(accountHolderName, true));
        return List.of(account.accountNo());
    }
}
