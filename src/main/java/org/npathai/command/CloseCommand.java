package org.npathai.command;

import org.npathai.domain.account.AccountService;
import org.npathai.domain.account.CloseRequest;

import java.util.List;

public class CloseCommand implements Command {
    private final String command;
    private final AccountService accountService;

    public CloseCommand(String command, AccountService accountService) {
        this.command = command;
        this.accountService = accountService;
    }

    @Override
    public List<String> execute() {
        String[] parts = command.split(" ");
        String accountNo = parts[1];
        accountService.close(new CloseRequest(accountNo));
        return List.of("Account closed");
    }
}
