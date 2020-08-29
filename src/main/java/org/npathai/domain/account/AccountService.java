package org.npathai.domain.account;

import org.npathai.command.BalanceRequest;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AccountService {
    private final InMemoryAccounts accounts;
    private Clock clock;

    public AccountService(InMemoryAccounts accounts, Clock clock) {
        this.accounts = accounts;
        this.clock = clock;
    }

    public Account createAccount(CreateAccountRequest request) {
        Account account = new Account(request.accountHolderName(), request.isZeroBalance() ? 0 : Account.MIN_BALANCE);
        accounts.save(account);
        return account;
    }

    public void depositAccount(DepositRequest depositRequest) throws AccountClosedException {
        accounts.get(depositRequest.accountNo()).deposit(depositRequest.amount(), clock.instant().atZone(ZoneId.systemDefault()));
    }

    public void withdrawAccount(WithdrawRequest withdrawRequest) throws AccountException {
        accounts.get(withdrawRequest.accountNo()).withdraw(withdrawRequest.amount(), clock.instant().atZone(ZoneId.systemDefault()));
    }

    public void transfer(TransferRequest transferRequest) throws InsufficientFundsException {
        Account fromAccount = accounts.get(transferRequest.fromAccountNo());
        Account toAccount = accounts.get(transferRequest.toAccountNo());
        try {
            fromAccount.withdraw(transferRequest.amount(), clock.instant().atZone(ZoneId.systemDefault()));
        } catch (AccountException ex) {
            throw new TransferFailedException(ex);
        }

        try {
            toAccount.deposit(transferRequest.amount(), clock.instant().atZone(ZoneId.systemDefault()));
        } catch (AccountException ex) {
            fromAccount.deposit(transferRequest.amount(), clock.instant().atZone(ZoneId.systemDefault()));
            throw new TransferFailedException(ex);
        }
    }

    public List<AccountTransaction> getStatement(ShowStatementRequest showStatementRequest) {
        Predicate<AccountTransaction> typeFilter = typeFilterFrom(showStatementRequest);
        Predicate<AccountTransaction> dateFilter = dateFilterFrom(showStatementRequest);
        return accounts.get(showStatementRequest.accountNo()).transactions()
                .stream()
                .filter(typeFilter)
                .filter(dateFilter)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    private Predicate<AccountTransaction> dateFilterFrom(ShowStatementRequest showStatementRequest) {
        if (showStatementRequest.fromDate() == null) {
            return type -> true;
        }

        ZonedDateTime fromDate = LocalDate.parse(showStatementRequest.fromDate(),
                DateTimeFormatter.ofPattern("dd/MM/yyyy")).atTime(0, 0, 0)
                .atZone(ZoneId.systemDefault());
        ZonedDateTime toDate = LocalDate.parse(showStatementRequest.toDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                .atTime(0, 0, 0)
                .atZone(ZoneId.systemDefault());

        return transaction -> {
            ZonedDateTime transactionDate = transaction.time().withHour(0).withMinute(0).withSecond(0).withNano(0);
            if (transactionDate.isEqual(fromDate)) {
                return true;
            }
            if (transactionDate.isAfter(fromDate) && transaction.time().isBefore(toDate)) {
                return true;
            }
            return transactionDate.isEqual(toDate);
        };
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
