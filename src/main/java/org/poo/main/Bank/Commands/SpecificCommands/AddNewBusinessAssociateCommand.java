package org.poo.main.Bank.Commands.SpecificCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account.Account;
import org.poo.main.Account.BusinessAccount;
import org.poo.main.Bank.Bank;
import org.poo.main.Bank.Commands.Command;
import org.poo.main.Entities.User;

import static org.poo.main.Utils.Utils.info;
import static org.poo.main.Utils.Utils.warn;

public final class AddNewBusinessAssociateCommand implements Command {
  private final Bank bank = Bank.getInstance(null);

  /**
   * Executes the command to add a new business associate to a business account.
   * If the user or account is not found, an error is logged, and the method returns null.
   * If the role is invalid, an error is logged, and the method returns null.
   */
  @Override
  public ObjectNode execute(final CommandInput input) {
    User user = bank.getUser(input.getEmail());
    Account account = bank.getAccount(input.getAccount());
    String role = input.getRole();

    info(String.format("Adding new business associate %s (role: %s)\n\tto account %s",
            user, role, account));

    if (user == null) {
      warn("User not found");
      return null;
    }

    if (account == null) {
      warn("Account not found");
      return null;
    }

    if (!account.getType().equals("business")) {
      warn("Account is not a business account");
      return null;
    }

    if (role.equals("manager")) {
      ((BusinessAccount) account).addManager(user);
    } else if (role.equals("employee")) {
      ((BusinessAccount) account).addEmployee(user);
    } else {
      warn("Invalid role");
      return null;
    }

    return null;
  }
}
