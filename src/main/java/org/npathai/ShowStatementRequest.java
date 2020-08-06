package org.npathai;

public class ShowStatementRequest {
    private final String accountNo;

    public ShowStatementRequest(String accountNo) {
        this.accountNo = accountNo;
    }

    public String accountNo() {
        return accountNo;
    }
}
