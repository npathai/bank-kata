package org.npathai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

class TransferCommandTest {
    final Account fromAccount = new Account("Alice");
    final Account toAccount = new Account("Bob");

    @Mock
    AccountService accountService;
    @Captor
    ArgumentCaptor<TransferRequest> requestArgumentCaptor;
    TransferCommand transferCommand;
    String command;

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

        verify(accountService).transfer(requestArgumentCaptor.capture());
        assertThat(requestArgumentCaptor.getValue().fromAccountNo()).isEqualTo(fromAccount.accountNo());
        assertThat(requestArgumentCaptor.getValue().toAccountNo()).isEqualTo(toAccount.accountNo());
        assertThat(requestArgumentCaptor.getValue().amount()).isEqualTo(1000);
    }

    @Test
    public void returnsNothing() {
        transferCommand = new TransferCommand(command, accountService);
        assertThat(transferCommand.execute()).isEmpty();
    }
}