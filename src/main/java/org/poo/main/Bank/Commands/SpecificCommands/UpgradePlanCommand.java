package org.poo.main.Bank.Commands.SpecificCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account.Account;
import org.poo.main.Bank.Bank;
import org.poo.main.Bank.Commands.Command;
import org.poo.main.Bank.OutputHelperClasses.ResponseOutput;
import org.poo.main.Bank.Transaction.SimpleTransaction;
import org.poo.main.Bank.Transaction.UpgradePlanTransaction;
import org.poo.main.Utils.Utils;

import static org.poo.main.Utils.Utils.info;
import static org.poo.main.Utils.Utils.warn;

public final class UpgradePlanCommand implements Command {
  private final Bank bank = Bank.getInstance(null);

  /**
   * Executes the command to upgrade the plan of a specific account.
   * If the account is not found, an appropriate output is generated.
   * If the account already has the new plan, a transaction is added to the account.
   */
  @Override
  public ObjectNode execute(final CommandInput input) {
    Account account = bank.getAccount(input.getAccount());

    info("Upgrade plan for account\n\t%s to %s",
            account, input.getNewPlanType());

    ObjectNode commandMapper = Utils.prepareCommandOutput(input);

    if (account == null) {
      warn("Account not found");

      commandMapper.set("output", MAPPER.valueToTree(new ResponseOutput()
              .setDescription("Account not found")
              .setTimestamp(input.getTimestamp())
      ));

      return commandMapper;
    }

    info("Want to upgrade from " + account.getPlanStrategy() + " to " + input.getNewPlanType());

    String newPlan = input.getNewPlanType();
    String oldPlan = account.getOwner().getPlanStrategy().toString();

    if (newPlan.equals(oldPlan)) {
      warn("Account already has this plan");

      SimpleTransaction transaction = new SimpleTransaction.Builder()
              .timestamp(input.getTimestamp())
              .description("The user already has the " + newPlan + " plan.")
              .build();

      account.addTransaction(transaction);

      return null;
    }

    try {
      account.tryPlanUpgrade(
              input.getNewPlanType(),
              input.getTimestamp()
      );
    } catch (Exception e) {
      warn(e.getMessage());

      return null;
    }

    info("%s (%s -> %s)\n",
            account, oldPlan, account.getOwner().getPlanStrategy().toString());

    UpgradePlanTransaction transaction = new UpgradePlanTransaction.Builder()
            .timestamp(input.getTimestamp())
            .description("Upgrade plan")
            .newPlanType(input.getNewPlanType())
            .accountIban(account.getIban())
            .build();
    account.addTransaction(transaction);

    return null;
  }
}
