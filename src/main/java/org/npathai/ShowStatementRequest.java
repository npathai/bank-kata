package org.npathai;

public class ShowStatementRequest {
    private final String accountNo;
    private String typeFilter;

    public ShowStatementRequest(String accountNo) {
        this.accountNo = accountNo;
    }

    public String accountNo() {
        return accountNo;
    }

    public String typeFilter() {
        return typeFilter;
    }

    public void typeFilter(String typeFilter) {
        this.typeFilter = typeFilter;
    }
}
