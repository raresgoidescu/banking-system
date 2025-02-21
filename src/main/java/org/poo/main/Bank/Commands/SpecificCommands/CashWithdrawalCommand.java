package org.poo.main.Bank.Commands.SpecificCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account.Account;
import org.poo.main.Account.Card.Card;
import org.poo.main.Bank.Bank;
import org.poo.main.Bank.Commands.Command;
import org.poo.main.Bank.ExchangeGraph;
import org.poo.main.Bank.OutputHelperClasses.ResponseOutput;
import org.poo.main.Entities.User;
import org.poo.main.Utils.Utils;

import static org.poo.main.Utils.Utils.info;
import static org.poo.main.Utils.Utils.warn;

public final class CashWithdrawalCommand implements Command {
  private final Bank bank = Bank.getInstance(null);

  /**
   * Withdraws money from a card (Always in RON)
   */
  @Override
  public ObjectNode execute(final CommandInput input) {
    User user = bank.getUser(input.getEmail());

    ObjectNode commandOutput = Utils.prepareCommandOutput(input);
    ResponseOutput response = new ResponseOutput();

    if (user == null) {
      response.setTimestamp(input.getTimestamp()).setDescription("User not found");
      commandOutput.set("output", MAPPER.valueToTree(response));
      return commandOutput;
    }

    info(user + " in need of " + input.getAmount() + " " + "RON");

    Card card = bank.getCard(input.getCardNumber());

    if (card == null) {
      response.setDescription("Card not found").setTimestamp(input.getTimestamp());
      commandOutput.set("output", MAPPER.valueToTree(response));
      return commandOutput;
    }

    Account account = card.getAccount();

    if (!account.checkAccess(user)) {
      response.setDescription("Card not found").setTimestamp(input.getTimestamp());
      commandOutput.set("output", MAPPER.valueToTree(response));
      return commandOutput;
    }

    Double rate = ExchangeGraph.getInstance().getRate("RON", account.getCurrency());
    assert rate != null;

    try {
      card.cashWithdraw(input.getAmount(), rate, input.getTimestamp());
    } catch (Exception e) {
      warn(e.getMessage());
    }

    return null;
  }
}
