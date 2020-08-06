package org.npathai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

class CloseCommandTest {
    static final Account ACCOUNT = new Account("Alice");
    static final String COMMAND = "close " + ACCOUNT.accountNo();

    @Mock
    AccountService accountService;
    @Captor
    ArgumentCaptor<CloseRequest> requestArgumentCaptor;
    CloseCommand closeCommand;

    @BeforeEach
    public void initialize() {
        MockitoAnnotations.initMocks(this);
        closeCommand = new CloseCommand(COMMAND, accountService);
    }

    @Test
    public void closesTheAccount() {
        closeCommand.execute();

        verify(accountService).close(requestArgumentCaptor.capture());
        assertThat(requestArgumentCaptor.getValue().accountNo()).isEqualTo(ACCOUNT.accountNo());
    }

    @Test
    public void acknowledgesWithClosureMessage() {
        List<String> output = closeCommand.execute();

        assertThat(output).containsExactly("Account closed");
    }
}