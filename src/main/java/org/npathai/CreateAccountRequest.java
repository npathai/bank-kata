package org.npathai;

public class CreateAccountRequest {
    private final String accountHolderName;

    public CreateAccountRequest(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String accountHolderName() {
        return accountHolderName;
    }
}
