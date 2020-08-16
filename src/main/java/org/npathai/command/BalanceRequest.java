package org.npathai.command;

public class BalanceRequest {
    private final String accountNo;

    public BalanceRequest(String accountNo) {
        this.accountNo = accountNo;
    }

    public String accountNo() {
        return accountNo;
    }
}
