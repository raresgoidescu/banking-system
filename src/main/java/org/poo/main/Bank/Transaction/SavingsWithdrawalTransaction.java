package org.poo.main.Bank.Transaction;

import lombok.Getter;

@Getter
public class SavingsWithdrawalTransaction extends Transaction {
  private final double amount;
  private final String savingsAccountIBAN;
  private final String classicAccountIBAN;

  public SavingsWithdrawalTransaction(final Builder builder) {
    super(builder);
    this.amount = builder.amount;
    this.savingsAccountIBAN = builder.savingsAccountIBAN;
    this.classicAccountIBAN = builder.classicAccountIBAN;
  }

  public static class Builder extends Transaction.Builder<Builder> {
    private double amount;
    private String savingsAccountIBAN;
    private String classicAccountIBAN;

    /**
     * Sets the amount of the transaction.
     */
    public Builder amount(final double val) {
      this.amount = val;
      return this;
    }

    /**
     * Sets the savings account IBAN of the transaction.
     */
    public Builder savingsAccountIBAN(final String savingsAccIBAN) {
      this.savingsAccountIBAN = savingsAccIBAN;
      return this;
    }

    /**
     * Sets the classic account IBAN of the transaction.
     */
    public Builder classicAccountIBAN(final String classicAccIBAN) {
      this.classicAccountIBAN = classicAccIBAN;
      return this;
    }

    @Override
    protected final Builder self() {
      return this;
    }

    @Override
    public final SavingsWithdrawalTransaction build() {
      return new SavingsWithdrawalTransaction(this);
    }
  }
}
