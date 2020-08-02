package org.npathai;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShowStatementCommandTest {

    static final Account ACCOUNT = new Account("Alice");
    static final String SHOW_STATEMENT_COMMAND = ACCOUNT.accountNo() + " statement";

    @Mock
    AccountService accountService;
    private ShowStatementCommand showStatementCommand;

    @BeforeEach
    public void initialize() {
        MockitoAnnotations.initMocks(this);
        showStatementCommand = new ShowStatementCommand(SHOW_STATEMENT_COMMAND, accountService);
    }

    @Test
    public void showsAllTransactionsInChronologicalOrderOfTheirOccurrence() {
        AccountTransaction depositTransaction = new AccountTransaction("C", 1000);
        AccountTransaction withdrawTransaction = new AccountTransaction("D", 500);
        AccountTransaction withdrawTransaction2 = new AccountTransaction("D", 100);

        when(accountService.getStatement(ACCOUNT.accountNo())).thenReturn(List.of(depositTransaction,
                withdrawTransaction, withdrawTransaction2));

        assertThat(showStatementCommand.execute()).isEqualTo(List.of("type||amount", "C||1000", "D||500", "D||100"));
    }

    @Test
    public void returnsEmptyStatementWhenNoTransactionsOnAnAccount() {
        when(accountService.getStatement(ACCOUNT.accountNo())).thenReturn(Collections.emptyList());

        assertThat(showStatementCommand.execute()).isEqualTo(List.of("type||amount"));
    }
}