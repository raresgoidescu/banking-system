package org.poo.main.Bank.Commands.SpecificCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account.Account;
import org.poo.main.Bank.Bank;
import org.poo.main.Bank.Commands.Command;
import org.poo.main.Bank.MonetaryValue;
import org.poo.main.Bank.Transaction.SavingsWithdrawalTransaction;
import org.poo.main.Bank.Transaction.SimpleTransaction;
import org.poo.main.Entities.User;

import static org.poo.main.Utils.Utils.info;
import static org.poo.main.Utils.Utils.warn;

public class WithdrawSavingsCommand implements Command {
  private static final int MINIMUM_AGE = 21;

  private final Bank bank = Bank.getInstance(null);

  /**
   * Executes the command to withdraw funds from a savings account and deposit them into a classic
   * account. If the account is not of type savings, an appropriate output is generated.
   */
  @Override
  public final ObjectNode execute(final CommandInput input) {
    Account account = bank.getAccount(input.getAccount());

    if (account == null) {
      warn("Account not found");
      return null;
    }

    info("Withdrawing %s %s from account %s",
            input.getAmount(), input.getCurrency(), account);

    User owner = account.getOwner();
    Account targetAccount = owner.getAccountByTypeAndCurrency("classic", input.getCurrency());

    if (targetAccount == null) {
      SimpleTransaction errTransaction = new SimpleTransaction.Builder().
              description("You do not have a classic account.")
              .timestamp(input.getTimestamp())
              .build();

      warn("You do not have a classic account.");
      account.addTransaction(errTransaction);

      return null;
    }

    SavingsWithdrawalTransaction transaction = new SavingsWithdrawalTransaction.Builder()
            .description("Savings withdrawal")
            .amount(input.getAmount())
            .savingsAccountIBAN(account.getIban())
            .classicAccountIBAN(targetAccount.getIban())
            .timestamp(input.getTimestamp()).build();

    if (owner.getAge() < MINIMUM_AGE) {
      SimpleTransaction errTransaction = new SimpleTransaction.Builder().
              description("You don't have the minimum age required.").
              timestamp(input.getTimestamp()).
              build();

      warn("You don't have the minimum age required.");
      account.getTransactions().add(errTransaction);

      return null;
    }

    if (!account.getType().equals("savings")) {
      SimpleTransaction errTransaction = new SimpleTransaction.Builder().
              description("Account is not of type savings.")
              .timestamp(input.getTimestamp())
              .build();

      warn("Account is not of type savings.");
      account.addTransaction(errTransaction);

      return null;
    }

    MonetaryValue convertedMonetaryValue =
            new MonetaryValue(input.getAmount(), input.getCurrency());

    double withdrawnAmount;

    try {
      withdrawnAmount = account.withdraw(convertedMonetaryValue);
    } catch (Exception e) {
      transaction.setDescription(e.getMessage());
      warn(e.getMessage());
      account.getTransactions().add(transaction);
      return null;
    }

    MonetaryValue targetMonetaryValue = new MonetaryValue(withdrawnAmount, input.getCurrency());

    targetAccount.addFunds(targetMonetaryValue);

    account.addTransaction(transaction);
    targetAccount.addTransaction(transaction);

    return null;
  }
}
