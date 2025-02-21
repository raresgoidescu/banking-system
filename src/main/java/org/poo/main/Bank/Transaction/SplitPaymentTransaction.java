package org.poo.main.Bank.Transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SplitPaymentTransaction extends Transaction {
  @Setter
  private List<Double> amountForUsers;
  @Setter
  private Double amount;
  private final String currency;
  @Getter
  private final List<String> involvedAccounts;
  private final String splitPaymentType;

  public SplitPaymentTransaction(final Builder builder) {
    super(builder);
    this.amountForUsers = builder.amountForUsers;
    this.amount = builder.amount;
    this.currency = builder.currency;
    this.involvedAccounts = builder.involvedAccounts;
    this.splitPaymentType = builder.splitPaymentType;
  }

  public static class Builder extends Transaction.Builder<Builder> {
    private List<Double> amountForUsers;
    private Double amount;
    private String currency;
    private List<String> involvedAccounts;
    private String splitPaymentType;

    /**
     * Set the amount for users.
     */
    public Builder amountForUsers(final List<Double> amntForUsers) {
      this.amountForUsers = amntForUsers;
      return this;
    }

    /**
     * Set the amount.
     */
    public Builder amount(final double val) {
      this.amount = val;
      return this;
    }

    /**
     * Set the currency.
     */
    public Builder currency(final String curr) {
      this.currency = curr;
      return this;
    }

    /**
     * Set the involved accounts.
     */
    public Builder involvedAccounts(final List<String> involvedAcc) {
      this.involvedAccounts = involvedAcc;
      return this;
    }

    /**
     * Set the split payment type.
     */
    public Builder splitPaymentType(final String spltPaymentType) {
      this.splitPaymentType = spltPaymentType;
      return this;
    }

    @Override
    protected final Builder self() {
      return this;
    }

    @Override
    public final SplitPaymentTransaction build() {
      return new SplitPaymentTransaction(this);
    }
  }

  @Override
  public final String toString() {
    return "{\n"
            + "\"timestamp\": " + super.getTimestamp() + ",\n"
            + "\"description\": \"" + super.getDescription() + "\",\n"
            + "\"error\": \"" + super.getError() + "\",\n"
            + "\"amountForUsers\": " + amountForUsers + ",\n"
            + "\"amount\": " + amount + ",\n"
            + "\"currency\": \"" + currency + "\",\n"
            + "\"involvedAccounts\": " + involvedAccounts + ",\n"
            + "\"splitPaymentType\": \"" + splitPaymentType + "\""
            + "\n}";
  }
}
