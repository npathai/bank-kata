package org.npathai.domain.account;

import org.npathai.domain.account.AccountException;

public class AccountUnderflowException extends AccountException {
    private final int minBalance;

    public AccountUnderflowException(int minBalance) {
        this.minBalance = minBalance;
    }

    public int minBalance() {
        return minBalance;
    }
}
