Feature: Account Statement
  Scenario: Show account statement
    Given Alice is an account holder
    When She makes a few transactions
    Then She can see account statement containing all the transactions