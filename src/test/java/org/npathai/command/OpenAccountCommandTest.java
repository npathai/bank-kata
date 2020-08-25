package org.npathai.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

class OpenAccountCommandTest {

    private static final Account ACCOUNT = new Account("Alice", Account.MIN_BALANCE);
    private static final String OPEN_ACCOUNT_COMMAND = "open account Alice";

    @Mock
    AccountService accountService;

    @BeforeEach
    public void initialize() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void returnsAccountNoOfNewlyOpenedAccount() {
        OpenAccountCommand openAccountCommand = new OpenAccountCommand(OPEN_ACCOUNT_COMMAND, accountService);
        when(accountService.createAccount(any(CreateAccountRequest.class))).thenReturn(ACCOUNT);
        CommandResponse output = openAccountCommand.execute();

        verify(accountService).createAccount(any(CreateAccountRequest.class));
        assertThat(output.lines()).isEqualTo(List.of(ACCOUNT.accountNo()));
    }
}