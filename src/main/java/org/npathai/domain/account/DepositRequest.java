package org.npathai.domain.account;

public class DepositRequest {
    private final String accountNo;
    private final int amount;

    public DepositRequest(String accountNo, int amount) {
        this.accountNo = accountNo;
        this.amount = amount;
    }

    public String accountNo() {
        return accountNo;
    }

    public int amount() {
        return amount;
    }
}
