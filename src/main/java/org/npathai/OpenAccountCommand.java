package org.npathai;

import java.util.List;
import java.util.Objects;

public class OpenAccountCommand implements Command {

    private final String command;
    private final AccountService accountService;

    public OpenAccountCommand(String command, AccountService accountService) {
        this.command = Objects.requireNonNull(command);
        this.accountService = Objects.requireNonNull(accountService);
    }

    @Override
    public List<String> execute() {
        String[] parts = command.split(" ");
        String accountHolderName = parts[2];
        Account account = accountService.createAccount(new CreateAccountRequest(accountHolderName));
        return List.of(account.accountNo());
    }
}
