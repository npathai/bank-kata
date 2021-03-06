package org.npathai.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.npathai.domain.account.AccountService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommandFactoryTest {
    static final String ACCOUNT_NO = UUID.randomUUID().toString();
    static final String OPEN_ACCOUNT_COMMAND = "open account Alice";
    static final String OPEN_ZERO_BALANCE_ACCOUNT_COMMAND = "open zero balance account Alice";
    static final String WITHDRAW_COMMAND = ACCOUNT_NO + " withdraw 1000";
    static final String DEPOSIT_COMMAND = ACCOUNT_NO + " deposit 1000";
    static final String SHOW_STATEMENT_COMMAND = ACCOUNT_NO + " statement";
    static final String SHOW_STATEMENT_WITH_FILTER_COMMAND = ACCOUNT_NO + " statement --type C";
    static final String TRANSFER_COMMAND = "transfer " + ACCOUNT_NO + " " + UUID.randomUUID().toString() + " 1000";
    static final String CLOSE_COMMAND = "close " + ACCOUNT_NO;
    static final String BALANCE_COMMAND = ACCOUNT_NO + " balance";

    @Mock
    AccountService accountService;

    CommandFactory commandFactory;

    @BeforeEach
    public void initialize() {
        MockitoAnnotations.initMocks(this);
        commandFactory = new CommandFactory(accountService);
    }
    
    @Test
    public void returnsOpenAccountCommand() {
        assertThat(commandFactory.createCommand(OPEN_ACCOUNT_COMMAND)).isInstanceOf(OpenAccountCommand.class);
    }

    @Test
    public void returnsWithdrawAccountCommand() {
        assertThat(commandFactory.createCommand(WITHDRAW_COMMAND)).isInstanceOf(WithdrawCommand.class);
    }

    @Test
    public void returnsDepositAccountCommand() {
        assertThat(commandFactory.createCommand(DEPOSIT_COMMAND)).isInstanceOf(DepositCommand.class);
    }

    @Test
    public void returnsShowStatementCommand() {
        assertThat(commandFactory.createCommand(SHOW_STATEMENT_COMMAND)).isInstanceOf(ShowStatementCommand.class);
    }

    @Test
    public void returnsShowStatementCommandWhenFilterIsProvided() {
        assertThat(commandFactory.createCommand(SHOW_STATEMENT_WITH_FILTER_COMMAND)).isInstanceOf(ShowStatementCommand.class);
    }

    @Test
    public void returnsTransferCommand() {
        assertThat(commandFactory.createCommand(TRANSFER_COMMAND)).isInstanceOf(TransferCommand.class);
    }

    @Test
    public void returnsCloseAccountCommand() {
        assertThat(commandFactory.createCommand(CLOSE_COMMAND)).isInstanceOf(CloseCommand.class);
    }

    @Test
    public void returnsOpenZeroBalanceAccountCommand() {
        assertThat(commandFactory.createCommand(OPEN_ZERO_BALANCE_ACCOUNT_COMMAND)).isInstanceOf(OpenZeroBalanceAccountCommand.class);
    }

    @Test
    public void returnsAccountBalanceCommand() {
        assertThat(commandFactory.createCommand(BALANCE_COMMAND)).isInstanceOf(BalanceCommand.class);
    }

    @Test
    public void throwsUnknownCommandExceptionWhenCommandIsUnknown() {
        assertThatThrownBy(() -> commandFactory.createCommand("unknown")).isInstanceOf(UnknownCommandException.class);
    }
}