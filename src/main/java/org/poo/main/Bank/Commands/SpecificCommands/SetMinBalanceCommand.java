package org.poo.main.Bank.Commands.SpecificCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account.Account;
import org.poo.main.Bank.Bank;
import org.poo.main.Bank.Commands.Command;

public class SetMinBalanceCommand implements Command {
  private final Bank bank = Bank.getInstance(null);

  /**
   * Executes the `SetMinBalanceCommand` which updates the minimum balance
   * of an account identified by the provided IBAN.
   */
  @Override
  public ObjectNode execute(final CommandInput input) {
    String iban = input.getAccount();
    Account account = bank.getAccount(iban);
    double minBalance = input.getMinBalance();

    account.setMinBalance(minBalance);

    return null;
  }
}
