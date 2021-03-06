package org.npathai.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.npathai.domain.account.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

class TransferCommandTest {
    final Account fromAccount = new Account("Alice", Account.MIN_BALANCE);
    final Account toAccount = new Account("Bob", Account.MIN_BALANCE);

    @Mock
    AccountService accountService;
    @Captor
    ArgumentCaptor<TransferRequest> requestArgumentCaptor;
    TransferCommand transferCommand;
    String command;

    @BeforeEach
    public void initialize() {
        MockitoAnnotations.initMocks(this);
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
        assertThat(transferCommand.execute().lines()).isEmpty();
    }

    @Test
    public void returnsMessageWhenTransferFailsDueToPayeeAccountClosure() {
        TransferFailedException transferFailedException = new TransferFailedException(new AccountClosedException());
        doThrow(transferFailedException).when(accountService).transfer(any(TransferRequest.class));

        transferCommand.execute();

        assertThat(transferCommand.execute().lines()).containsExactly("Payee account is closed. " +
                "Amount will be reversed back to your account.");
    }

    @Test
    public void returnsMessageWhenTransferFailsDueToInsufficientFunds() {
        TransferFailedException transferFailedException = new TransferFailedException(new InsufficientFundsException());
        doThrow(transferFailedException).when(accountService).transfer(any(TransferRequest.class));

        transferCommand.execute();

        assertThat(transferCommand.execute().lines()).containsExactly("Insufficient funds in account");
    }

    @Test
    public void returnsMessageWhenTransferFailsDueToAccountUnderflows() {
        TransferFailedException transferFailedException = new TransferFailedException(new AccountUnderflowException(500));
        doThrow(transferFailedException).when(accountService).transfer(any(TransferRequest.class));

        transferCommand.execute();

        assertThat(transferCommand.execute().lines()).containsExactly("Must maintain minimum balance of 500");
    }
}