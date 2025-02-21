package org.poo.main.Bank.Commands.SpecificCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account.Account;
import org.poo.main.Account.BusinessAccount;
import org.poo.main.Bank.Bank;
import org.poo.main.Bank.Commands.Command;
import org.poo.main.Bank.OutputHelperClasses.ResponseOutput;
import org.poo.main.Entities.User;

import static org.poo.main.Utils.Utils.prepareCommandOutput;
import static org.poo.main.Utils.Utils.warn;

public final class ChangeDepositLimitCommand implements Command {
  private Bank bank = Bank.getInstance(null);

  /**
   * Changes the deposit limit of a business account.
   */
  @Override
  public ObjectNode execute(final CommandInput input) {
    Account account = bank.getAccount(input.getAccount());
    User user = bank.getUser(input.getEmail());

    try {
      ((BusinessAccount) account).setDepositLimit(user, input.getAmount());
    } catch (Exception e) {
      warn(e.getMessage());

      ObjectNode commandOutput = prepareCommandOutput(input);

      ResponseOutput responseOutput = new ResponseOutput()
              .setDescription(e.getMessage())
              .setTimestamp(input.getTimestamp());

      commandOutput.set("output", MAPPER.valueToTree(responseOutput));

      return commandOutput;
    }

    return null;
  }
}
