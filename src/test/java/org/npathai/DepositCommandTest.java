package org.npathai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DepositCommandTest {

    static final Account ACCOUNT = new Account("Alice");
    static final String DEPOSIT_AMOUNT_COMMAND = ACCOUNT.accountNo() + " deposit " + 1000;

    @Mock
    AccountService accountService;
    private DepositCommand depositCommand;

    @BeforeEach
    public void initialize() {
        MockitoAnnotations.initMocks(this);
        depositCommand = new DepositCommand(DEPOSIT_AMOUNT_COMMAND, accountService);
    }

    @Test
    public void depositsAmountToAccount() {
        depositCommand.execute();

        verify(accountService).depositAccount(ACCOUNT.accountNo(), 1000);
    }

    @Test
    public void doesNotReturnAnyValueOnSuccessfulDeposit() {
        assertThat(depositCommand.execute()).isEmpty();
    }

    @Test
    public void returnsClosureMessageWhenTriedToDepositAccountAfterClosingAccount() {
        doThrow(AccountClosedException.class).when(accountService).depositAccount(ACCOUNT.accountNo(), 1000);

        assertThat(depositCommand.execute()).contains("Account is closed, cannot make any transaction");
    }
}