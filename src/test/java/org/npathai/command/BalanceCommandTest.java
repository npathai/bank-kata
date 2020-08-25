package org.npathai.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.npathai.domain.account.Account;
import org.npathai.domain.account.AccountService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BalanceCommandTest {

    private static final Account ACCOUNT = new Account("Alice", Account.MIN_BALANCE);
    private static final String BALANCE_COMMAND = ACCOUNT.accountNo() + " balance";

    @Mock
    AccountService accountService;
    @Captor
    ArgumentCaptor<BalanceRequest> requestArgumentCaptor;

    @BeforeEach
    public void initialize() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void returnsBalanceOfAccount() {
        BalanceCommand command = new BalanceCommand(BALANCE_COMMAND, accountService);
        when(accountService.getBalance(Mockito.any(BalanceRequest.class))).thenReturn(10000L);
        CommandResponse output = command.execute();

        verify(accountService).getBalance(requestArgumentCaptor.capture());
        assertThat(output.lines()).containsExactly("Balance Rs " + 10000);
        assertThat(requestArgumentCaptor.getValue().accountNo()).isEqualTo(ACCOUNT.accountNo());
    }
}