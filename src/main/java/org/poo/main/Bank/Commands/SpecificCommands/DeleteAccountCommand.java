package org.poo.main.Bank.Commands.SpecificCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account.Account;
import org.poo.main.Account.Card.Card;
import org.poo.main.Bank.Bank;
import org.poo.main.Bank.Commands.Command;
import org.poo.main.Bank.OutputHelperClasses.ResponseOutput;
import org.poo.main.Entities.User;
import org.poo.main.Utils.Utils;

import java.util.List;

import static org.poo.main.Utils.Utils.warn;

public class DeleteAccountCommand implements Command {
  private final Bank bank = Bank.getInstance(null);

  /**
   * Executes the delete account command. This method handles the logic for deleting
   * an account associated with a user, including removing all linked cards and
   * generating a response indicating success or failure.
   */
  @Override
  public ObjectNode execute(final CommandInput input) {
    Account account = bank.getAccount(input.getAccount());
    User user = bank.getUser(input.getEmail());
    int timestamp = input.getTimestamp();

    ObjectNode commandMapper = Utils.prepareCommandOutput(input);
    ResponseOutput responseOutput = new ResponseOutput();

    try {
      List<Card> cardsToBeDeleted = account.getCards();

      user.deleteAccount(account, timestamp);
      cardsToBeDeleted.forEach(bank::removeCard);
      bank.removeAccount(account);

      responseOutput.setSuccess("Account deleted").setTimestamp(timestamp);
    } catch (Exception e) {
      responseOutput.setError(e.getMessage()).setTimestamp(timestamp);
      warn(account + " still got money!");
    }

    commandMapper.set("output", MAPPER.valueToTree(responseOutput));

    return commandMapper;
  }
}
