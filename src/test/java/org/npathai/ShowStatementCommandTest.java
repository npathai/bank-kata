package org.npathai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

class ShowStatementCommandTest {

    static final Account ACCOUNT = new Account("Alice");
    static final String SHOW_STATEMENT_COMMAND = ACCOUNT.accountNo() + " statement";

    @Mock
    AccountService accountService;
    ShowStatementCommand showStatementCommand;

    @BeforeEach
    public void initialize() {
        MockitoAnnotations.initMocks(this);
        showStatementCommand = new ShowStatementCommand(SHOW_STATEMENT_COMMAND, accountService);
    }

    @Test
    public void showsAllTransactionsInChronologicalOrderOfTheirOccurrence() {
        AccountTransaction depositTransaction = new AccountTransaction(TransactionType.typeFrom("C"), 1000);
        AccountTransaction withdrawTransaction = new AccountTransaction(TransactionType.typeFrom("D"), 500);
        AccountTransaction withdrawTransaction2 = new AccountTransaction(TransactionType.typeFrom("D"), 100);

        when(accountService.getStatement(any(ShowStatementRequest.class))).thenReturn(List.of(depositTransaction,
                withdrawTransaction, withdrawTransaction2));

        assertThat(showStatementCommand.execute()).isEqualTo(List.of("type||amount", "C||1000", "D||500", "D||100"));
    }

    @Test
    public void returnsEmptyStatementWhenNoTransactionsOnAnAccount() {
        when(accountService.getStatement(new ShowStatementRequest(ACCOUNT.accountNo()))).thenReturn(Collections.emptyList());

        assertThat(showStatementCommand.execute()).isEqualTo(List.of("type||amount"));
    }
}