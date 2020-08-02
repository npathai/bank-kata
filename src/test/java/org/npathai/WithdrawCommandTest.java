package org.npathai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

class WithdrawCommandTest {
    static final Account ACCOUNT = new Account("Alice");
    static final String WITHDRAW_AMOUNT_COMMAND = ACCOUNT.accountNo() + " withdraw " + 1000;

    @Mock
    AccountService accountService;
    private WithdrawCommand withdrawCommand;

    @BeforeEach
    public void initialize() {
        MockitoAnnotations.initMocks(this);
        withdrawCommand = new WithdrawCommand(WITHDRAW_AMOUNT_COMMAND, accountService);
    }

    @Test
    public void withdrawsAmountFromAccount() {
        withdrawCommand.execute();

        verify(accountService).withdrawAccount(ACCOUNT.accountNo(), 1000);
    }

    @Test
    public void doesNotReturnAnyValue() {
        assertThat(withdrawCommand.execute()).isEmpty();
    }
}