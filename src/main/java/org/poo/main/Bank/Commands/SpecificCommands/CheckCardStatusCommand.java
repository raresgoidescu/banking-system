package org.poo.main.Bank.Commands.SpecificCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account.Card.Card;
import org.poo.main.Bank.Bank;
import org.poo.main.Bank.Commands.Command;
import org.poo.main.Bank.OutputHelperClasses.ResponseOutput;
import org.poo.main.Utils.Utils;

import static org.poo.main.Utils.Utils.info;

public class CheckCardStatusCommand implements Command {
  private final Bank bank = Bank.getInstance(null);

  /**
   * Executes the command to check the status of a card based on provided input.
   * If the card is not found in the bank, a response containing an error message
   * is generated and returned.
   * Otherwise, the card's status is validated based on the current timestamp.
   */
  @Override
  public ObjectNode execute(final CommandInput input) {
    String cardNumber = input.getCardNumber();
    Card card = bank.getCard(cardNumber);
    int timestamp = input.getTimestamp();


    /* Only case when this command generates output is when Card is not found */
    if (card == null) {
      ObjectNode commandOutput = Utils.prepareCommandOutput(input);
      ResponseOutput responseOutput = new ResponseOutput()
              .setDescription("Card not found")
              .setTimestamp(timestamp);

      commandOutput.set("output", MAPPER.valueToTree(responseOutput));

      return commandOutput;
    }

    info("Before Check: %s", card);

    card.check(timestamp);

    info("After Check %s", card);

    return null;
  }
}
