package org.npathai.command;

import intrastructure.MutableClock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.npathai.domain.account.*;

import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShowStatementCommandTest {
    MutableClock mutableClock = new MutableClock();
    static final Account ACCOUNT = new Account("Alice", Account.MIN_BALANCE);
    static final String SHOW_STATEMENT_COMMAND = ACCOUNT.accountNo() + " statement";
    static final String SHOW_STATEMENT_WITH_FILTER_COMMAND = ACCOUNT.accountNo() + " statement --type C";

    @Mock
    AccountService accountService;
    @Captor
    ArgumentCaptor<ShowStatementRequest> requestArgumentCaptor;
    ShowStatementCommand showStatementCommand;

    @BeforeEach
    public void initialize() {
        MockitoAnnotations.initMocks(this);
        showStatementCommand = new ShowStatementCommand(SHOW_STATEMENT_COMMAND, accountService);
    }

    @Test
    public void showsAllTransactions() {
        AccountTransaction depositTransaction = new AccountTransaction(TransactionType.CREDIT, 1000, mutableClock.instant().atZone(ZoneId.systemDefault()));
        AccountTransaction withdrawTransaction = new AccountTransaction(TransactionType.DEBIT, 500, mutableClock.instant().atZone(ZoneId.systemDefault()));
        AccountTransaction withdrawTransaction2 = new AccountTransaction(TransactionType.DEBIT, 100, mutableClock.instant().atZone(ZoneId.systemDefault()));

        when(accountService.getStatement(any(ShowStatementRequest.class))).thenReturn(List.of(depositTransaction,
                withdrawTransaction, withdrawTransaction2));

        List<String> statement = showStatementCommand.execute();

        verify(accountService).getStatement(requestArgumentCaptor.capture());
        ShowStatementRequest request = requestArgumentCaptor.getValue();
        assertThat(request.typeFilter()).isNull();
        assertThat(statement).isEqualTo(List.of("type||amount", "C||1000", "D||500", "D||100"));
    }

    @Test
    public void returnsEmptyStatementWhenNoTransactionsOnAnAccount() {
        when(accountService.getStatement(new ShowStatementRequest(ACCOUNT.accountNo()))).thenReturn(Collections.emptyList());

        assertThat(showStatementCommand.execute()).isEqualTo(List.of("type||amount"));
    }

    @Test
    public void showsFilteredTransactionsOnTransactionType() {
        showStatementCommand = new ShowStatementCommand(SHOW_STATEMENT_WITH_FILTER_COMMAND, accountService);
        AccountTransaction depositTransaction = new AccountTransaction(TransactionType.CREDIT, 1000, mutableClock.instant().atZone(ZoneId.systemDefault()));
        AccountTransaction depositTransaction2 = new AccountTransaction(TransactionType.CREDIT, 2000, mutableClock.instant().atZone(ZoneId.systemDefault()));

        when(accountService.getStatement(any(ShowStatementRequest.class))).thenReturn(List.of(depositTransaction,
                depositTransaction2));

        List<String> statement = showStatementCommand.execute();

        verify(accountService).getStatement(requestArgumentCaptor.capture());
        ShowStatementRequest request = requestArgumentCaptor.getValue();
        assertThat(request.typeFilter()).isEqualTo("C");
        assertThat(statement).isEqualTo(List.of("type||amount", "C||1000", "C||2000"));
    }
}