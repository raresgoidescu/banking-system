package org.poo.main.Bank.Commands.SpecificCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Bank.Bank;
import org.poo.main.Bank.Commands.Command;
import org.poo.main.Bank.OutputHelperClasses.ResponseOutput;
import org.poo.main.Bank.Payments.SimplePayment;
import org.poo.main.Bank.Payments.SplitPayment;
import org.poo.main.Entities.User;
import org.poo.main.Utils.Utils;

import java.util.Objects;

import static org.poo.main.Utils.Utils.info;
import static org.poo.main.Utils.Utils.success;
import static org.poo.main.Utils.Utils.warn;

public final class AcceptSplitPaymentCommand implements Command {
  private final Bank bank = Bank.getInstance(null);

  /**
   * Executes the specified command to accept a split payment for the user associated with the input
   * If the user is not found, an error is logged, and the method returns null.
   * In the case of successful split payment acceptance, the payment is accepted.
   */
  @Override
  public ObjectNode execute(final CommandInput input) {
    User user = bank.getUser(input.getEmail());

    info("User " + user + " accepts split payment ");

    ObjectNode commandOutput = Utils.prepareCommandOutput(input);

    if (user == null) {
      warn("User not found");

      commandOutput.set("output", MAPPER.valueToTree(new ResponseOutput()
              .setDescription("User not found")
              .setTimestamp(input.getTimestamp())
      ));

      return commandOutput;
    }

    String splitPaymentType = input.getSplitPaymentType();

    try {
      SimplePayment payment = user.getSplitPaymentsQueue().poll();

      Objects.requireNonNull(payment);

      if (!((SplitPayment) payment.getParentPayment()).getType().equals(splitPaymentType)) {
        return null;
      }

      ((SplitPayment) payment.getParentPayment()).accept(payment);
      success("User %s accepted split payment.", user);
    } catch (Exception e) {
      warn("User %s could not accept split payment.", user);
      warn(e.getMessage());
    }

    return null;
  }
}
