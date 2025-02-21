package org.poo.main.Bank.Payments;

import lombok.Getter;
import lombok.Setter;
import org.poo.main.Account.Account;
import org.poo.main.Bank.Bank;
import org.poo.main.Bank.MonetaryValue;

import static org.poo.main.Utils.Utils.info;
import static org.poo.main.Utils.Utils.warn;

@Getter
@Setter
public class SimplePayment extends Payment {
  private Payment parentPayment;

  private String senderIban;
  private String receiverIban;

  private Double unconvertedAmount;
  private String unconvertedCurrency;
  private Double convertedAmount;
  private String convertedCurrency;

  public SimplePayment(final String senderIban, final String receiverIban,
                       final Double unconvertedAmount, final String unconvertedCurrency,
                       final Double convertedAmount, final String convertedCurrency) {
    this.senderIban = senderIban;
    this.receiverIban = receiverIban;
    this.unconvertedAmount = unconvertedAmount;
    this.unconvertedCurrency = unconvertedCurrency;
    this.convertedAmount = convertedAmount;
    this.convertedCurrency = convertedCurrency;

    if (convertedAmount == null) {
      MonetaryValue monetaryValue = new MonetaryValue(unconvertedAmount, unconvertedCurrency);
      this.convertedAmount = monetaryValue.convertTo(this.convertedCurrency).getAmount();
    } else if (unconvertedAmount == null) {
      MonetaryValue monetaryValue = new MonetaryValue(convertedAmount, convertedCurrency);
      this.unconvertedAmount = monetaryValue.convertTo(this.unconvertedCurrency).getAmount();
    }

    info(String.format("Needs to pay %s %s aka %s %s",
            this.getUnconvertedAmount(), this.getUnconvertedCurrency(),
            this.getConvertedAmount(), this.getConvertedCurrency()));
  }

  /**
   * Process the payment
   * Withdraws the amount from the sender account and deposits it into the receiver account
   * If the receiver account does not exist, it will be created (for merchant payments)
   *
   * @throws Exception if the payment is rejected
   */
  @Override
  public void process() throws Exception {
    Bank bank = Bank.getInstance(null);
    Account senderAccount = bank.getAccount(this.getSenderIban());
    Account receiverAccount = bank.getAccount(this.getReceiverIban());

    if (receiverAccount == null) {
      receiverAccount = new Account();
    }

    if (this.isRejected()) {
      warn("Payment rejected");
      return;
    }

    info(String.format("Withdrawing %s %s from account %s",
            this.getUnconvertedAmount(), this.getUnconvertedCurrency(), senderAccount.getIban())
    );

    senderAccount.withdraw(
            new MonetaryValue(this.getUnconvertedAmount(), this.getUnconvertedCurrency())
    );

    info(String.format("New balance for account %s: %s %s", senderAccount.getIban(),
            senderAccount.getBalance(), senderAccount.getCurrency()));

    receiverAccount.deposit(
            new MonetaryValue(this.getConvertedAmount(), this.getConvertedCurrency())
    );
  }

  /**
   * Check if the sender has enough money to make the payment
   *
   * @return true if the sender has enough money, false otherwise
   */
  public boolean hasMoney() {
    Bank bank = Bank.getInstance(null);
    Account account = bank.getAccount(this.getSenderIban());

    return account.hasEnoughFunds(
            new MonetaryValue(this.getUnconvertedAmount(), this.getUnconvertedCurrency())
    );
  }

  @Override
  public final boolean accept() {
    this.setAccepted(true);
    return this.isAccepted();
  }

  @Override
  public final boolean reject() {
    this.setRejected(true);
    return this.isRejected();
  }
}
