package org.poo.main.Account.Card;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.poo.main.Account.Account;
import org.poo.main.Bank.MonetaryValue;
import org.poo.main.Bank.Transaction.CashWithdrawalTransaction;
import org.poo.main.Bank.Transaction.OnlinePaymentTransaction;
import org.poo.main.Bank.Transaction.SimpleTransaction;
import org.poo.main.Bank.Transaction.Transaction;
import org.poo.main.Bank.Transaction.UpgradePlanTransaction;
import org.poo.main.Entities.Merchant;
import org.poo.main.Entities.User;
import org.poo.utils.Utils;

@Getter
@Setter
public class Card {
  @JsonIgnore
  private Account account;
  @JsonIgnore
  private User owner;

  private String cardNumber;
  private String status;

  public Card(final Account account, final User owner) {
    this.account = account;
    this.owner = owner;
    this.cardNumber = Utils.generateCardNumber();
    this.status = "active";
  }

  /**
   * Checks if the account balance is less than or equal to the minimum balance.
   * If the balance is below the specified threshold, the card status is set to "frozen",
   * and a transaction is added to the account's transaction history with a message
   * indicating the card has been frozen due to low funds.
   */
  public final void check(final int timestamp) {
    if (this.account.getBalance() <= this.account.getMinBalance()) {
      this.status = "frozen";

      SimpleTransaction transaction = new SimpleTransaction.Builder()
              .description("You have reached the minimum amount "
                      + "of funds, the card will be frozen")
              .timestamp(timestamp)
              .build();

      this.getAccount().getTransactions().add(transaction);
    }
  }

  /**
   * Cash withdrawal operation.
   *
   * @param amount    The amount to be withdrawn.
   * @param rate      The exchange rate.
   * @param timestamp The timestamp of the transaction.
   * @return True if the operation was successful, false otherwise.
   * @throws Exception If the card is frozen or if the account balance is insufficient.
   */
  public final boolean cashWithdraw(final double amount, final double rate,
                                    final int timestamp) throws Exception {
    String description = String.format("Cash withdrawal of %.1f", amount);

    Transaction transaction;

    if (status.equals("frozen")) {
      throw new Exception("The card is frozen");
    }

    MonetaryValue monetaryValue = new MonetaryValue(amount, account.getCurrency());

    double fee = account.getPlanStrategy().calculateFee(monetaryValue);

    if ((amount + fee) * rate > this.account.getBalance()) {
      transaction = new SimpleTransaction.Builder()
              .timestamp(timestamp).description("Insufficient funds").build();
      this.getAccount().getTransactions().add(transaction);
      throw new Exception("Insufficient funds");
    }

    double newBalance = this.account.getBalance() - (amount + fee) * rate;
    account.setBalance(newBalance);

    transaction = new CashWithdrawalTransaction.Builder()
            .description(description).timestamp(timestamp).amount(amount).build();

    this.getAccount().getTransactions().add(transaction);

    return true;
  }

  /**
   * Online payment operation.
   *
   * @param user        The user performing the transaction.
   * @param amount      The amount to be paid.
   * @param currency    The currency of the transaction.
   * @param merchant    The merchant receiving the payment.
   * @param description The description of the transaction.
   * @param timestamp   The timestamp of the transaction.
   * @return The card object.
   * @throws Exception If the card is frozen, if the account balance is insufficient,
   */
  public Card onlinePayment(final User user,
                            final double amount, final String currency,
                            final Merchant merchant,
                            final String description, final int timestamp) throws Exception {
    if (status.equals("frozen")) {
      throw new Exception("The card is frozen");
    }

    MonetaryValue convertedMonetaryValue = new MonetaryValue(amount, currency);
    MonetaryValue unconvertedMonetaryValue = convertedMonetaryValue
            .convertTo(this.account.getCurrency());

    double fee = account.getPlanStrategy().calculateFee(unconvertedMonetaryValue);

    unconvertedMonetaryValue.setAmount(unconvertedMonetaryValue.getAmount() + fee);

    if (!this.account.hasEnoughFunds(unconvertedMonetaryValue)) {
      throw new Exception("Insufficient funds");
    }

    this.account.withdraw(user, unconvertedMonetaryValue);

    unconvertedMonetaryValue.setAmount(unconvertedMonetaryValue.getAmount() - fee);
    double cashback = this.account.getCashback(unconvertedMonetaryValue, merchant);
    this.account.addFunds(new MonetaryValue(cashback, unconvertedMonetaryValue.getCurrency()));

    OnlinePaymentTransaction transaction = new OnlinePaymentTransaction.Builder()
            .amount(unconvertedMonetaryValue.getBeforeFeeOrCashback())
            .merchant(merchant.getName())
            .description(description.equals("Online payment") ? "Card payment" : description)
            .timestamp(timestamp)
            .user(user)
            .build();

    this.getAccount().addTransaction(transaction);

    if (this.account.getOwner().getPlanStrategy().tryUpgrade(
            this.account.getOwner(), unconvertedMonetaryValue)
    ) {
      UpgradePlanTransaction planTransaction = new UpgradePlanTransaction.Builder()
              .timestamp(timestamp)
              .description("Upgrade plan")
              .newPlanType(this.account.getOwner().getPlanStrategy().toString())
              .accountIban(account.getIban())
              .build();

      this.account.addTransaction(planTransaction);
    }

    return this;
  }

  /**
   * Checks if the user has access to the card.
   *
   * @param user The user to check.
   * @return True if the user has access to the card, false otherwise.
   */
  public boolean checkAccess(final User user) {
    return account.checkAccess(user);
  }

  /**
   * Returns the card number and status.
   *
   * @return The card number and status.
   */
  @Override
  public String toString() {
    return String.format("%s %s", this.getCardNumber(), this.getStatus());
  }
}
