package org.npathai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountService {
    private Map<String, Account> accountByAccountNo = new HashMap<>();

    public List<AccountTransaction> getStatement(String accountNo) {
        return accountByAccountNo.get(accountNo).transactions();
    }

    public void transfer(String fromAccountNo, String toAccountNo, int amount) {
        Account fromAccount = accountByAccountNo.get(fromAccountNo);
        Account toAccount = accountByAccountNo.get(toAccountNo);
        fromAccount.withdraw(amount);
        toAccount.deposit(amount);
    }

    public void close(String accountNo) {
        accountByAccountNo.get(accountNo).close();
    }

    public Account createAccount(CreateAccountRequest request) {
        Account account = new Account(request.accountHolderName());
        accountByAccountNo.put(account.accountNo(), account);
        return account;
    }

    public void depositAccount(DepositRequest depositRequest) {
        accountByAccountNo.get(depositRequest.accountNo()).deposit(depositRequest.amount());
    }

    public void withdrawAccount(WithdrawRequest withdrawRequest) {
        accountByAccountNo.get(withdrawRequest.accountNo()).withdraw(withdrawRequest.amount());
    }
}
