package org.npathai;

public class CreateAccountRequest {
    private final String accountHolderName;
    private final boolean isZeroBalance;

    public CreateAccountRequest(String accountHolderName, boolean isZeroBalance) {
        this.accountHolderName = accountHolderName;
        this.isZeroBalance = isZeroBalance;
    }

    public String accountHolderName() {
        return accountHolderName;
    }

    public boolean isZeroBalance() {
        return isZeroBalance;
    }
}
