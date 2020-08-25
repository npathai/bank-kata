package org.npathai.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.npathai.domain.account.Account;
import org.npathai.domain.account.AccountService;
import org.npathai.domain.account.CreateAccountRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OpenZeroBalanceAccountCommandTest {

    private static final Account ACCOUNT = new Account("Alice", 0);
    private static final String OPEN_ZERO_BALANCE_ACCOUNT_COMMAND = "open zero balance account Alice";

    @Mock
    AccountService accountService;

    @Captor
    ArgumentCaptor<CreateAccountRequest> requestArgumentCaptor;

    @BeforeEach
    public void initialize() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void returnsAccountNoOfNewlyOpenedAccount() {
        OpenZeroBalanceAccountCommand openAccountCommand = new OpenZeroBalanceAccountCommand(OPEN_ZERO_BALANCE_ACCOUNT_COMMAND, accountService);
        when(accountService.createAccount(any(CreateAccountRequest.class))).thenReturn(ACCOUNT);
        CommandResponse output = openAccountCommand.execute();

        verify(accountService).createAccount(requestArgumentCaptor.capture());
        assertThat(output.lines()).isEqualTo(List.of(ACCOUNT.accountNo()));
        assertThat(requestArgumentCaptor.getValue().isZeroBalance()).isTrue();
    }
}