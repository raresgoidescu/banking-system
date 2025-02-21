package org.poo.main.Bank.Commands.SpecificCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account.Account;
import org.poo.main.Bank.Bank;
import org.poo.main.Bank.Commands.Command;
import org.poo.main.Entities.User;

import static org.poo.main.Utils.Utils.success;

public class AddAccountCommand implements Command {
  private final Bank bank = Bank.getInstance(null);

  /**
   * Executes the command to add a new account for a user based on the provided input.
   * Retrieves the user associated with the input email, creates a new account,
   * and adds it to the bank.
   */
  @Override
  public ObjectNode execute(final CommandInput input) {
    User user = bank.getUser(input.getEmail());

    Account account = user.addAccount(
            input.getAccountType(),
            input.getCurrency(),
            input.getInterestRate(),
            input.getTimestamp()
    );

    bank.addAccount(account);

    success(String.format(
            "Created %s account for %s", input.getAccountType(), input.getEmail())
    );

    return null;
  }
}
