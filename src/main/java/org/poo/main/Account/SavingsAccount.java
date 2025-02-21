package org.poo.main.Account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.poo.main.Bank.Transaction.AddInterestRateTransaction;
import org.poo.main.Bank.Transaction.SimpleTransaction;
import org.poo.main.Entities.User;

@Getter
@Setter
public final class SavingsAccount extends Account {
  @JsonIgnore
  private double interestRate;

  public SavingsAccount(final User owner, final String currency,
                        final String type, final double interestRate) {
    super(owner, currency, type);
    this.interestRate = interestRate;
  }

  /**
   * Updates the interest rate for the account and logs the change as a transaction.
   */
  public void changeInterest(final double interest, final int timestamp) {
    this.interestRate = interest;

    String description =
            String.format("Interest rate of the account changed to %.2f", interestRate);

    SimpleTransaction transaction = new SimpleTransaction.Builder()
            .timestamp(timestamp)
            .description(description)
            .build();

    this.addTransaction(transaction);
  }

  /**
   * Applies the current interest rate to the account's balance
   * and updates the balance accordingly.
   * (Should've created a transaction, that's why I let timestamp param here)
   */
  public void addInterest(final int timestamp) {
    double currentBalance = getBalance();

    this.setBalance((1 + interestRate) * currentBalance);

    AddInterestRateTransaction transaction = new AddInterestRateTransaction.Builder()
            .currency(getCurrency())
            .amount(interestRate * currentBalance)
            .timestamp(timestamp)
            .description("Interest rate income")
            .build();

    this.addTransaction(transaction);
  }
}
