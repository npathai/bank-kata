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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
    private String currentDate;
    private ZonedDateTime currentDateTime;

    @BeforeEach
    public void initialize() {
        MockitoAnnotations.initMocks(this);
        showStatementCommand = new ShowStatementCommand(SHOW_STATEMENT_COMMAND, accountService);
        currentDateTime = mutableClock.instant().atZone(ZoneId.systemDefault());
        currentDate = formatted(currentDateTime);
    }

    private String formatted(ZonedDateTime currentDateTime) {
        return currentDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    @Test
    public void showsAllTransactions() {
        AccountTransaction depositTransaction = new AccountTransaction(TransactionType.CREDIT, 1000, currentDateTime);
        AccountTransaction withdrawTransaction = new AccountTransaction(TransactionType.DEBIT, 500, currentDateTime);
        AccountTransaction withdrawTransaction2 = new AccountTransaction(TransactionType.DEBIT, 100, currentDateTime);

        when(accountService.getStatement(any(ShowStatementRequest.class))).thenReturn(List.of(depositTransaction,
                withdrawTransaction, withdrawTransaction2));

        CommandResponse statement = showStatementCommand.execute();

        verify(accountService).getStatement(requestArgumentCaptor.capture());
        ShowStatementRequest request = requestArgumentCaptor.getValue();
        assertThat(request.typeFilter()).isNull();

        assertThat(statement.lines()).isEqualTo(List.of(
                "type||amount||date",
                "C||1000||" + currentDate,
                "D||500||" + currentDate,
                "D||100||" + currentDate));
    }

    @Test
    public void returnsEmptyStatementWhenNoTransactionsOnAnAccount() {
        when(accountService.getStatement(new ShowStatementRequest(ACCOUNT.accountNo()))).thenReturn(Collections.emptyList());

        assertThat(showStatementCommand.execute().lines()).isEqualTo(List.of("type||amount||date"));
    }

    @Test
    public void showsFilteredTransactionsOnTransactionType() {
        showStatementCommand = new ShowStatementCommand(SHOW_STATEMENT_WITH_FILTER_COMMAND, accountService);
        ZonedDateTime currentDateTime = mutableClock.instant().atZone(ZoneId.systemDefault());
        AccountTransaction depositTransaction = new AccountTransaction(TransactionType.CREDIT, 1000, currentDateTime);
        AccountTransaction depositTransaction2 = new AccountTransaction(TransactionType.CREDIT, 2000, currentDateTime);

        when(accountService.getStatement(any(ShowStatementRequest.class))).thenReturn(List.of(depositTransaction,
                depositTransaction2));

        CommandResponse statement = showStatementCommand.execute();

        verify(accountService).getStatement(requestArgumentCaptor.capture());
        ShowStatementRequest request = requestArgumentCaptor.getValue();
        assertThat(request.typeFilter()).isEqualTo("C");
        assertThat(statement.lines()).isEqualTo(List.of(
                "type||amount||date",
                "C||1000||" + currentDate,
                "C||2000||" + currentDate));
    }

    @Test
    public void showsFilteredStatementsWithinDateRange() {
        String currentDateMinusThreeDays = formatted(currentDateTime.minusDays(3));
        String currentDateMinusOneDay = formatted(currentDateTime.minusDays(1));
        String commandWithDateFilter = ACCOUNT.accountNo() + " statement --date "+ currentDateMinusThreeDays + " " + currentDateMinusOneDay;
        showStatementCommand = new ShowStatementCommand(commandWithDateFilter, accountService);
        AccountTransaction depositTransaction2 = new AccountTransaction(TransactionType.CREDIT, 2000, currentDateTime.minusDays(1));
        AccountTransaction depositTransaction3 = new AccountTransaction(TransactionType.CREDIT, 3000, currentDateTime.minusDays(2));

        when(accountService.getStatement(any(ShowStatementRequest.class))).thenReturn(List.of(depositTransaction2,
                depositTransaction3));

        showStatementCommand.execute();
        verify(accountService).getStatement(requestArgumentCaptor.capture());
        ShowStatementRequest request = requestArgumentCaptor.getValue();
        assertThat(request.fromDate()).isEqualTo(currentDateMinusThreeDays);
        assertThat(request.toDate()).isEqualTo(currentDateMinusOneDay);
    }
}