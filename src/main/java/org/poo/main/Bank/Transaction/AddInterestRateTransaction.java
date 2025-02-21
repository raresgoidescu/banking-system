package org.poo.main.Bank.Transaction;

import lombok.Getter;

@Getter
public final class AddInterestRateTransaction extends Transaction {
  private final double amount;
  private final String currency;

  public AddInterestRateTransaction(final Builder builder) {
    super(builder);
    this.amount = builder.amount;
    this.currency = builder.currency;
  }

  public static class Builder extends Transaction.Builder<Builder> {
    private double amount;
    private String currency;

    /**
     * Set the amount of the transaction
     */
    public Builder amount(final double val) {
      this.amount = val;
      return this;
    }

    /**
     * Set the currency of the transaction
     */
    public Builder currency(final String curr) {
      this.currency = curr;
      return this;
    }

    @Override
    public final AddInterestRateTransaction build() {
      return new AddInterestRateTransaction(this);
    }

    @Override
    protected final Builder self() {
      return this;
    }
  }
}
