package org.npathai.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.npathai.domain.account.Account;
import org.npathai.domain.account.AccountClosedException;
import org.npathai.domain.account.AccountService;
import org.npathai.domain.account.DepositRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DepositCommandTest {

    static final Account ACCOUNT = new Account("Alice", Account.MIN_BALANCE);
    static final String DEPOSIT_AMOUNT_COMMAND = ACCOUNT.accountNo() + " deposit " + 1000;

    @Mock
    AccountService accountService;
    @Captor
    ArgumentCaptor<DepositRequest> requestArgumentCaptor;
    private DepositCommand depositCommand;

    @BeforeEach
    public void initialize() {
        MockitoAnnotations.initMocks(this);
        depositCommand = new DepositCommand(DEPOSIT_AMOUNT_COMMAND, accountService);
    }

    @Test
    public void depositsAmountToAccount() {
        depositCommand.executeNew();

        verify(accountService).depositAccount(requestArgumentCaptor.capture());
        assertThat(requestArgumentCaptor.getValue().accountNo()).isEqualTo(ACCOUNT.accountNo());
        assertThat(requestArgumentCaptor.getValue().amount()).isEqualTo(1000);
    }

    @Test
    public void returnsMessageWhenDepositIsSuccessful() {
        assertThat(depositCommand.executeNew().lines()).containsExactly("Successfully deposited Rs 1000");
    }

    @Test
    public void returnsClosureMessageWhenTriedToDepositAccountAfterClosingAccount() {
        doThrow(AccountClosedException.class).when(accountService).depositAccount(any(DepositRequest.class));

        assertThat(depositCommand.executeNew().lines()).contains("Account is closed, cannot make any transaction");
    }
}