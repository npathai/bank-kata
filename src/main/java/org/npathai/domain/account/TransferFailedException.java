package org.npathai.domain.account;

import org.npathai.domain.account.AccountClosedException;

public class TransferFailedException extends RuntimeException {

    public TransferFailedException(AccountClosedException ex) {
        super(ex);
    }
}
