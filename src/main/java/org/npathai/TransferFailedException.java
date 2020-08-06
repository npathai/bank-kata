package org.npathai;

public class TransferFailedException extends RuntimeException {

    public TransferFailedException(AccountClosedException ex) {
        super(ex);
    }
}
