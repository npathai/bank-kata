package org.npathai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AccountServiceTest {
    AccountService accountService;

    @BeforeEach
    public void initialize() {
        accountService = new AccountService();
    }

    @Test
    public void returnsNewlyCreatedAccount() {
        Account account = accountService.createAccount("Alice");
        assertThat(account.accountHolderName()).isEqualTo("Alice");
        assertThat(account.transactions()).isEmpty();
    }

    @Nested
    public class AccountExists {

        private Account account;

        @BeforeEach
        public void initialize() {
            account = accountService.createAccount("Alice");
        }

        @Test
        public void depositsAmountInAccountWhichIsVisibleAsCreditInAccountStatement() {
            accountService.depositAccount(account.accountNo(), 1000);

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
            sourceAccount = accountService.createAccount("Alice");
            destinationAccount = accountService.createAccount("Bob");
        }

        @Test
        public void transferringAmountWithdrawsTheAmountFromSourceAccountAndDepositsToDestinationAccount() {
            accountService.depositAccount(sourceAccount.accountNo(), 1000);
            accountService.transfer(sourceAccount.accountNo(), destinationAccount.accountNo(), 1000);

            assertThat(sourceAccount.transactions()).contains(new AccountTransaction("D", 1000));
            assertThat(destinationAccount.transactions()).containsExactly(new AccountTransaction("C", 1000));
        }
    }
}