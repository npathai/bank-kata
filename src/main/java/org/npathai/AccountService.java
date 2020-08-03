package org.npathai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountService {
    private Map<String, Account> accountByAccountNo = new HashMap<>();

    public Account createAccount(String accountHolderName) {
        Account account = new Account(accountHolderName);
        accountByAccountNo.put(account.accountNo(), account);
        return account;
    }

    public void withdrawAccount(String accountNo, int amount) {
        accountByAccountNo.get(accountNo).withdraw(amount);
    }

    public void depositAccount(String accountNo, int amount) {
        accountByAccountNo.get(accountNo).deposit(amount);
    }

    public List<AccountTransaction> getStatement(String accountNo) {
        return accountByAccountNo.get(accountNo).transactions();
    }
}
