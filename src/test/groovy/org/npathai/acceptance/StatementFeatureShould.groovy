package org.npathai.acceptance

import org.npathai.acceptance.infrastructure.BankSpecification

class StatementFeatureShould extends BankSpecification {

    def "print all transactions from account in order they occurred"() {
        given: "Alice holds account with our bank"
        user "Alice" opens_account()
        and: "Alice makes a few transactions"
        user "Alice" deposits 500.00 on "10-01-2020"
        user "Alice" deposits 1000.00 on "11-01-2020"
        user "Alice" withdraws 500.00 on "12-01-2020"
        when: "She prints the account statement"
        user "Alice" prints_statement()
        start()
        then: "She would see all the transactions in order they occurred"
        user "Alice" sees([
            "Type||Amount||Date",
            "C||500.00||10-01-2020",
            "C||1000.00||11-01-2020",
            "D||500.00||12-01-2020"
        ])
    }
}
