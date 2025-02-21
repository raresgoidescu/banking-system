package org.poo.main.Bank.Commands.SpecificCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account.Account;
import org.poo.main.Bank.Bank;
import org.poo.main.Bank.Commands.Command;
import org.poo.main.Bank.OutputHelperClasses.ResponseOutput;
import org.poo.main.Utils.Utils;

import static org.poo.main.Utils.Utils.warn;

public class SendMoneyCommand implements Command {
  private final Bank bank = Bank.getInstance(null);

  /**
   * Executes the sendMoney command. Transfers the specified amount from the sender's account
   * to the receiver's account if the sender and receiver accounts are valid and sufficient funds
   * are available.
   */
  @Override
  public ObjectNode execute(final CommandInput input) {
    String sender = input.getAccount();
    double amount = input.getAmount();
    String receiver = input.getReceiver();
    int timestamp = input.getTimestamp();
    String description = input.getDescription();

    ObjectNode commandOutput = Utils.prepareCommandOutput(input);

    if (receiver.isEmpty() || sender.isEmpty()) {
      ResponseOutput response =
              new ResponseOutput().setDescription("User not found").setTimestamp(timestamp);
      commandOutput.set("output", MAPPER.valueToTree(response));
      return commandOutput;
    }

    if (bank.getAliases().contains(sender)) {
      warn("Trying to send from alias!");
      return null;
    }

    Account senderAccount = bank.getAccount(sender);
    Account receiverAccount = bank.getAccount(receiver);

    if (senderAccount == null || receiverAccount == null) {
      warn("Account not found");
      return null;
    }

    senderAccount.transfer(amount, receiverAccount, timestamp, description);

    return null;
  }
}
