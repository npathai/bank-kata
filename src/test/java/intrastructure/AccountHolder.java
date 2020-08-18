package intrastructure;

import java.util.HashMap;
import java.util.Map;

public class AccountHolder {
    private final Map<String, String> accountNoByAccountHolderName;

    public AccountHolder() {
        accountNoByAccountHolderName = new HashMap<>();
    }

    public void saveAccount(String accountHolderName, String accountNo) {
        accountNoByAccountHolderName.put(accountHolderName, accountNo);
    }

    public String getAccountNoByName(String accountHolderName) {
        return accountNoByAccountHolderName.get(accountHolderName);
    }
}
