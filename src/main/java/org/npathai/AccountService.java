package org.npathai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountService {
    private Map<String, Account> accountByAccountNo = new HashMap<>();

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

    public void transfer(TransferRequest transferRequest) {
        Account fromAccount = accountByAccountNo.get(transferRequest.fromAccountNo());
        Account toAccount = accountByAccountNo.get(transferRequest.toAccountNo());
        fromAccount.withdraw(transferRequest.amount());
        toAccount.deposit(transferRequest.amount());
    }

    public List<AccountTransaction> getStatement(ShowStatementRequest showStatementRequest) {
        return accountByAccountNo.get(showStatementRequest.accountNo()).transactions();
    }
}
