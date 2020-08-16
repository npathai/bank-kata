package org.npathai.domain.account;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryAccountsTest {

    static final Account ACCOUNT = new Account("Alice", 0);
    InMemoryAccounts inMemoryAccounts = new InMemoryAccounts();

    @Test
    public void returnsAccountByAccountNo() {
        inMemoryAccounts.save(ACCOUNT);
        assertThat(inMemoryAccounts.get(ACCOUNT.accountNo())).isEqualTo(ACCOUNT);
    }

    @Test
    public void returnsNullWhenAccountWithGivenAccountNoDoesNotExist() {
        assertThat(inMemoryAccounts.get("234234")).isNull();
    }
}