package org.poo.main.Bank.Transaction;

import lombok.Getter;

@Getter
public final class AddCardTransaction extends Transaction {
  private final String account;
  private final String card;
  private final String cardHolder;

  private AddCardTransaction(final Builder builder) {
    super(builder);
    this.account = builder.account;
    this.card = builder.card;
    this.cardHolder = builder.cardHolder;
  }

  public static class Builder extends Transaction.Builder<Builder> {
    private String account;
    private String card;
    private String cardHolder;

    /**
     * Set the account of the transaction.
     */
    public Builder account(final String acc) {
      this.account = acc;
      return this;
    }

    /**
     * Set the card of the transaction.
     */
    public Builder card(final String crd) {
      this.card = crd;
      return this;
    }

    /**
     * Set the cardHolder of the transaction.
     */
    public Builder cardHolder(final String crdHolder) {
      this.cardHolder = crdHolder;
      return this;
    }

    @Override
    public final AddCardTransaction build() {
      return new AddCardTransaction(this);
    }

    @Override
    public final Builder self() {
      return this;
    }
  }
}
