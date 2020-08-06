package org.npathai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountServiceTest {
    AccountService accountService;

    @BeforeEach
    public void initialize() {
        accountService = new AccountService();
    }

    @Test
    public void returnsNewlyCreatedAccount() {
        CreateAccountRequest createAccountRequest = new CreateAccountRequest("Alice");
        Account account = accountService.createAccount(createAccountRequest);
        assertThat(account.accountHolderName()).isEqualTo("Alice");
        assertThat(account.transactions()).isEmpty();
    }

    @Nested
    public class AccountExists {

        private Account account;

        @BeforeEach
        public void initialize() {
            account = accountService.createAccount(new CreateAccountRequest("Alice"));
        }

        @Test
        public void depositsAmountInAccountWhichIsVisibleAsCreditInAccountStatement() {
            DepositRequest depositRequest = new DepositRequest(account.accountNo(), 1000);
            accountService.depositAccount(depositRequest);

            assertThat(accountService.getStatement(account.accountNo()))
                    .isEqualTo(List.of(new AccountTransaction("C", 1000)));
        }

        @Test
        public void withdrawsAmountInAccountWhichIsVisibleAsCreditInAccountStatement() {
            accountService.withdrawAccount(account.accountNo(), 1000);

            assertThat(accountService.getStatement(account.accountNo()))
                    .isEqualTo(List.of(new AccountTransaction("D", 1000)));
        }
    }

    @Nested
    class TransferAmount {
        private Account sourceAccount;
        private Account destinationAccount;

        @BeforeEach
        public void initialize() {
            sourceAccount = accountService.createAccount(new CreateAccountRequest("Alice"));
            destinationAccount = accountService.createAccount(new CreateAccountRequest("Bob"));
        }

        @Test
        public void transferringAmountWithdrawsTheAmountFromSourceAccountAndDepositsToDestinationAccount() {
            accountService.depositAccount(new DepositRequest(sourceAccount.accountNo(), 1000));
            accountService.transfer(sourceAccount.accountNo(), destinationAccount.accountNo(), 1000);

            assertThat(sourceAccount.transactions()).contains(new AccountTransaction("D", 1000));
            assertThat(destinationAccount.transactions()).containsExactly(new AccountTransaction("C", 1000));
        }
    }

    @Nested
    public class CloseAccount {

        private Account sourceAccount;

        @BeforeEach
        public void initialize() {
            sourceAccount = accountService.createAccount(new CreateAccountRequest("Alice"));
        }

        @Test
        public void cannotWithdrawAmountAfterClosingTheAccount() {
            accountService.close(sourceAccount.accountNo());
            assertThatThrownBy(() -> accountService.withdrawAccount(sourceAccount.accountNo(), 1))
                    .isInstanceOf(AccountClosedException.class);
        }

        @Test
        public void cannotDepositAmountAfterClosingTheAccount() {
            accountService.close(sourceAccount.accountNo());
            assertThatThrownBy(() -> accountService.depositAccount(new DepositRequest(sourceAccount.accountNo(), 1)))
                    .isInstanceOf(AccountClosedException.class);
        }
    }
}