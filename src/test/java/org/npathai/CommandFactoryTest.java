package org.npathai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CommandFactoryTest {
    static final String ACCOUNT_NO = UUID.randomUUID().toString();
    static final String OPEN_ACCOUNT_COMMAND = "open account Alice";
    static final String WITHDRAW_COMMAND = ACCOUNT_NO + " withdraw 1000";
    static final String DEPOSIT_COMMAND = ACCOUNT_NO + " deposit 1000";
    static final String SHOW_STATEMENT_COMMAND = ACCOUNT_NO + " statement";

    CommandFactory commandFactory;

    @BeforeEach
    public void initialize() {
        commandFactory = new CommandFactory();
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
}