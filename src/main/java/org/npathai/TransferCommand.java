package org.npathai;

import java.util.Collections;
import java.util.List;

public class TransferCommand implements Command {
    private final String command;
    private final AccountService accountService;

    public TransferCommand(String command, AccountService accountService) {
        this.command = command;
        this.accountService = accountService;
    }

    @Override
    public List<String> execute() {
        String[] parts = command.split(" ");
        String fromAccountNo = parts[1];
        String toAccountNo = parts[2];
        int amount = Integer.parseInt(parts[3]);
        accountService.transfer(fromAccountNo, toAccountNo, amount);
        return Collections.emptyList();
    }
}