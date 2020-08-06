package org.npathai;

public class TransferRequest {
    private final String fromAccountNo;
    private final String toAccountNo;
    private final int amount;

    public TransferRequest(String fromAccountNo, String toAccountNo, int amount) {
        this.fromAccountNo = fromAccountNo;
        this.toAccountNo = toAccountNo;
        this.amount = amount;
    }

    public String fromAccountNo() {
        return fromAccountNo;
    }

    public String toAccountNo() {
        return toAccountNo;
    }

    public int amount() {
        return amount;
    }
}
