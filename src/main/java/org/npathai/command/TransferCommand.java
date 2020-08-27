package org.npathai.command;

import org.npathai.domain.account.*;

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
    public CommandResponse execute() {
        String[] parts = command.split(" ");
        String fromAccountNo = parts[1];
        String toAccountNo = parts[2];
        int amount = Integer.parseInt(parts[3]);
        try {
            accountService.transfer(new TransferRequest(fromAccountNo, toAccountNo, amount));
        } catch (TransferFailedException ex) {
            if (ex.getCause() instanceof AccountClosedException) {
                return CommandResponse.of("Payee account is closed. Amount will be reversed back to your account.");
            } else if (ex.getCause() instanceof InsufficientFundsException) {
                return CommandResponse.of("Insufficient funds in account");
            } else if (ex.getCause() instanceof AccountUnderflowException) {
                AccountUnderflowException cause = (AccountUnderflowException) ex.getCause();
                return CommandResponse.of("Must maintain minimum balance of " + cause.minBalance());
            }
        }
        return CommandResponse.of(Collections.emptyList());
    }
}
