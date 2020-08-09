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
        try {
            accountService.transfer(new TransferRequest(fromAccountNo, toAccountNo, amount));
        } catch (TransferFailedException ex) {
            // FIXME bad that we are swallowing exception. Add acceptance test to give feedback for failure
        } catch (InsufficientFundsException ex) {
            return List.of("Insufficient funds in account");
        }
        return Collections.emptyList();
    }
}
