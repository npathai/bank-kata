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

public class StatementStepDefs {
    private final Fixture application;
    private final AccountHolder accountHolder;
    private List<String> statement;

    public StatementStepDefs(Fixture application, AccountHolder accountHolder) {
        this.application = application;
        this.accountHolder = accountHolder;
    }

    @When("{string} sees account statement filtered by type {string}")
    public void seesAccountStatementFilteredByType(String accountHolderName, String transactionType) {
        application.willReceive(accountHolder.getAccountNoByName(accountHolderName) + " statement --type "
                + toStatementType(transactionType));
        statement = allTransactions(application.readOutput());
    }

    @Then("{string} can see account statement containing {int} transactions of type {string}")
    public void canSeeAccountStatementContainingTransactionsOfType(String accountHolderName, int count,
                                                                   String transactionType) {
        // Doing size - 1 to remove the statement header
        assertThat(statement.size() - 1).isEqualTo(count);
        String statementType = toStatementType(transactionType);
        for (int i = 1; i < count; i++) {
            assertThat(statement.get(i)).contains(statementType);
        }
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

    @And("{string} should see a credit of Rs {int} in account")
    public void shouldSeeACreditOfInAccount(String accountHolderName, int amount) {
        application.willReceive(accountHolder.getAccountNoByName(accountHolderName) + " statement");
        assertThat(allTransactions(application.readOutput())).contains("C||" + amount);
    }

    private String toStatementType(String transactionType) {
        return "deposit".equals(transactionType) ? "C" : "D";
    }

    @Then("{string} should see statement:")
    public void shouldSeeStatement(String accountHolderName, DataTable dataTable) {
        application.willReceive(accountHolder.getAccountNoByName(accountHolderName) + " statement");
        String statement = createStatementFrom(dataTable);
        assertThat(application.readOutput()).isEqualTo(statement);
    }

    private String createStatementFrom(DataTable dataTable) {
        String statement = dataTable.asLists().stream()
                .map(row -> String.join("||", row))
                .collect(Collectors.joining(System.lineSeparator()));
        return statement;
    }

    @Then("{string} can see account statement containing all the transactions \\(in order they occurred)")
    public void verify_can_see_account_statement(String accountHolderName) {
        application.willReceive(accountHolder.getAccountNoByName(accountHolderName) + " statement");
        String statement = Joiner.on(System.lineSeparator()).join(List.of(
                "type||amount",
                "C||1000",
                "C||500",
                "D||100"
        ));
        assertThat(application.readOutput()).isEqualTo(statement);
    }
}
