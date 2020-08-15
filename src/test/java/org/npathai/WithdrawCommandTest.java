package org.npathai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

class WithdrawCommandTest {
    static final Account ACCOUNT = new Account("Alice", Account.MIN_BALANCE);
    static final String WITHDRAW_AMOUNT_COMMAND = ACCOUNT.accountNo() + " withdraw " + 1000;

    @Mock
    AccountService accountService;
    @Captor
    ArgumentCaptor<WithdrawRequest> requestArgumentCaptor;
    WithdrawCommand withdrawCommand;

    @BeforeEach
    public void initialize() {
        MockitoAnnotations.initMocks(this);
        withdrawCommand = new WithdrawCommand(WITHDRAW_AMOUNT_COMMAND, accountService);
    }

    @Test
    public void withdrawsAmountFromAccount() {
        withdrawCommand.execute();

        verify(accountService).withdrawAccount(requestArgumentCaptor.capture());
        assertThat(requestArgumentCaptor.getValue().accountNo()).isEqualTo(ACCOUNT.accountNo());
        assertThat(requestArgumentCaptor.getValue().amount()).isEqualTo(1000);
    }

    @Test
    public void returnsMessageWhenWithdrawalIsSuccessful() {
        assertThat(withdrawCommand.execute()).containsExactly("Successfully withdrawn Rs 1000");
    }

    @Test
    public void returnsClosureMessageWhenTriedToDepositAccountAfterClosingAccount() {
        doThrow(AccountClosedException.class).when(accountService).withdrawAccount(any(WithdrawRequest.class));

        assertThat(withdrawCommand.execute()).contains("Account is closed, cannot make any transaction");
    }
    
    @Test
    public void returnsMinimumBalanceNecessaryMessageWhenWithdrawingAmountThatCausesBalanceToGoBelowMinRequirement() {
        AccountUnderflowException accountUnderflowException = new AccountUnderflowException(500);
        doThrow(accountUnderflowException).when(accountService).withdrawAccount(any(WithdrawRequest.class));

        assertThat(withdrawCommand.execute()).contains("Must maintain minimum balance of 500");
    }
}