package org.poo.main.Bank.Commands.SpecificCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Bank.Bank;
import org.poo.main.Bank.Commands.Command;
import org.poo.main.Utils.Utils;

public class PrintUsersCommand implements Command {
  private final Bank bank = Bank.getInstance(null);

  /**
   * Executes the PrintUsersCommand by generating a JSON object that includes
   * the command information, a list of all users in the bank, and a timestamp.
   */
  @Override
  public ObjectNode execute(final CommandInput input) {
    ObjectNode commandMapper = Utils.prepareCommandOutput(input);

    commandMapper.set("output", MAPPER.valueToTree(bank.getUsers()));

    return commandMapper;
  }
}
