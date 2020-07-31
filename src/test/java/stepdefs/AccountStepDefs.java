package stepdefs;

import intrastructure.Fixture;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountStepDefs {

    Fixture application = new Fixture();
    private String accountNo;

    public AccountStepDefs() {
        System.out.println("Instance of AccountStepDefs created");
    }

    @Given("Alice is an account holder")
    public void alice_is_an_account_holder() {
        application.willReceive("open account " + "Alice");
        this.accountNo = application.readOutput();
        System.out.println("Created account " + accountNo);
    }

    @Then("She can see account statement containing all the transactions")
    public void verify_can_see_account_statement() {
        application.willReceive(accountNo + " statement");
        assertThat(application.readOutput()).isEqualTo("type||amount");
        assertThat(application.readOutput()).isEqualTo("C||1000");
        assertThat(application.readOutput()).isEqualTo("C||500");
        assertThat(application.readOutput()).isEqualTo("D||100");
    }

    @When("She deposits Rs {int} to her account")
    public void sheDepositsRsToHerAccount(int amount) {
        application.willReceive(accountNo + " deposit " + amount);
    }

    @And("She withdraws Rs {int} from her account")
    public void sheWithdrawsRsFromHerAccount(int amount) {
        application.willReceive(accountNo + " withdraw " + amount);
    }
}
