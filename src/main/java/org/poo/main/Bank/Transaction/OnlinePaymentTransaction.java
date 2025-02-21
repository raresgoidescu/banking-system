package org.poo.main.Bank.Transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.poo.main.Entities.User;

@Getter
public class OnlinePaymentTransaction extends Transaction {
  @JsonIgnore
  private final User user;
  private final double amount;
  @JsonProperty("commerciant")
  private String merchant;

  public OnlinePaymentTransaction(final Builder builder) {
    super(builder);
    this.amount = builder.amount;
    this.merchant = builder.merchant;
    this.user = builder.user;
  }

  public static class Builder extends Transaction.Builder<Builder> {
    private double amount;
    private String merchant;
    private User user;

    /**
     * Sets the amount of the transaction.
     */
    public Builder amount(final double val) {
      this.amount = val;
      return this;
    }

    /**
     * Sets the user of the transaction.
     */
    public Builder user(final User usr) {
      this.user = usr;
      return this;
    }

    /**
     * Sets the merchant of the transaction.
     */
    public Builder merchant(final String merch) {
      this.merchant = merch;
      return this;
    }

    @Override
    protected final Builder self() {
      return this;
    }

    @Override
    public final OnlinePaymentTransaction build() {
      return new OnlinePaymentTransaction(this);
    }
  }
}
