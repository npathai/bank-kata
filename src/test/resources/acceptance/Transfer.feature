Feature: Transfer Funds
  Scenario: As an account holder, I want to transfer funds, so that I can pay
    funds online to known payees

    Given "Alice" is an account holder with initial balance of Rs 1000
    And "Bob" is an account holder
    When "Alice" transfers Rs 1000 to "Bob"'s account
    Then "Alice" should see a withdrawal of Rs 1000 in account
    And "Bob" should see a credit of Rs 1000 in account