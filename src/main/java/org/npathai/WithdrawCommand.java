package org.npathai;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class WithdrawCommand implements Command {
    private final String command;
    private final AccountService accountService;

    public WithdrawCommand(String command, AccountService accountService) {
        this.command = Objects.requireNonNull(command);
        this.accountService = Objects.requireNonNull(accountService);
    }

    @Override
    public List<String> execute() {
        String[] parts = command.split(" ");
        String accountNo = parts[0];
        int amount = Integer.parseInt(parts[2]);
        try {
            accountService.withdrawAccount(accountNo, amount);
            return Collections.emptyList();
        } catch (AccountClosedException ex) {
            return List.of("Account is closed, cannot make any transaction");
        }
    }
}
