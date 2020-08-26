package org.npathai.command;

import org.npathai.domain.account.AccountClosedException;
import org.npathai.domain.account.AccountService;
import org.npathai.domain.account.AccountUnderflowException;
import org.npathai.domain.account.WithdrawRequest;

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
    public CommandResponse execute() {
        String[] parts = command.split(" ");
        String accountNo = parts[0];
        int amount = Integer.parseInt(parts[2]);
        try {
            accountService.withdrawAccount(new WithdrawRequest(accountNo, amount));
            return CommandResponse.of(List.of("Successfully withdrawn Rs " + amount));
        } catch (AccountClosedException ex) {
            return CommandResponse.of(List.of("Account is closed, cannot make any transaction"));
        } catch (AccountUnderflowException ex) {
            return CommandResponse.of(List.of("Must maintain minimum balance of " + ex.minBalance()));
        }
    }
}
