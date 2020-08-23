package org.npathai.domain.account;

import java.time.ZonedDateTime;
import java.util.Objects;

public class AccountTransaction implements Comparable<AccountTransaction> {
    private TransactionType type;
    private long amount;
    private ZonedDateTime transactionTime;

    public AccountTransaction(TransactionType type, long amount, ZonedDateTime zonedDateTime) {
        this.type = type;
        this.amount = amount;
        this.transactionTime = zonedDateTime;
    }

    public TransactionType type() {
        return type;
    }

    public long amount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountTransaction that = (AccountTransaction) o;
        return amount == that.amount &&
                type == that.type &&
                Objects.equals(transactionTime, that.transactionTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, amount, transactionTime);
    }

    @Override
    public String toString() {
        return "AccountTransaction{" +
                "type=" + type +
                ", amount=" + amount +
                ", transactionTime=" + transactionTime +
                '}';
    }

    @Override
    public int compareTo(AccountTransaction o) {
        return transactionTime.compareTo(o.transactionTime);
    }
}
