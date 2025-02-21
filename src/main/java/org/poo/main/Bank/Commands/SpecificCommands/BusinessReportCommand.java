package org.poo.main.Bank.Commands.SpecificCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account.Account;
import org.poo.main.Account.BusinessAccount;
import org.poo.main.Bank.Bank;
import org.poo.main.Bank.Commands.Command;
import org.poo.main.Utils.Utils;

import static org.poo.main.Utils.Utils.info;
import static org.poo.main.Utils.Utils.warn;

public class BusinessReportCommand implements Command {
  private final Bank bank = Bank.getInstance(null);

  /**
   * Executes the command to generate a business report for a business account.
   * If the account is not found, an error is logged, and the method returns null.
   */
  @Override
  public final ObjectNode execute(final CommandInput input) {
    String type = input.getType();
    int startTimestamp = input.getStartTimestamp();
    int endTimestamp = input.getEndTimestamp();
    Account account = bank.getAccount(input.getAccount());

    if (account == null) {
      warn("Account not found");
      return null;
    }

    info("Requesting business report (" + type + ") for account "
            + account + " from " + startTimestamp + " to " + endTimestamp);

    try {
      ObjectNode commandOutput = Utils.prepareCommandOutput(input);
      commandOutput.set("output", ((BusinessAccount) account)
              .generateReport(startTimestamp, endTimestamp, type));

      return commandOutput;
    } catch (Exception e) {
      warn(e.getMessage());
      return null;
    }
  }
}
