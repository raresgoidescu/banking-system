package org.poo.main.Bank.Commands.SpecificCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account.Account;
import org.poo.main.Bank.Bank;
import org.poo.main.Bank.Commands.Command;
import org.poo.main.Bank.MonetaryValue;
import org.poo.main.Entities.User;

import static org.poo.main.Utils.Utils.info;
import static org.poo.main.Utils.Utils.warn;

public class AddFundsCommand implements Command {
  private final Bank bank = Bank.getInstance(null);

  /**
   * Executes the command to add funds to a specific account.
   */
  @Override
  public ObjectNode execute(final CommandInput input) {
    Account account = bank.getAccount(input.getAccount());
    User user = bank.getUser(input.getEmail());

    if (account == null) {
      warn("Account not found.");
      return null;
    }

    MonetaryValue monetaryValue = new MonetaryValue(input.getAmount(), account.getCurrency());

    if (!account.checkAccess(user)) {
      warn("User does not have access to this account.");
      return null;
    }

    info(user + " adding " + monetaryValue.getAmount() + " "
            + monetaryValue.getCurrency() + " to account " + account);

    try {
      account.deposit(user, monetaryValue);
    } catch (Exception e) {
      warn(e.getMessage());
    }

    info("%s", account);

    return null;
  }
}
