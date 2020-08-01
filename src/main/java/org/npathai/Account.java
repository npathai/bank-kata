package org.npathai;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Account {
    private String accountNo = UUID.randomUUID().toString();
    private long balance;
    private final String accountHolderName;
    private List<AccountTransaction> transactionList = new ArrayList<>();

    public Account(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public void deposit(long amount) {
        balance += amount;
        transactionList.add(new AccountTransaction("C", amount));
    }

    public void withdraw(int amount) {
        balance -= amount;
        transactionList.add(new AccountTransaction("D", amount));
    }

    public String accountNo() {
        return accountNo;
    }

    class AccountTransaction {
        String type;
        long amount;

        public AccountTransaction(String type, long amount) {
            this.type = type;
            this.amount = amount;
        }
    }
}