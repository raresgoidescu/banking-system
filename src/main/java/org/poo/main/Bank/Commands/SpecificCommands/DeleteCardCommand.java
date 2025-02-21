package org.poo.main.Bank.Commands.SpecificCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account.Card.Card;
import org.poo.main.Bank.Bank;
import org.poo.main.Bank.Commands.Command;

import static org.poo.main.Utils.Utils.info;
import static org.poo.main.Utils.Utils.warn;

public class DeleteCardCommand implements Command {
  private final Bank bank = Bank.getInstance(null);

  /**
   * Executes the delete card command. This method identifies the card associated with the
   * given card number, deletes it from the corresponding account, and removes it from the
   * bank's registry. If no card matching the provided card number is found, an error message
   * is logged.
   */
  @Override
  public ObjectNode execute(final CommandInput input) {
    String cardNumber = input.getCardNumber();
    Card card = bank.getCard(cardNumber);

    if (card == null) {
      warn("No card found based on the cardNumber provided.");
      return null;
    }

    info("%s\n\tDeleting card %s\n", card.getAccount(), card);

    // delete itself :)
    card.getAccount().deleteCard(card, input.getTimestamp(), false);

    bank.removeCard(card);

    return null;
  }
}
