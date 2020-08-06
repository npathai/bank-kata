package org.npathai;

public class WithdrawRequest {
    private final String accountNo;
    private final int amount;

    public WithdrawRequest(String accountNo, int amount) {
        this.accountNo = accountNo;
        this.amount = amount;
    }

    public int amount() {
        return amount;
    }

    public String accountNo() {
        return accountNo;
    }
}
