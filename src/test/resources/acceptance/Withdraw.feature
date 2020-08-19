Feature: Withdraw Amount
  Scenario: As a bank, I want to all account holders to maintain minimum balance of 500 in account,
  for account to remain valid

    Given "Alice" is an account holder with initial balance of Rs 1000
    When "Alice" tries to withdraw Rs 501 from her account
    Then "Alice" should fail to withdraw amount due to minimum balance requirement