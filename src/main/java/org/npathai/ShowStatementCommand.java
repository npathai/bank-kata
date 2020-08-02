package org.npathai;

import java.util.ArrayList;
import java.util.List;

public class ShowStatementCommand implements Command {

    private final String command;
    private final AccountService accountService;

    public ShowStatementCommand(String command, AccountService accountService) {
        this.command = command;
        this.accountService = accountService;
    }

    @Override
    public List<String> execute() {
        String accountNo = command.split(" ")[0];
        List<AccountTransaction> transactions = accountService.getStatement(accountNo);
        List<String> statement = new ArrayList<>();
        statement.add("type||amount");
        for (AccountTransaction transaction : transactions) {
            statement.add(transaction.type + "||" + transaction.amount);
        }
        return statement;
    }
}
