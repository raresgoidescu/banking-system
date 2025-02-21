package org.poo.main.Bank.Transaction;

import lombok.Getter;

@Getter
public class DeleteCardTransaction extends Transaction {
  private final String account;
  private final String cardHolder;
  private final String card;

  public DeleteCardTransaction(final Builder builder) {
    super(builder);
    this.account = builder.account;
    this.cardHolder = builder.cardHolder;
    this.card = builder.card;
  }

  public static class Builder extends Transaction.Builder<Builder> {
    private String account;
    private String cardHolder;
    private String card;

    /**
     * Set the account
     */
    public Builder account(final String acc) {
      this.account = acc;
      return this;
    }

    /**
     * Set the cardHolder
     */
    public Builder cardHolder(final String crdHolder) {
      this.cardHolder = crdHolder;
      return this;
    }

    /**
     * Set the deleted card number
     */
    public Builder card(final String crd) {
      this.card = crd;
      return this;
    }

    @Override
    protected final Builder self() {
      return this;
    }

    @Override
    public final DeleteCardTransaction build() {
      return new DeleteCardTransaction(this);
    }
  }
}
