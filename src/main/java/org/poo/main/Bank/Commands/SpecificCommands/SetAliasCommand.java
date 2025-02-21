package org.poo.main.Bank.Commands.SpecificCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Bank.Bank;
import org.poo.main.Bank.Commands.Command;


public class SetAliasCommand implements Command {
  private final Bank bank = Bank.getInstance(null);

  /**
   * Executes the SetAliasCommand by associating an alias to an account within the bank.
   * Adds the alias to the list of aliases and maps it to the
   * specified account in the bank's system.
   */
  @Override
  public ObjectNode execute(final CommandInput input) {
    // I don't get it why would I do this, but sure...
    // In my opinion, every user should have had a List<String> aliases.
    bank.getAliases().add(input.getAlias());

    bank.addAccount(input.getAlias(), bank.getAccount(input.getAccount()));

    return null;
  }
}
