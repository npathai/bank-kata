package org.npathai.domain.account;

import org.npathai.command.BalanceRequest;

import java.time.Clock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AccountService {
    private final InMemoryAccounts accounts;

    public AccountService(InMemoryAccounts accounts, Clock clock) {
        this.accounts = accounts;
    }

    public Account createAccount(CreateAccountRequest request) {
        Account account = new Account(request.accountHolderName(), request.isZeroBalance() ? 0 : Account.MIN_BALANCE);
        accounts.save(account);
        return account;
    }

    public void depositAccount(DepositRequest depositRequest) throws AccountClosedException {
        accounts.get(depositRequest.accountNo()).deposit(depositRequest.amount());
    }

    public void withdrawAccount(WithdrawRequest withdrawRequest) throws AccountException {
        accounts.get(withdrawRequest.accountNo()).withdraw(withdrawRequest.amount());
    }

    public void transfer(TransferRequest transferRequest) throws InsufficientFundsException {
        Account fromAccount = accounts.get(transferRequest.fromAccountNo());
        Account toAccount = accounts.get(transferRequest.toAccountNo());
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
        return accounts.get(showStatementRequest.accountNo()).transactions()
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
        accounts.get(closeRequest.accountNo()).close();
    }

    public Long getBalance(BalanceRequest balanceRequest) {
        return accounts.get(balanceRequest.accountNo()).balance();
    }
}
