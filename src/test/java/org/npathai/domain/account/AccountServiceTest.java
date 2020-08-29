package org.npathai.domain.account;

import intrastructure.MutableClock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.npathai.command.BalanceRequest;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccountServiceTest {
    AccountService accountService;
    @Mock
    InMemoryAccounts accounts;
    MutableClock mutableClock = new MutableClock();
    private ZonedDateTime currentDateTime;

    @BeforeEach
    public void initialize() {
        MockitoAnnotations.initMocks(this);
        accountService = new AccountService(accounts, mutableClock);
        currentDateTime = mutableClock.instant().atZone(ZoneId.systemDefault());
    }

    @Test
    public void returnsNewlyCreatedAccount() {
        CreateAccountRequest createAccountRequest = new CreateAccountRequest("Alice", false);
        Account account = accountService.createAccount(createAccountRequest);
        assertThat(account.accountHolderName()).isEqualTo("Alice");
        assertThat(account.transactions()).isEmpty();
    }

    @Test
    public void storesAccount() {
        CreateAccountRequest createAccountRequest = new CreateAccountRequest("Alice", false);
        Account account = accountService.createAccount(createAccountRequest);
        verify(accounts).save(account);
    }

    @Nested
    public class AccountExists {

        private Account account = new Account("Alice", 1000);

        @BeforeEach
        public void initialize() {
            when(accounts.get(account.accountNo())).thenReturn(account);
        }

        @Test
        public void depositsAmountInAccountWhichIsVisibleAsCreditInAccountStatement() {
            DepositRequest depositRequest = new DepositRequest(account.accountNo(), 1000);
            accountService.depositAccount(depositRequest);
            ShowStatementRequest showStatementRequest = new ShowStatementRequest(account.accountNo());
            assertThat(accountService.getStatement(showStatementRequest))
                    .isEqualTo(List.of(new AccountTransaction(TransactionType.CREDIT, 1000, mutableClock.instant().atZone(ZoneId.systemDefault()))));
        }

        @Test
        public void withdrawsAmountInAccountWhichIsVisibleAsDebitInAccountStatement() {
            DepositRequest depositRequest = new DepositRequest(account.accountNo(), 2000);
            accountService.depositAccount(depositRequest);
            WithdrawRequest withdrawRequest = new WithdrawRequest(account.accountNo(), 1000);
            accountService.withdrawAccount(withdrawRequest);

            assertThat(accountService.getStatement(new ShowStatementRequest(account.accountNo())))
                    .contains(new AccountTransaction(TransactionType.DEBIT, 1000, mutableClock.instant().atZone(ZoneId.systemDefault())));
        }
    }

    @Nested
    class TransferAmount {
        private Account sourceAccount = new Account("Alice", Account.MIN_BALANCE);
        private Account destinationAccount = new Account("Bob", 0);

        @BeforeEach
        public void initialize() {
            when(accounts.get(sourceAccount.accountNo())).thenReturn(sourceAccount);
            when(accounts.get(destinationAccount.accountNo())).thenReturn(destinationAccount);
        }

        @Test
        public void transferringAmountWithdrawsTheAmountFromSourceAccountAndDepositsToDestinationAccount() {
            accountService.depositAccount(new DepositRequest(sourceAccount.accountNo(), 2000));
            TransferRequest transferRequest = new TransferRequest(sourceAccount.accountNo(),
                    destinationAccount.accountNo(), 1000);
            accountService.transfer(transferRequest);

            assertThat(sourceAccount.transactions())
                    .contains(new AccountTransaction(TransactionType.DEBIT, 1000, mutableClock.instant().atZone(ZoneId.systemDefault())));
            assertThat(destinationAccount.transactions())
                    .containsExactly(new AccountTransaction(TransactionType.CREDIT, 1000, mutableClock.instant().atZone(ZoneId.systemDefault())));
        }

        @Test
        public void throwsExceptionWhenAmountCannotBeTransferredToDestinationAccount() {
            accountService.depositAccount(new DepositRequest(sourceAccount.accountNo(), 2000));
            TransferRequest transferRequest = new TransferRequest(sourceAccount.accountNo(),
                    destinationAccount.accountNo(), 1000);
            destinationAccount.close();

            assertThatThrownBy(() -> accountService.transfer(transferRequest))
                    .isInstanceOf(TransferFailedException.class)
                    .hasCauseInstanceOf(AccountClosedException.class);
        }

        @Test
        public void addsReversalTransactionToSourceAccountWhenAmountCannotBeTransferredToDestinationAccount() {
            accountService.depositAccount(new DepositRequest(sourceAccount.accountNo(), 2000));
            destinationAccount.close();
            TransferRequest transferRequest = new TransferRequest(sourceAccount.accountNo(),
                    destinationAccount.accountNo(), 1000);

            assertThatThrownBy(() -> accountService.transfer(transferRequest));

            List<AccountTransaction> transactions = sourceAccount.transactions();
            assertThat(transactions.get(1)).isEqualTo(
                    new AccountTransaction(TransactionType.DEBIT, 1000, mutableClock.instant().atZone(ZoneId.systemDefault())));
            assertThat(transactions.get(2)).isEqualTo(
                    new AccountTransaction(TransactionType.CREDIT, 1000, mutableClock.instant().atZone(ZoneId.systemDefault())));
        }

        @Test
        public void throwsExceptionWhenSourceAccountIsClosed() {
            accountService.depositAccount(new DepositRequest(sourceAccount.accountNo(), 2000));
            TransferRequest transferRequest = new TransferRequest(sourceAccount.accountNo(),
                    destinationAccount.accountNo(), 1000);
            sourceAccount.close();

            assertThatThrownBy(() -> accountService.transfer(transferRequest))
                    .isInstanceOf(TransferFailedException.class)
                    .hasCauseInstanceOf(AccountClosedException.class);
        }

        @Test
        public void throwsExceptionWhenSourceAccountDoesNotHaveSufficientFunds() {
            accountService.depositAccount(new DepositRequest(sourceAccount.accountNo(), 1000));
            accountService.withdrawAccount(new WithdrawRequest(sourceAccount.accountNo(), 100));
            accountService.depositAccount(new DepositRequest(sourceAccount.accountNo(), 500));

            TransferRequest transferRequest = new TransferRequest(sourceAccount.accountNo(),
                    destinationAccount.accountNo(), 1401);

            assertThatThrownBy(() -> accountService.transfer(transferRequest))
                    .isInstanceOf(TransferFailedException.class)
                    .hasCauseInstanceOf(InsufficientFundsException.class);
        }

        @Test
        public void throwsExceptionWhenTriedToWithdrawAmountThatTakesAccountBalanceBelowMinBalance() {
            accountService.depositAccount(new DepositRequest(sourceAccount.accountNo(), 2000));
            TransferRequest transferRequest = new TransferRequest(sourceAccount.accountNo(),
                    destinationAccount.accountNo(), 1501);
            Throwable throwable = catchThrowable(() -> accountService.transfer(transferRequest));
            assertThat(throwable).isInstanceOf(TransferFailedException.class);
            assertThat(((AccountUnderflowException) throwable.getCause()).minBalance()).isEqualTo(500);
        }
    }

    @Nested
    public class CloseAccount {

        private Account account = new Account("Alice", 0);

        @BeforeEach
        public void initialize() {
            when(accounts.get(account.accountNo())).thenReturn(account);
        }

        @Test
        public void cannotWithdrawAmountAfterClosingTheAccount() {
            CloseRequest closeRequest = new CloseRequest(account.accountNo());
            accountService.close(closeRequest);
            assertThatThrownBy(() -> accountService.withdrawAccount(new WithdrawRequest(account.accountNo(), 1)))
                    .isInstanceOf(AccountClosedException.class);
        }

        @Test
        public void cannotDepositAmountAfterClosingTheAccount() {
            accountService.close(new CloseRequest(account.accountNo()));
            assertThatThrownBy(() -> accountService.depositAccount(new DepositRequest(account.accountNo(), 1)))
                    .isInstanceOf(AccountClosedException.class);
        }
    }

    @Nested
    public class ShowStatement {

        private Account account = new Account("Alice", 0);

        @BeforeEach
        public void initialize() {
            when(accounts.get(account.accountNo())).thenReturn(account);
            accountService.depositAccount(new DepositRequest(account.accountNo(), 2000));
            mutableClock.advanceBy(Duration.ofDays(1));
            accountService.withdrawAccount(new WithdrawRequest(account.accountNo(), 1000));
            mutableClock.advanceBy(Duration.ofDays(1));
            accountService.depositAccount(new DepositRequest(account.accountNo(), 3000));
            accountService.withdrawAccount(new WithdrawRequest(account.accountNo(), 2000));
            mutableClock.advanceBy(Duration.ofDays(1));
            accountService.withdrawAccount(new WithdrawRequest(account.accountNo(), 100));
            mutableClock.advanceBy(Duration.ofDays(1));
            accountService.withdrawAccount(new WithdrawRequest(account.accountNo(), 1000));
        }

        @Test
        public void returnsOnlyCreditedTransactionsInOrderFromNewestToOldestWhenTypeFilterIsCredit() {
            ShowStatementRequest showStatementRequest = new ShowStatementRequest(account.accountNo());
            showStatementRequest.typeFilter("C");
            List<AccountTransaction> statement = accountService.getStatement(showStatementRequest);
            assertThat(statement).isEqualTo(List.of(
                    new AccountTransaction(TransactionType.CREDIT, 3000,
                            currentDate().minusDays(2)),
                    new AccountTransaction(TransactionType.CREDIT, 2000,
                            currentDate().minusDays(4))));
        }

        @Test
        public void returnsOnlyWithdrawnTransactionsInOrderFromNewestToOldestWhenTypeFilterIsDebit() {
            ShowStatementRequest showStatementRequest = new ShowStatementRequest(account.accountNo());
            showStatementRequest.typeFilter("D");
            List<AccountTransaction> statement = accountService.getStatement(showStatementRequest);
            assertThat(statement).isEqualTo(List.of(
                    new AccountTransaction(TransactionType.DEBIT, 1000, currentDate()),
                    new AccountTransaction(TransactionType.DEBIT, 100, currentDate().minusDays(1)),
                    new AccountTransaction(TransactionType.DEBIT, 2000, currentDate().minusDays(2)),
                    new AccountTransaction(TransactionType.DEBIT, 1000, currentDate().minusDays(3))
            ));
        }

        private ZonedDateTime currentDate() {
            return mutableClock.instant().atZone(ZoneId.systemDefault());
        }

        @Test
        public void returnsOnlyTransactionsOnOrWithinTheDateFilterRange() {
            AccountTransaction depositTransaction1 = new AccountTransaction(TransactionType.CREDIT, 1000, currentDateTime);
            AccountTransaction withdrawalTransaction1 = new AccountTransaction(TransactionType.DEBIT, 1000, currentDateTime);
            AccountTransaction depositTransaction2 = new AccountTransaction(TransactionType.CREDIT, 2000, currentDateTime.minusDays(1));
            AccountTransaction withdrawalTransaction2 = new AccountTransaction(TransactionType.DEBIT, 2000, currentDateTime.minusDays(1));
            AccountTransaction depositTransaction3 = new AccountTransaction(TransactionType.CREDIT, 3000, currentDateTime.minusDays(2));
            AccountTransaction depositTransaction4 = new AccountTransaction(TransactionType.DEBIT, 1000, currentDateTime.minusDays(3));


        }
    }

    @Nested
    public class WithdrawAmount {

        private Account account = new Account("Alice", Account.MIN_BALANCE);

        @BeforeEach
        public void initialize() {
            when(accounts.get(account.accountNo())).thenReturn(account);
            accountService.depositAccount(new DepositRequest(account.accountNo(), 1000));
            accountService.depositAccount(new DepositRequest(account.accountNo(), 500));
        }

        @Test
        public void throwsExceptionWhenTriedToWithdrawAmountThatTakesAccountBalanceBelowMinBalance() {
            WithdrawRequest withdrawRequest = new WithdrawRequest(account.accountNo(), 1001);
            Throwable throwable = catchThrowable(() -> accountService.withdrawAccount(withdrawRequest));
            assertThat(throwable).isInstanceOf(AccountUnderflowException.class);
            assertThat(((AccountUnderflowException) throwable).minBalance()).isEqualTo(500);
        }
    }

    @Nested
    public class ZeroBalanceAccount {

        private Account account = new Account("Alice", 0);

        @BeforeEach
        public void initialize() {
            when(accounts.get(account.accountNo())).thenReturn(account);
            accountService.depositAccount(new DepositRequest(account.accountNo(), 1000));
            accountService.depositAccount(new DepositRequest(account.accountNo(), 500));
        }

        @Test
        public void canWithdrawWholeAmount() {
            accountService.withdrawAccount(new WithdrawRequest(account.accountNo(), 1500));
            List<AccountTransaction> statement = accountService.getStatement(new ShowStatementRequest(account.accountNo()));
            assertThat(statement).contains(
                    new AccountTransaction(TransactionType.DEBIT, 1500, mutableClock.instant().atZone(ZoneId.systemDefault())));
        }
    }

    @Nested
    public class AccountBalance {

        private Account account = new Account("Alice", 0);

        @BeforeEach
        public void initialize() {
            when(accounts.get(account.accountNo())).thenReturn(account);
            accountService.depositAccount(new DepositRequest(account.accountNo(), 1000));
            accountService.withdrawAccount(new WithdrawRequest(account.accountNo(), 100));
            accountService.depositAccount(new DepositRequest(account.accountNo(), 500));
        }

        @Test
        public void returnsCurrentAccountBalance() {
            assertThat(accountService.getBalance(new BalanceRequest(account.accountNo()))).isEqualTo(1400L);
        }
    }

}