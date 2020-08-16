package org.npathai.domain.account;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AccountService {
    private Map<String, Account> accountByAccountNo = new HashMap<>();

    public Account createAccount(CreateAccountRequest request) {
        Account account = new Account(request.accountHolderName(), request.isZeroBalance() ? 0 : Account.MIN_BALANCE);
        accountByAccountNo.put(account.accountNo(), account);
        return account;
    }

    public void depositAccount(DepositRequest depositRequest) throws AccountClosedException {
        accountByAccountNo.get(depositRequest.accountNo()).deposit(depositRequest.amount());
    }

    public void withdrawAccount(WithdrawRequest withdrawRequest) throws AccountException {
        accountByAccountNo.get(withdrawRequest.accountNo()).withdraw(withdrawRequest.amount());
    }

    public void transfer(TransferRequest transferRequest) throws InsufficientFundsException {
        Account fromAccount = accountByAccountNo.get(transferRequest.fromAccountNo());
        Account toAccount = accountByAccountNo.get(transferRequest.toAccountNo());
        fromAccount.withdraw(transferRequest.amount());
        try {
            toAccount.deposit(transferRequest.amount());
        } catch (AccountClosedException ex) {
            fromAccount.deposit(transferRequest.amount());
            throw new TransferFailedException(ex);
        }
    }

    public List<AccountTransaction> getStatement(ShowStatementRequest showStatementRequest) {
        Predicate<AccountTransaction> typeFilter = typeFilterFrom(showStatementRequest);
        return accountByAccountNo.get(showStatementRequest.accountNo()).transactions()
                .stream()
                .filter(typeFilter)
                .collect(Collectors.toList());
    }

    private Predicate<AccountTransaction> typeFilterFrom(ShowStatementRequest showStatementRequest) {
        if (showStatementRequest.typeFilter() == null) {
            return type -> true;
        } else {
            TransactionType filterType = TransactionType.typeFrom(showStatementRequest.typeFilter());
            return type -> type.type() == filterType;
        }
    }

    public void close(CloseRequest closeRequest) {
        accountByAccountNo.get(closeRequest.accountNo()).close();
    }
}
