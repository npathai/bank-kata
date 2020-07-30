package org.npathai.acceptance.infrastructure

import org.npathai.BankApplication
import org.npathai.Console
import spock.lang.Specification

import static org.assertj.core.api.Assertions.assertThat

class BankSpecification extends Specification {

    Console console = new TestingConsole()
    BankApplication bankApplication = new BankApplication(console)

    def user(name) {
        return new UserDSL(name)
    }

    void start() {
        console.enqueueCommand("q")
        bankApplication.start()
    }

    class UserDSL {
        String name
        String accountNo

        UserDSL(name) {
            this.name = name
        }

        TransactionDSL deposits(double amount) {
            return new TransactionDSL("deposit", amount)
        }

        TransactionDSL withdraws(double amount) {
            return new TransactionDSL("withdraw", amount)
        }

        void sees(List<String> records) {
            for (String record : records) {
                assertThat(console.dequeueOutput()).isEqualTo(record)
            }
        }

        void opens_account() {
            String command = "open " + name
            console.enqueueCommand(command)
            String accountNo = console.dequeueOutput()
            assertThat(accountNo).isNotNull()
            this.accountNo = accountNo
        }

        void prints_statement() {
            String command = "${accountNo} print statement"
            console.enqueueCommand(command)
        }

        class TransactionDSL {
            private final double amount
            private final String type

            TransactionDSL(String type, double amount) {
                this.type = type
                this.amount = amount
            }

            void on(String date) {
                String command = "${accountNo} ${type} ${amount}"
                console.enqueueCommand(command)
            }
        }
    }


    class TestingConsole extends Console {
        Deque<String> commands = new LinkedList<>()
        Deque<String> output = new LinkedList<>()

        @Override
        String readLine() {
            return commands.removeFirst()
        }

        @Override
        void write(String input) {
            output.add(input)
        }

        void enqueueCommand(String command) {
            commands.addLast(command)
        }

        String dequeueOutput() {
            return output.removeFirst()
        }
    }
}
