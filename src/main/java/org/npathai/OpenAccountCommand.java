package org.npathai;

import java.util.List;

public class OpenAccountCommand implements Command {

    private final String command;
    private final AccountService accountService;

    public OpenAccountCommand(String command, AccountService accountService) {
        this.command = command;
        this.accountService = accountService;
    }

    @Override
    public List<String> execute() {
        String[] parts = command.split(" ");
        String accountHolderName = parts[2];
        Account account = accountService.createAccount(accountHolderName);
        return List.of(account.accountNo());
    }
}
