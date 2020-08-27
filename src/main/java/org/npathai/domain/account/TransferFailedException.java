package org.npathai.domain.account;

public class TransferFailedException extends RuntimeException {

    public TransferFailedException(AccountException ex) {
        super(ex);
    }
}
