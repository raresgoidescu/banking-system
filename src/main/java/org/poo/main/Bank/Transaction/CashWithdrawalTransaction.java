package org.poo.main.Bank.Transaction;

import lombok.Getter;

@Getter
public final class CashWithdrawalTransaction extends Transaction {
  private final double amount;

  public CashWithdrawalTransaction(final Builder builder) {
    super(builder);
    this.amount = builder.amount;
  }

  public static class Builder extends Transaction.Builder<Builder> {
    private double amount;

    /**
     * Set the amount of the transaction
     */
    public Builder amount(final double val) {
      this.amount = val;
      return this;
    }

    @Override
    protected final Builder self() {
      return this;
    }

    @Override
    public final CashWithdrawalTransaction build() {
      return new CashWithdrawalTransaction(this);
    }
  }
}
