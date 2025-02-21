package org.poo.main.Bank.Commands.SpecificCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account.Card.Card;
import org.poo.main.Bank.Bank;
import org.poo.main.Bank.Commands.Command;
import org.poo.main.Bank.OutputHelperClasses.ResponseOutput;
import org.poo.main.Bank.Transaction.SimpleTransaction;
import org.poo.main.Entities.User;
import org.poo.main.Utils.Utils;

import static org.poo.main.Utils.Utils.info;
import static org.poo.main.Utils.Utils.warn;

public class PayOnlineCommand implements Command {
  private final Bank bank = Bank.getInstance(null);

  /**
   * Executes the command to perform an online payment operation. This method processes
   * the payment using the provided card, validates if the user owns the card, handles
   * currency exchange rates, and updates the card or user account details in the bank system.
   * If the card is not found, a response is added to the result indicating the issue.
   */
  @Override
  public ObjectNode execute(final CommandInput input) {
    if (input.getAmount() <= 0) {
      return null;
    }

    int timestamp = input.getTimestamp();
    String description = input.getDescription();
    String merchant = input.getCommerciant();

    Card card = bank.getCard(input.getCardNumber());
    User user = bank.getUser(input.getEmail());

    ObjectNode commandMapper = Utils.prepareCommandOutput(input);

    info("Online payment requested by %s, in need of %.2f %s",
            user, input.getAmount(), input.getCurrency());

    if (card == null) {
      ResponseOutput responseOutput = new ResponseOutput()
              .setDescription("Card not found")
              .setTimestamp(timestamp);

      commandMapper.set("output", MAPPER.valueToTree(responseOutput));

      return commandMapper;
    }

    if (card.checkAccess(user)) {
      try {
        info("Performing online payment with card %s", card.getCardNumber());
        info("Account: %s", card.getAccount());

        Card newCard = card.onlinePayment(user, input.getAmount(), input.getCurrency(),
                bank.getMerchant(merchant), description, timestamp);

        if (!newCard.equals(card)) {
          bank.removeCard(card);
          bank.addCard(newCard);
          info("New card: %s", newCard.getCardNumber());
        }
      } catch (Exception e) {
        warn("Failed to perform online payment: %s", e.getMessage());

        SimpleTransaction transaction = new SimpleTransaction.Builder()
                .description(e.getMessage()).timestamp(timestamp).build();

        card.getAccount().addTransaction(transaction);
      }
    } else {
      warn(String.format("User %s does not own the card\n", user));

      ResponseOutput responseOutput = new ResponseOutput()
              .setDescription("Card not found")
              .setTimestamp(timestamp);

      commandMapper.set("output", MAPPER.valueToTree(responseOutput));

      return commandMapper;
    }

    return null;
  }
}
