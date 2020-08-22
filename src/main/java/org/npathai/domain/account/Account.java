package org.npathai.domain.account;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Account {
    public static final int MIN_BALANCE = 500;

    private String accountNo = UUID.randomUUID().toString();
    private final String accountHolderName;
    private final int minBalance;
    private List<AccountTransaction> transactionList = new ArrayList<>();
    private boolean closed;

    public Account(String accountHolderName, int minBalance) {
        this.accountHolderName = accountHolderName;
        this.minBalance = minBalance;
    }

    public void deposit(long amount, ZonedDateTime time) {
        if (closed) {
            throw new AccountClosedException();
        }
        transactionList.add(new AccountTransaction(TransactionType.CREDIT, amount, time));
    }

    public void withdraw(int amount, ZonedDateTime time) {
        if (closed) {
            throw new AccountClosedException();
        }
        if (balance() < amount) {
            throw new InsufficientFundsException();
        }
        if (balance() - amount < minBalance) {
            throw new AccountUnderflowException(MIN_BALANCE);
        }
        transactionList.add(new AccountTransaction(TransactionType.DEBIT, amount, time));
    }

    public long balance() {
        long balance = 0;
        for (AccountTransaction accountTransaction : transactions()) {
            if (accountTransaction.type() == TransactionType.CREDIT) {
                balance += accountTransaction.amount();
            } else {
                balance -= accountTransaction.amount();
            }
        }
        return balance;
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