package org.npathai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OpenAccountCommandTest {

    private static final Account ACCOUNT = new Account("Alice");
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
        when(accountService.createAccount("Alice")).thenReturn(ACCOUNT);
        List<String> output = openAccountCommand.execute();

        verify(accountService).createAccount("Alice");
        assertThat(output).isEqualTo(List.of(ACCOUNT.accountNo()));
    }
}