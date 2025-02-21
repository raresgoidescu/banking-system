package org.poo.main.Bank.Commands.SpecificCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account.Account;
import org.poo.main.Bank.Bank;
import org.poo.main.Bank.Commands.Command;
import org.poo.main.Bank.MonetaryValue;
import org.poo.main.Bank.Payments.SimplePayment;
import org.poo.main.Bank.Payments.SplitPayment;
import org.poo.main.Bank.Transaction.SplitPaymentTransaction;
import org.poo.main.Entities.User;

import java.util.ArrayList;
import java.util.List;

import static org.poo.main.Utils.Utils.info;

public class SplitPaymentCommand implements Command {
  private final Bank bank = Bank.getInstance(null);

  /**
   * Executes the split payment command by dividing a specified amount among
   * a list of accounts and updating their balances accordingly.
   * If any account has insufficient funds for the payment,
   * an error is recorded in the transaction.
   */
  @Override
  public ObjectNode execute(final CommandInput input) {
    String type = input.getSplitPaymentType();
    List<String> ibans = input.getAccounts();
    List<Double> amounts = input.getAmountForUsers();
    double totalAmount = input.getAmount();

    info("New split payment of type %s", input.getSplitPaymentType());

    SplitPaymentTransaction transaction = new SplitPaymentTransaction.Builder()
            .description(String.format("Split payment of %.2f %s",
                    totalAmount, input.getCurrency()))
            .currency(input.getCurrency())
            .timestamp(input.getTimestamp())
            .splitPaymentType(input.getSplitPaymentType())
            .involvedAccounts(input.getAccounts())
            .build();

    int numberOfAccounts = ibans.size();

    if (type.equals("equal")) {
      amounts = new ArrayList<>();
      for (int i = 0; i < numberOfAccounts; i++) {
        amounts.add(totalAmount / numberOfAccounts);
      }

      transaction.setAmount(totalAmount / numberOfAccounts);
    } else {
      transaction.setAmountForUsers(amounts);
    }

    List<Account> accounts = ibans.stream().map(bank::getAccount).toList();

    SplitPayment splitPayment = new SplitPayment(
            input.getSplitPaymentType(), accounts, totalAmount,
            amounts, new ArrayList<>(), input.getCurrency(), transaction
    );

    for (int i = 0; i < numberOfAccounts; i++) {
      Account account = bank.getAccount(ibans.get(i));
      User owner = account.getOwner();

      info(account + " paricipates to split payment\n"
              + "  and needs to pay " + amounts.get(i) + " " + input.getCurrency());

      double amountToPay = amounts.get(i);

      info("Account %s needs to pay %.2f %s",
              account.getIban(), amountToPay, input.getCurrency());

      MonetaryValue convertedMonetaryValue =
              new MonetaryValue(amountToPay, input.getCurrency());

      MonetaryValue unconvertedMonetaryValue =
              new MonetaryValue(amountToPay, input.getCurrency())
                      .convertTo(account.getCurrency());

      SimplePayment possiblePayment = new SimplePayment(account.getIban(), null,
              unconvertedMonetaryValue.getAmount(), unconvertedMonetaryValue.getCurrency(),
              convertedMonetaryValue.getAmount(), convertedMonetaryValue.getCurrency());

      possiblePayment.setParentPayment(splitPayment);
      splitPayment.addPayment(possiblePayment);
      owner.getSplitPaymentsQueue().add(possiblePayment);
    }

    return null;
  }
}
