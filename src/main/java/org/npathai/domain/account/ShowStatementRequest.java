package org.npathai.domain.account;

public class ShowStatementRequest {
    private final String accountNo;
    private String typeFilter;
    private String fromDate;
    private String toDate;

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

    public void fromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public void toDate(String toDate) {
        this.toDate = toDate;
    }

    public String fromDate() {
        return fromDate;
    }

    public String toDate() {
        return toDate;
    }
}
