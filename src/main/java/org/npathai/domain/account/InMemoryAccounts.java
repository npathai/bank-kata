package org.npathai.domain.account;

import java.util.HashMap;
import java.util.Map;

public class InMemoryAccounts {
    private Map<String, Account> accountByAccountNo = new HashMap<>();

    public void save(Account account) {
        accountByAccountNo.put(account.accountNo(), account);
    }

    public Account get(String accountNo) {
        return accountByAccountNo.get(accountNo);
    }
}
