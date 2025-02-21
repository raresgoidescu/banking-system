package org.poo.main.Account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.main.Account.Card.Card;
import org.poo.main.Account.Plans.AccountPlanStrategy;
import org.poo.main.Account.Plans.GoldPlanStrategy;
import org.poo.main.Account.Plans.SilverPlanStrategy;
import org.poo.main.Bank.MonetaryValue;
import org.poo.main.Bank.Transaction.DeleteCardTransaction;
import org.poo.main.Bank.Transaction.SendMoneyTransaction;
import org.poo.main.Bank.Transaction.SimpleTransaction;
import org.poo.main.Bank.Transaction.Transaction;
import org.poo.main.Entities.Merchant;
import org.poo.main.Entities.MerchantType;
import org.poo.main.Entities.User;
import org.poo.utils.Utils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class Account {
  @JsonProperty("IBAN")
  private String iban;

  @JsonIgnore
  private User owner;

  private double balance;
  private String currency;
  private String type;

  @JsonIgnore
  private double minBalance;

  private List<Card> cards = new ArrayList<>();

  @JsonIgnore
  private List<Transaction> transactions = new ArrayList<>();

  @JsonIgnore
  private double stTotalRON = 0;
  @JsonIgnore
  private Set<MerchantType> ntUsedCashbacks = EnumSet.noneOf(MerchantType.class);
  @JsonIgnore
  private Map<Merchant, Integer> ntMerchantTransactions = new HashMap<>();

  public Account() {
  }

  public Account(final User owner, final String currency, final String type) {
    this.iban = Utils.generateIBAN();
    this.owner = owner;
    this.currency = currency;
    this.type = type;
  }

  /**
   * Withdraw method for the account
   *
   * @param monetaryValue the amount to withdraw
   * @return the amount withdrawn
   * @throws Exception if the user doesn't have enough funds
   */
  public double withdraw(final MonetaryValue monetaryValue) throws Exception {
    MonetaryValue convertedMonetaryValue = monetaryValue.convertTo(this.currency);

    if (!hasEnoughFunds(convertedMonetaryValue)) {
      throw new Exception("Insufficient funds");
    }

    this.subtractFunds(convertedMonetaryValue);

    return convertedMonetaryValue.getAmount();
  }

  /**
   * Withdraw method for the user
   *
   * @param user          the user that wants to withdraw
   * @param monetaryValue the amount to withdraw
   * @return the amount withdrawn
   * @throws Exception if the user doesn't have enough funds
   */
  public double withdraw(final User user, final MonetaryValue monetaryValue) throws Exception {
    return this.withdraw(monetaryValue);
  }

  /**
   * Deposit method for the account
   *
   * @param monetaryValue the amount to deposit
   * @throws Exception if the currency is null
   */
  public void deposit(final MonetaryValue monetaryValue) throws Exception {
    if (currency == null) {
      return;
    }

    MonetaryValue convertedMonetaryValue = monetaryValue.convertTo(this.currency);

    this.addFunds(convertedMonetaryValue);
  }

  /**
   * Deposit method for the user
   *
   * @param user          the user that wants to deposit
   * @param monetaryValue the amount to deposit
   * @throws Exception if the currency is null
   */
  public void deposit(final User user, final MonetaryValue monetaryValue) throws Exception {
    this.deposit(monetaryValue);
  }

  /**
   * Transfer method for the account
   *
   * @param amount      the amount to transfer
   * @param that        the account to transfer to
   * @param timestamp   the timestamp of the transfer
   * @param description the description of the transfer
   */
  public final void transfer(final double amount,
                             final Account that,
                             final int timestamp,
                             final String description) {
    MonetaryValue unconvertedMonetaryValue = new MonetaryValue(amount, this.currency);
    MonetaryValue convertedMonetaryValue =
            unconvertedMonetaryValue.convertTo(that.getCurrency());

    double fee = this.getPlanStrategy().calculateFee(unconvertedMonetaryValue);
    MonetaryValue afterFee = new MonetaryValue(unconvertedMonetaryValue.getAmount() + fee,
            unconvertedMonetaryValue.getCurrency());

    if (!hasEnoughFunds(afterFee)) {
      SimpleTransaction transaction = new SimpleTransaction.Builder()
              .description("Insufficient funds")
              .timestamp(timestamp)
              .build();

      this.addTransaction(transaction);
      return;
    }

    unconvertedMonetaryValue.setAmount(unconvertedMonetaryValue.getAmount() + fee);
    this.subtractFunds(unconvertedMonetaryValue);

    unconvertedMonetaryValue.setAmount(unconvertedMonetaryValue.getAmount() - fee);

    if (that.getType().equals("merchant")) {
      double cashback = this.getCashback(
              unconvertedMonetaryValue,
              ((MerchantAccount) that).getMerchant()
      );
      this.addFunds(new MonetaryValue(cashback, unconvertedMonetaryValue.getCurrency()));
    }

    that.addFunds(convertedMonetaryValue);

    SendMoneyTransaction senderTransaction =
            (SendMoneyTransaction) new SendMoneyTransaction.Builder()
                    .timestamp(timestamp)
                    .description(description)
                    .amount(
                            unconvertedMonetaryValue.getBeforeFeeOrCashback(),
                            unconvertedMonetaryValue.getCurrency()
                    )
                    .senderIBAN(this.getIban())
                    .receiverIBAN(that.getIban())
                    .transferType("sent")
                    .build();

    SendMoneyTransaction receiverTransaction =
            (SendMoneyTransaction) new SendMoneyTransaction.Builder()
                    .timestamp(timestamp)
                    .description(description)
                    .amount(
                            convertedMonetaryValue.getBeforeFeeOrCashback(),
                            convertedMonetaryValue.getCurrency()
                    )
                    .senderIBAN(this.getIban())
                    .receiverIBAN(that.getIban())
                    .transferType("received")
                    .build();

    this.addTransaction(senderTransaction);
    that.addTransaction(receiverTransaction);
  }

  /**
   * Delete a card from the account
   *
   * @param card               the card to delete
   * @param timestamp          the timestamp of the deletion
   * @param bypassBalanceCheck if the balance check should be bypassed (e.g. for the OneTimeCard)
   */
  public void deleteCard(final Card card, final int timestamp, final boolean bypassBalanceCheck) {
    if (this.balance == 0 || bypassBalanceCheck && cards.remove(card)) {
      DeleteCardTransaction transaction = new DeleteCardTransaction.Builder()
              .account(this.iban)
              .card(card.getCardNumber())
              .cardHolder(this.getOwner().getEmail())
              .description("The card has been destroyed")
              .timestamp(timestamp)
              .build();

      this.addTransaction(transaction);
    }
  }

  /**
   * Check if the account has enough funds
   *
   * @param monetaryValue the amount to check
   * @return if the account has enough funds
   */
  public final boolean hasEnoughFunds(final MonetaryValue monetaryValue) {
    monetaryValue.convertTo(this.currency);

    return monetaryValue.getAmount() <= balance;
  }

  /**
   * Try to upgrade the account plan
   *
   * @param plan      the plan to upgrade to
   * @param timestamp the timestamp of the upgrade
   * @throws Exception if the user doesn't have enough funds
   */
  public final void tryPlanUpgrade(final String plan, final int timestamp) throws Exception {
    // Determine the price of the upgrade
    double price = getPlanStrategy().canUpgradeTo(plan)
            ? getPlanStrategy().getUpgradeCost(plan) : -1;

    // If the price is -1, the user can't upgrade to the specified plan
    if (price == -1) {
      return;
    }

    MonetaryValue monetaryValue = new MonetaryValue(price, "RON").convertTo(this.currency);

    if (!hasEnoughFunds(monetaryValue)) {
      SimpleTransaction transaction = new SimpleTransaction.Builder()
              .description("Insufficient funds")
              .timestamp(timestamp)
              .build();

      this.addTransaction(transaction);

      throw new Exception("Insufficient funds");
    }

    subtractFunds(monetaryValue);

    owner.setPlanStrategy(
            switch (plan) {
              case "silver" -> new SilverPlanStrategy();
              case "gold" -> new GoldPlanStrategy();
              default -> this.getPlanStrategy();
            }
    );
  }

  /**
   * Subtract funds from the account
   *
   * @param monetaryValue the amount to subtract
   */
  public final void subtractFunds(final MonetaryValue monetaryValue) {
    monetaryValue.convertTo(this.currency);

    if (monetaryValue.getAmount() < 0) {
      return;
    }

    this.balance -= monetaryValue.getAmount();
  }

  /**
   * Add funds to the account
   *
   * @param monetaryValue the amount to add
   */
  public final void addFunds(final MonetaryValue monetaryValue) {
    monetaryValue.convertTo(this.currency);

    if (monetaryValue.getAmount() < 0) {
      return;
    }

    this.balance += monetaryValue.getAmount();
  }

  @JsonIgnore
  public final AccountPlanStrategy getPlanStrategy() {
    return this.owner.getPlanStrategy();
  }

  /**
   * Get the string representation of the account
   */
  @Override
  public String toString() {
    if (owner == null) {
      return "[VOID ACCOUNT]";
    }

    return String.format("%s: %6.2f (%s) [%s] owned by %s", iban, balance, currency, type,
            owner);
  }

  /**
   * Check if the user has access to the account
   *
   * @param user the user to check
   * @return if the user has access to the account
   */
  public boolean checkAccess(final User user) {
    return owner.equals(user);
  }

  /**
   * Add a transaction to the account
   *
   * @param transaction the transaction to add
   */
  public void addTransaction(final Transaction transaction) {
    transactions.add(transaction);
  }

  /**
   * Generate a report for the account
   *
   * @param start the start of the report
   * @param end   the end of the report
   * @return the report
   * @throws Exception if the report can't be generated
   */
  public ObjectNode generateReport(final int start, final int end) throws Exception {
    return null;
  }

  /**
   * Get the cashback for the account
   *
   * @param monetaryValue the amount to get the cashback for
   * @param merchant      the merchant to get the cashback from
   * @return the cashback
   */
  public double getCashback(final MonetaryValue monetaryValue, final Merchant merchant) {
    // Update the total amount spent for the spendingThreshold strategy
    if (merchant.getCashbackStrategyString().equals("spendingThreshold")) {
      stTotalRON += monetaryValue.convertTo("RON").getAmount();
    }

    double cashback = merchant.getCashbackStrategy().getCashback(monetaryValue, this, merchant);

    // Update the number of transactions for the nrOfTransactions strategy
    if (merchant.getCashbackStrategyString().equals("nrOfTransactions")) {
      addMerchantTransaction(merchant);
    }

    return cashback;
  }

  /**
   * Use a cashback for the account
   *
   * @param merchantType the merchant type to use the cashback for
   */
  public void useCashback(final MerchantType merchantType) {
    ntUsedCashbacks.add(merchantType);
  }

  /**
   * Add to the number of transactions for a merchant
   *
   * @param merchant the merchant to add the transaction to
   */
  public void addMerchantTransaction(final Merchant merchant) {
    int current = ntMerchantTransactions.getOrDefault(merchant, 0);
    ntMerchantTransactions.put(merchant, current + 1);
  }

  /**
   * Get the number of transactions for a merchant
   *
   * @param merchant the merchant to get the transactions for
   * @return the number of transactions
   */
  public int getMerchantNOfTransactions(final Merchant merchant) {
    return ntMerchantTransactions.getOrDefault(merchant, 0);
  }
}
