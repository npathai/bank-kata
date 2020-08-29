package org.npathai.domain.account;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Account {
    public static final int MIN_BALANCE = 500;

    private final String accountNo = UUID.randomUUID().toString();
    private final String accountHolderName;
    private final int minBalance;
    private final List<AccountTransaction> transactionList = new ArrayList<>();
    private boolean closed;

    public Account(String accountHolderName, int minBalance) {
        this.accountHolderName = accountHolderName;
        this.minBalance = minBalance;
    }

    public void deposit(long amount, ZonedDateTime time) {
        checkState();
        transactionList.add(new AccountTransaction(TransactionType.CREDIT, amount, time));
    }

    private void checkState() {
        if (closed) {
            throw new AccountClosedException();
        }
    }

    public void withdraw(int amount, ZonedDateTime time) {
        checkState();
        checkConstraints(amount);
        transactionList.add(new AccountTransaction(TransactionType.DEBIT, amount, time));
    }

    private void checkConstraints(int amount) {
        if (balance() < amount) {
            throw new InsufficientFundsException();
        }
        if (balance() - amount < minBalance) {
            throw new AccountUnderflowException(MIN_BALANCE);
        }
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