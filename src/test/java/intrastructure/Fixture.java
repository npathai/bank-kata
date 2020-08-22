package intrastructure;

import org.mockito.Mockito;
import org.npathai.*;
import org.npathai.command.CommandExecutor;
import org.npathai.command.CommandFactory;
import org.npathai.domain.account.AccountService;
import org.npathai.domain.account.InMemoryAccounts;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class Fixture {
    MutableClock mutableClock = Mockito.spy(new MutableClock());
    final ZonedDateTime NOW = ZonedDateTime.now(mutableClock);
    BlockingTestingConsole console = new BlockingTestingConsole();
    BankApplication bankApplication = new BankApplication(console,
            Executors.newSingleThreadExecutor(), new CommandExecutor(new CommandFactory(new AccountService(new InMemoryAccounts()))));

    public Fixture() {
        bankApplication.start();
    }

    public void willReceive(String command) {
        mutableClock.set(NOW);
        console.enqueueCommand(command);
    }

    public String readOutput() {
        return console.dequeueOutput();
    }

    public void willReceive(String command, LocalDateTime dateOfReceipt) {
        mutableClock.set(dateOfReceipt.atZone(ZoneId.systemDefault()));
        console.enqueueCommand(command);
    }

    class BlockingTestingConsole extends Console {
        BlockingDeque<String> commands = new LinkedBlockingDeque<>();
        BlockingDeque<String> output = new LinkedBlockingDeque<>();

        @Override
        public String readLine() {
            try {
                return commands.pollFirst(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException("Console Read timeout");
            }
        }

        @Override
        public void write(String output) {
            this.output.offer(output);
        }

        void enqueueCommand(String command) {
            commands.offerLast(command);
        }

        String dequeueOutput() {
            try {
                return output.pollFirst(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException("Timeout waiting for output being written to console");
            }
        }
    }
}
