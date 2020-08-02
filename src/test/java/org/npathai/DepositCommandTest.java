package org.npathai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

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
    public void withdrawsAmountFromAccount() {
        depositCommand.execute();

        verify(accountService).depositAccount(ACCOUNT.accountNo(), 1000);
    }

    @Test
    public void doesNotReturnAnyValue() {
        assertThat(depositCommand.execute()).isEmpty();
    }

}