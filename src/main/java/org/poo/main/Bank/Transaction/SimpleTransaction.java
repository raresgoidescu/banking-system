package org.poo.main.Bank.Transaction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleTransaction extends Transaction {
  public SimpleTransaction(final Builder builder) {
    super(builder);
  }

  public static class Builder extends Transaction.Builder<Builder> {
    @Override
    protected final Builder self() {
      return this;
    }

    @Override
    public final SimpleTransaction build() {
      return new SimpleTransaction(this);
    }
  }
}
