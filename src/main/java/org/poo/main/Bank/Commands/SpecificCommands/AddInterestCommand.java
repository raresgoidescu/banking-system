package org.poo.main.Bank.Commands.SpecificCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account.Account;
import org.poo.main.Account.SavingsAccount;
import org.poo.main.Bank.Bank;
import org.poo.main.Bank.Commands.Command;

import static org.poo.main.Utils.Utils.info;
import static org.poo.main.Utils.Utils.warn;

public class AddInterestCommand implements Command {
  private final Bank bank = Bank.getInstance(null);

  /**
   * Executes the command to add interest to a savings account. If the account
   * specified in the input is not a savings account, an appropriate output is logged.
   */
  @Override
  public ObjectNode execute(final CommandInput input) {
    Account account = bank.getAccount(input.getAccount());

    try {
      ((SavingsAccount) account).addInterest(input.getTimestamp());
      info(String.format("%s", account));
    } catch (Exception e) {
      bank.notSavingsAccountOutput(input);
      warn("Account is not a savings account.");
    }
    return null;
  }
}
