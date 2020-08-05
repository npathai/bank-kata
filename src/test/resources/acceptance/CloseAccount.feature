Feature: Close Account
  Scenario: As an account holder, I want to close account so that I can transfer
    my account to some other bank

    Given "Alice" is an account holder with initial balance of Rs 1000
    When "Alice" closes her account
    Then "Alice" cannot make any further transactions from account