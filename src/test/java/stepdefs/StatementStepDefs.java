package stepdefs;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import intrastructure.AccountHolder;
import intrastructure.Fixture;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;

public class StatementStepDefs {
    private final Fixture application;
    private final AccountHolder accountHolder;
    private List<String> statement;

    public StatementStepDefs(Fixture application, AccountHolder accountHolder) {
        this.application = application;
        this.accountHolder = accountHolder;
    }

    @And("{string} opens account statement")
    public void opensAccountStatement(String accountHolderName) {
        application.willReceive(accountHolder.getAccountNoByName(accountHolderName) + " statement");
        statement = allTransactions(application.readOutput());
    }

    @When("{string} opens account statement filtered by type {string}")
    public void opensAccountStatementFilteredByType(String accountHolderName, String transactionType) {
        application.willReceive(accountHolder.getAccountNoByName(accountHolderName) + " statement --type "
                + toStatementType(transactionType));
        statement = allTransactions(application.readOutput());
    }

    @Then("{string} should see a withdrawal of Rs {int} in account")
    public void shouldSeeAWithdrawalOfInAccount(String accountHolderName, int amount) {
        applictionWillReceiveShowStatementCommand(accountHolder.getAccountNoByName(accountHolderName), " statement");
        assertThat(allTransactions(application.readOutput())).contains("D||" + amount);
    }

    private void applictionWillReceiveShowStatementCommand(String s, String s2) {
        application.willReceive(s + s2);
    }

    private List<String> allTransactions(String output) {
        return ImmutableList.copyOf(Splitter.on(System.lineSeparator()).split(output));
    }

    private String toStatementType(String transactionType) {
        return "deposit".equals(transactionType) ? "C" : "D";
    }

    @Then("{string} should see statement:")
    public void shouldSeeStatement(String accountHolderName, DataTable dataTable) {
        String expectedStatement = createStatementFrom(dataTable);
        assertThat(statement).isEqualTo(allTransactions(expectedStatement));
    }

    private String createStatementFrom(DataTable dataTable) {
        String statement = dataTable.asLists().stream()
                .map(row -> String.join("||", row))
                .collect(Collectors.joining(System.lineSeparator()));
        return statement;
    }

    @When("{string} opens account statement filtered by date from {string} to {string}")
    public void opensAccountStatementFilteredByDateFromTo(String accountHolderName, String fromDate, String toDate) {
        application.willReceive(accountHolder.getAccountNoByName(accountHolderName) + " statement --date "
                + fromDate + " " + toDate);
        statement = allTransactions(application.readOutput());
    }
}
