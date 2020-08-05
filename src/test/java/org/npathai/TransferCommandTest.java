package org.npathai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

class TransferCommandTest {
    private final Account fromAccount = new Account("Alice");
    private final Account toAccount = new Account("Bob");

    @Mock
    AccountService accountService;
    private TransferCommand transferCommand;
    private String command;

    @BeforeEach
    public void initialize() {
        MockitoAnnotations.initMocks(this);
        // ensure initial balance
        fromAccount.deposit(1000);
        command = String.format("transfer %s %s %d", fromAccount.accountNo(), toAccount.accountNo(), 1000);
        transferCommand = new TransferCommand(command, accountService);
    }

    @Test
    public void transfersAmountFromSourceToDestinationAccount() {
        transferCommand.execute();
        verify(accountService).transfer(fromAccount.accountNo(), toAccount.accountNo(), 1000);
    }

    @Test
    public void returnsNothing() {
        transferCommand = new TransferCommand(command, accountService);
        assertThat(transferCommand.execute()).isEmpty();
    }
}