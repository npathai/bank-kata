package org.npathai;

public class AccountUnderflowException extends AccountException {
    private final int minBalance;

    public AccountUnderflowException(int minBalance) {
        this.minBalance = minBalance;
    }

    public int minBalance() {
        return minBalance;
    }
}
