Feature: Account Statement
  Scenario: Show account statement
    Given Alice is an account holder
    When She deposits Rs 1000 to her account
    And She deposits Rs 500 to her account
    And She withdraws Rs 100 from her account
    Then She can see account statement containing all the transactions