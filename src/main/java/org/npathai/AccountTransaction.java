package org.npathai;

import java.util.Objects;

public class AccountTransaction {
    String type;
    long amount;

    public AccountTransaction(String type, long amount) {
        this.type = type;
        this.amount = amount;
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
}
