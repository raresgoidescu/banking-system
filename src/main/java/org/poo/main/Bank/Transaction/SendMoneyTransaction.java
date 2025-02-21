package org.poo.main.Bank.Transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class SendMoneyTransaction extends Transaction {
  @JsonIgnore
  private static final int PRECISION = 13;

  private final Object amount;
  private final String transferType;
  private final String senderIBAN;
  private final String receiverIBAN;

  public SendMoneyTransaction(final Builder builder) {
    super(builder);
    this.amount = builder.amount;
    this.transferType = builder.transferType;
    this.senderIBAN = builder.senderIBAN;
    this.receiverIBAN = builder.receiverIBAN;
  }

  public static class Builder extends Transaction.Builder<Builder> {
    private Object amount;
    private String transferType;
    private String senderIBAN;
    private String receiverIBAN;

    /**
     * Set the amount of the transaction
     */
    public Builder amount(final double value, final String currency) {
      BigDecimal amountBD = BigDecimal.valueOf(value)
              .setScale(PRECISION, RoundingMode.HALF_UP)
              .stripTrailingZeros();

      if (amountBD.scale() < 1) {
        amountBD = amountBD.setScale(1, RoundingMode.HALF_UP);
      }

      this.amount = String.format("%s %s", amountBD, currency);

      return this;
    }

    /**
     * Set the transfer type of the transaction
     */
    public Builder transferType(final String transfType) {
      this.transferType = transfType;
      return this;
    }

    /**
     * Set the sender IBAN of the transaction
     */
    public Builder senderIBAN(final String senderIban) {
      this.senderIBAN = senderIban;
      return this;
    }

    /**
     * Set the receiver IBAN of the transaction
     */
    public Builder receiverIBAN(final String receiverIban) {
      this.receiverIBAN = receiverIban;
      return this;
    }

    @Override
    public final Transaction build() {
      return new SendMoneyTransaction(this);
    }

    @Override
    protected final SendMoneyTransaction.Builder self() {
      return this;
    }
  }
}
