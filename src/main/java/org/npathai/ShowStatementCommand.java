package org.npathai;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShowStatementCommand implements Command {

    private final String command;
    private final AccountService accountService;

    public ShowStatementCommand(String command, AccountService accountService) {
        this.command = Objects.requireNonNull(command);
        this.accountService = Objects.requireNonNull(accountService);
    }

    @Override
    public List<String> execute() {
        String[] parts = command.split(" ");
        String accountNo = parts[0];
        ShowStatementRequest showStatementRequest = new ShowStatementRequest(accountNo);
        if (command.contains(" --type")) {
            String typeFilter = parts[3];
            showStatementRequest.typeFilter(typeFilter);
        }
        List<AccountTransaction> transactions = accountService.getStatement(showStatementRequest);
        List<String> statement = new ArrayList<>();
        statement.add("type||amount");
        for (AccountTransaction transaction : transactions) {
            statement.add(TransactionType.asString(transaction.type()) + "||" + transaction.amount());
        }
        return statement;
    }
}
