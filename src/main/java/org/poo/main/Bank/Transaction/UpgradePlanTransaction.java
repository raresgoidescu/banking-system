package org.poo.main.Bank.Transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UpgradePlanTransaction extends Transaction {
  private final String newPlanType;
  @JsonProperty("accountIBAN")
  private String accountIban;

  public UpgradePlanTransaction(final Builder builder) {
    super(builder);
    this.newPlanType = builder.newPlanType;
    this.accountIban = builder.accountIban;
  }

  public static class Builder extends Transaction.Builder<Builder> {
    private String newPlanType;
    private String accountIban;

    /**
     * Sets the new plan type.
     */
    public Builder newPlanType(final String newPlanT) {
      this.newPlanType = newPlanT;
      return this;
    }

    /**
     * Sets the account IBAN.
     */
    public Builder accountIban(final String accIban) {
      this.accountIban = accIban;
      return this;
    }

    @Override
    protected final Builder self() {
      return this;
    }

    @Override
    public final UpgradePlanTransaction build() {
      return new UpgradePlanTransaction(this);
    }
  }
}
