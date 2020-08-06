package org.npathai;

public class CloseRequest {
    private final String accountNo;

    public CloseRequest(String accountNo) {
        this.accountNo = accountNo;
    }

    public String accountNo() {
        return accountNo;
    }
}
