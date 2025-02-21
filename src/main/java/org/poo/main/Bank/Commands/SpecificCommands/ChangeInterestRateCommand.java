package org.poo.main.Bank.Commands.SpecificCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account.Account;
import org.poo.main.Account.SavingsAccount;
import org.poo.main.Bank.Bank;
import org.poo.main.Bank.Commands.Command;

public class ChangeInterestRateCommand implements Command {
  private final Bank bank = Bank.getInstance(null);

  /**
   * Executes a command to change the interest rate of a savings account. If the specified
   * account is not a savings account, an appropriate output is generated.
   */
  @Override
  public ObjectNode execute(final CommandInput input) {
    Account account = bank.getAccount(input.getAccount());

    try {
      ((SavingsAccount) account).changeInterest(
              input.getInterestRate(),
              input.getTimestamp()
      );
    } catch (Exception e) {
      bank.notSavingsAccountOutput(input);
    }
    return null;
  }
}
