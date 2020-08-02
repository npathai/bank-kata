package org.npathai;

import java.util.Collections;
import java.util.List;

public class DepositCommand implements Command {
    private final String command;
    private final AccountService accountService;

    public DepositCommand(String command, AccountService accountService) {
        this.command = command;
        this.accountService = accountService;
    }

    @Override
    public List<String> execute() {
        String[] parts = command.split(" ");
        String accountNo = parts[0];
        int amount = Integer.parseInt(parts[2]);
        accountService.depositAccount(accountNo, amount);
        return Collections.emptyList();
    }
}
