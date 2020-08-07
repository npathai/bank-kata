package org.npathai;

import java.util.Objects;

public class AccountTransaction {
    private TransactionType type;
    private long amount;

    public AccountTransaction(TransactionType type, long amount) {
        this.type = type;
        this.amount = amount;
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
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, amount);
    }

    @Override
    public String toString() {
        return "AccountTransaction{" +
                "type='" + type + '\'' +
                ", amount=" + amount +
                '}';
    }
}
