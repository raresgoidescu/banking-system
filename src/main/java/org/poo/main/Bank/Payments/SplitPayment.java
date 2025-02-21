package org.poo.main.Bank.Payments;

import lombok.Getter;
import org.poo.main.Account.Account;
import org.poo.main.Bank.Bank;
import org.poo.main.Bank.Transaction.SplitPaymentTransaction;

import java.util.List;

import static org.poo.main.Utils.Utils.info;

@Getter
public final class SplitPayment extends Payment {
  private final String type;
  private final List<Account> accounts;
  private final Double totalAmount;
  private final List<Double> amounts;

  // This will ALWAYS have SimplePayments, not any other type of Payment
  //  this is why I chose List<SimplePayment>
  private final List<SimplePayment> payments;

  private final String currency;
  private final SplitPaymentTransaction transaction;

  public SplitPayment(final String type, final List<Account> accounts,
                      final Double totalAmount, final List<Double> amounts,
                      final List<SimplePayment> payments, final String currency,
                      final SplitPaymentTransaction transaction) {
    this.type = type;
    this.accounts = accounts;
    this.totalAmount = totalAmount;
    this.amounts = amounts;
    this.payments = payments;
    this.currency = currency;
    this.transaction = transaction;
  }

  /**
   * Adds a payment to the split payment.
   *
   * @param payment the payment to be added
   */
  public void addPayment(final SimplePayment payment) {
    payments.add(payment);
  }

  /**
   * Processes the split payment.
   *
   * @throws Exception if the payment cannot be processed
   */
  @Override
  public void process() throws Exception {
    for (SimplePayment payment : payments) {
      if (!this.isRejected()) {
        payment.process();
      }
    }
  }

  private Account findPoorGuy() {
    Bank bank = Bank.getInstance(null);

    return payments.stream()
            .filter(payment -> !payment.hasMoney())
            .map(payment -> bank.getAccount(payment.getSenderIban()))
            .findFirst()
            .orElse(null);
  }

  @Override
  public boolean accept() {
    return this.isAccepted();
  }

  /**
   * Accepts the payment and sets the transaction for every participant.
   * If all payments are accepted, the split payment is processed.
   * If a user has insufficient funds, the split payment is not processed.
   * If the split payment is processed, add transaction for every user.
   */
  public boolean accept(final SimplePayment payment) {
    payment.setAccepted(true);

    if (allPaymentsAccepted()) {
      info("Every user accepted the payment");
      Account poorAccount = findPoorGuy();
      if (poorAccount == null) {
        try {
          this.process();
          addTransactionForEveryUser();

          return true;
        } catch (Exception e) {
          System.out.println(e.getMessage());
        }
      } else {
        info("Account " + poorAccount.getIban()
                + " has insufficient funds for a split payment.");
        transaction.setError("Account " + poorAccount.getIban()
                + " has insufficient funds for a split " + "payment.");
        addTransactionForEveryUser();
      }
    }

    return false;
  }

  /**
   * Rejects the payment and sets the transaction for every participant.
   *
   * @param payment the payment to be rejected
   */
  public void reject(final SimplePayment payment) {
    payment.setRejected(true);

    info("Payment rejected");

    transaction.setError("One user rejected the payment.");

    addTransactionForEveryUser();

    this.setRejected(true);
  }

  @Override
  public boolean reject() {
    this.setRejected(true);

    return this.isRejected();
  }

  private boolean allPaymentsAccepted() {
    return payments.stream().allMatch(Payment::isAccepted);
  }

  private void addTransactionForEveryUser() {
    Bank bank = Bank.getInstance(null);
    payments.forEach(payment -> {
      Account account = bank.getAccount(payment.getSenderIban());
      account.getTransactions().add(transaction);
    });
  }
}
