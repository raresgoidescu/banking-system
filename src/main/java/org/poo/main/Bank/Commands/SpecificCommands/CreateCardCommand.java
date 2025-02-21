package org.poo.main.Bank.Commands.SpecificCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account.Account;
import org.poo.main.Account.Card.Card;
import org.poo.main.Bank.Bank;
import org.poo.main.Bank.Commands.Command;
import org.poo.main.Entities.User;

import static org.poo.main.Utils.Utils.success;
import static org.poo.main.Utils.Utils.warn;

public class CreateCardCommand implements Command {
  private final Bank bank = Bank.getInstance(null);

  /**
   * Executes the specified command to create a new card for the user associated with the input.
   * If the user is not found, an error is logged, and the method returns null.
   * In the case of successful card creation, the card is added to the bank's records.
   */
  @Override
  public ObjectNode execute(final CommandInput input) {
    Account account = bank.getAccount(input.getAccount());
    User user = bank.getUser(input.getEmail());

    if (user == null) {
      warn("User not found");
      return null;
    }

    try {
      Card card = user.addCard(account, input.getCommand(), input.getTimestamp());

      bank.addCard(card);
      success(user.getEmail() + " created new card: " + card + " for account:\n\t" + account);
    } catch (Exception e) {
      warn("Failed to create card: " + e.getMessage());
    }

    return null;
  }
}
