package org.npathai.command;

import org.npathai.domain.account.AccountService;

import java.util.List;

public class BalanceCommand implements Command {

    private final String balanceCommand;
    private final AccountService accountService;

    public BalanceCommand(String balanceCommand, AccountService accountService) {
        this.balanceCommand = balanceCommand;
        this.accountService = accountService;
    }

    @Override
    public CommandResponse execute() {
        String accountNo = balanceCommand.split(" ")[0];
        BalanceRequest balanceRequest = new BalanceRequest(accountNo);
        Long balance = accountService.getBalance(balanceRequest);
        return new CommandResponse(List.of("Balance Rs " + balance));
    }
}
