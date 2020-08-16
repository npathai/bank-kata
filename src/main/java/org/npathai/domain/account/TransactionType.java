package org.npathai.domain.account;

public enum TransactionType {
    DEBIT,
    CREDIT;

    public static TransactionType typeFrom(String type) {
        return type.equals("C") ? TransactionType.CREDIT : TransactionType.DEBIT;
    }

    public static String asString(TransactionType type) {
        return type == TransactionType.CREDIT ? "C" : "D";
    }
}
