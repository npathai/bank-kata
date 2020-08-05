package org.npathai;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Account {
    private String accountNo = UUID.randomUUID().toString();
    private final String accountHolderName;
    private List<AccountTransaction> transactionList = new ArrayList<>();
    private boolean closed;

    public Account(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public void deposit(long amount) {
        if (closed) {
            throw new AccountClosedException();
        }
        transactionList.add(new AccountTransaction("C", amount));
    }

    public void withdraw(int amount) {
        if (closed) {
            throw new AccountClosedException();
        }
        transactionList.add(new AccountTransaction("D", amount));
    }

    public String accountNo() {
        return accountNo;
    }

    public String accountHolderName() {
        return accountHolderName;
    }

    public List<AccountTransaction> transactions() {
        return transactionList;
    }

    public void close() {
        closed = true;
    }
}