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
            return new CommandResponse(List.of("Payee account is closed. Amount will be reversed back to your account."));
        } catch (InsufficientFundsException ex) {
            return new CommandResponse(List.of("Insufficient funds in account"));
        } catch (AccountUnderflowException ex) {
            return new CommandResponse(List.of("Must maintain minimum balance of " + ex.minBalance()));
        }
        return new CommandResponse(Collections.emptyList());
    }
}
