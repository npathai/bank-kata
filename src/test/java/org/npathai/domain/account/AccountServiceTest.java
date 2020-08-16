package org.npathai.domain.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.npathai.domain.account.*;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class AccountServiceTest {
    AccountService accountService;

    @BeforeEach
    public void initialize() {
        accountService = new AccountService();
    }

    @Test
    public void returnsNewlyCreatedAccount() {
        CreateAccountRequest createAccountRequest = new CreateAccountRequest("Alice", false);
        Account account = accountService.createAccount(createAccountRequest);
        assertThat(account.accountHolderName()).isEqualTo("Alice");
        assertThat(account.transactions()).isEmpty();
    }

    @Nested
    public class AccountExists {

        private Account account;

        @BeforeEach
        public void initialize() {
            account = accountService.createAccount(new CreateAccountRequest("Alice", false));
        }

        @Test
        public void depositsAmountInAccountWhichIsVisibleAsCreditInAccountStatement() {
            DepositRequest depositRequest = new DepositRequest(account.accountNo(), 1000);
            accountService.depositAccount(depositRequest);
            ShowStatementRequest showStatementRequest = new ShowStatementRequest(account.accountNo());
            assertThat(accountService.getStatement(showStatementRequest))
                    .isEqualTo(List.of(new AccountTransaction(TransactionType.CREDIT, 1000)));
        }

        @Test
        public void withdrawsAmountInAccountWhichIsVisibleAsDebitInAccountStatement() {
            DepositRequest depositRequest = new DepositRequest(account.accountNo(), 2000);
            accountService.depositAccount(depositRequest);
            WithdrawRequest withdrawRequest = new WithdrawRequest(account.accountNo(), 1000);
            accountService.withdrawAccount(withdrawRequest);

            assertThat(accountService.getStatement(new ShowStatementRequest(account.accountNo())))
                    .contains(new AccountTransaction(TransactionType.DEBIT, 1000));
        }
    }

    @Nested
    class TransferAmount {
        private Account sourceAccount;
        private Account destinationAccount;

        @BeforeEach
        public void initialize() {
            sourceAccount = accountService.createAccount(new CreateAccountRequest("Alice", false));
            destinationAccount = accountService.createAccount(new CreateAccountRequest("Bob", false));
        }

        @Test
        public void transferringAmountWithdrawsTheAmountFromSourceAccountAndDepositsToDestinationAccount() {
            accountService.depositAccount(new DepositRequest(sourceAccount.accountNo(), 2000));
            TransferRequest transferRequest = new TransferRequest(sourceAccount.accountNo(),
                    destinationAccount.accountNo(), 1000);
            accountService.transfer(transferRequest);

            assertThat(sourceAccount.transactions())
                    .contains(new AccountTransaction(TransactionType.DEBIT, 1000));
            assertThat(destinationAccount.transactions())
                    .containsExactly(new AccountTransaction(TransactionType.CREDIT, 1000));
        }

        @Test
        public void throwsExceptionAndAddsReversalTransactionToSourceAccountWhenAmountCannotBeTransferredToDestinationAccount() {
            accountService.depositAccount(new DepositRequest(sourceAccount.accountNo(), 2000));
            destinationAccount.close();
            TransferRequest transferRequest = new TransferRequest(sourceAccount.accountNo(),
                    destinationAccount.accountNo(), 1000);

            assertThatThrownBy(() -> accountService.transfer(transferRequest))
                    .isInstanceOf(TransferFailedException.class);
            List<AccountTransaction> transactions = sourceAccount.transactions();
            assertThat(transactions.get(1)).isEqualTo(new AccountTransaction(TransactionType.DEBIT, 1000));
            assertThat(transactions.get(2)).isEqualTo(new AccountTransaction(TransactionType.CREDIT, 1000));
        }

        @Test
        public void throwsInsufficientFundsExceptionWhenSourceAccountDoesNotHaveSufficientFunds() {
            accountService.depositAccount(new DepositRequest(sourceAccount.accountNo(), 1000));
            accountService.withdrawAccount(new WithdrawRequest(sourceAccount.accountNo(), 100));
            accountService.depositAccount(new DepositRequest(sourceAccount.accountNo(), 500));

            TransferRequest transferRequest = new TransferRequest(sourceAccount.accountNo(),
                    destinationAccount.accountNo(), 1401);

            assertThatThrownBy(() -> accountService.transfer(transferRequest))
                    .isInstanceOf(InsufficientFundsException.class);
        }

        @Test
        public void throwsExceptionWhenTriedToWithdrawAmountThatTakesAccountBalanceBelowMinBalance() {
            accountService.depositAccount(new DepositRequest(sourceAccount.accountNo(), 2000));
            TransferRequest transferRequest = new TransferRequest(sourceAccount.accountNo(),
                    destinationAccount.accountNo(), 1501);
            Throwable throwable = catchThrowable(() -> accountService.transfer(transferRequest));
            assertThat(throwable).isInstanceOf(AccountUnderflowException.class);
            assertThat(((AccountUnderflowException) throwable).minBalance()).isEqualTo(500);
        }
    }

    @Nested
    public class CloseAccount {

        private Account sourceAccount;

        @BeforeEach
        public void initialize() {
            sourceAccount = accountService.createAccount(new CreateAccountRequest("Alice", false));
        }

        @Test
        public void cannotWithdrawAmountAfterClosingTheAccount() {
            CloseRequest closeRequest = new CloseRequest(sourceAccount.accountNo());
            accountService.close(closeRequest);
            assertThatThrownBy(() -> accountService.withdrawAccount(new WithdrawRequest(sourceAccount.accountNo(), 1)))
                    .isInstanceOf(AccountClosedException.class);
        }

        @Test
        public void cannotDepositAmountAfterClosingTheAccount() {
            accountService.close(new CloseRequest(sourceAccount.accountNo()));
            assertThatThrownBy(() -> accountService.depositAccount(new DepositRequest(sourceAccount.accountNo(), 1)))
                    .isInstanceOf(AccountClosedException.class);
        }
    }

    @Nested
    public class ShowStatement {

        private Account account;

        @BeforeEach
        public void initialize() {
            account = accountService.createAccount(new CreateAccountRequest("Alice", false));
            accountService.depositAccount(new DepositRequest(account.accountNo(), 2000));
            accountService.withdrawAccount(new WithdrawRequest(account.accountNo(), 1000));
            accountService.depositAccount(new DepositRequest(account.accountNo(), 3000));
            accountService.withdrawAccount(new WithdrawRequest(account.accountNo(), 100));
        }

        @Test
        public void returnsOnlyCreditedTransactionsInChronologicalOrderWhenTypeFilterIsCredit() {
            ShowStatementRequest showStatementRequest = new ShowStatementRequest(account.accountNo());
            showStatementRequest.typeFilter("C");
            List<AccountTransaction> statement = accountService.getStatement(showStatementRequest);
            assertThat(statement).isEqualTo(List.of(new AccountTransaction(TransactionType.CREDIT, 2000),
                    new AccountTransaction(TransactionType.CREDIT, 3000)));
        }

        @Test
        public void returnsOnlyWithdrawnTransactionsInChronologicalOrderWhenTypeFilterIsDebit() {
            ShowStatementRequest showStatementRequest = new ShowStatementRequest(account.accountNo());
            showStatementRequest.typeFilter("D");
            List<AccountTransaction> statement = accountService.getStatement(showStatementRequest);
            assertThat(statement).isEqualTo(List.of(new AccountTransaction(TransactionType.DEBIT, 1000),
                    new AccountTransaction(TransactionType.DEBIT, 100)));
        }
    }

    @Nested
    public class WithdrawAmount {

        private Account account;

        @BeforeEach
        public void initialize() {
            account = accountService.createAccount(new CreateAccountRequest("Alice", false));
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

        private Account account;

        @BeforeEach
        public void initialize() {
            account = accountService.createAccount(new CreateAccountRequest("Alice", true));
            accountService.depositAccount(new DepositRequest(account.accountNo(), 1000));
            accountService.depositAccount(new DepositRequest(account.accountNo(), 500));
        }

        @Test
        public void canWithdrawWholeAmount() {
            account.withdraw(1500);
            List<AccountTransaction> statement = accountService.getStatement(new ShowStatementRequest(account.accountNo()));
            assertThat(statement).contains(new AccountTransaction(TransactionType.DEBIT, 1500));
        }
    }
}