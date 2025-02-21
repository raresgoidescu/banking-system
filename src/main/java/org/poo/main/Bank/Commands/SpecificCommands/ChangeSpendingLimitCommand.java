package org.poo.main.Bank.Commands.SpecificCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account.Account;
import org.poo.main.Account.BusinessAccount;
import org.poo.main.Bank.Bank;
import org.poo.main.Bank.Commands.Command;
import org.poo.main.Bank.OutputHelperClasses.ResponseOutput;
import org.poo.main.Entities.User;
import org.poo.main.Utils.Utils;

import static org.poo.main.Utils.Utils.info;
import static org.poo.main.Utils.Utils.warn;

public final class ChangeSpendingLimitCommand implements Command {
  private final Bank bank = Bank.getInstance(null);

  /**
   * Changes the spending limit of a business account.
   */
  @Override
  public ObjectNode execute(final CommandInput input) {
    Account account = bank.getAccount(input.getAccount());
    User user = bank.getUser(input.getEmail());

    info("%s tries to change spending limit for account\n\t%s to %.2f %s",
            user.getEmail(), account, input.getAmount(), account.getCurrency());

    try {
      ((BusinessAccount) account).setSpendingLimit(user, input.getAmount());
    } catch (Exception e) {
      String errorMessage = e.getMessage();
      warn(e.getMessage());

      if (!account.getType().equals("business")) {
        errorMessage = "This is not a business account";
      }

      ObjectNode commandOutput = Utils.prepareCommandOutput(input);

      ResponseOutput responseOutput = new ResponseOutput()
              .setDescription(errorMessage)
              .setTimestamp(input.getTimestamp());

      commandOutput.set("output", MAPPER.valueToTree(responseOutput));

      return commandOutput;
    }

    return null;
  }
}
