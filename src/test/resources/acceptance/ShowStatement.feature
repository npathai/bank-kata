Feature: Account Statement
  Scenario: As an account holder, I want to see the past transactions, so that I can verify
    transactions I made
    Given "Alice" is an account holder
    When "Alice" deposits Rs 1000 to her account
    And "Alice" deposits Rs 500 to her account
    And "Alice" withdraws Rs 100 from her account
    Then "Alice" can see account statement containing all the transactions (in order they occurred)