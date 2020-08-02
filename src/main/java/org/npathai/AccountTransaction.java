package org.npathai;

public class AccountTransaction {
    String type;
    long amount;

    public AccountTransaction(String type, long amount) {
        this.type = type;
        this.amount = amount;
    }
}
