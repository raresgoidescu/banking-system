package org.poo.main.Account.Card;

import org.poo.main.Account.Account;
import org.poo.main.Bank.Transaction.AddCardTransaction;
import org.poo.main.Entities.User;

public final class CardFactory {
  private CardFactory() {
  }

  /**
   * Creates a new card based on the given type.
   *
   * @param user      The user that owns the card.
   * @param account   The account that the card is associated with.
   * @param type      The type of card to create.
   * @param timestamp The timestamp of the transaction.
   * @return The newly created card.
   */
  public static Card createCard(final User user,
                                final Account account,
                                final String type,
                                final int timestamp) {
    Card card;

    switch (type) {
      case "createCard" -> card = new Card(account, user);
      case "createOneTimeCard" -> card = new OneTimeUseCard(account, user);
      default -> {
        return null;
      }
    }

    AddCardTransaction transaction = new AddCardTransaction.Builder()
            .timestamp(timestamp)
            .description("New card created")
            .cardHolder(account.getOwner().getEmail())
            .card(card.getCardNumber())
            .account(account.getIban())
            .build();

    account.addTransaction(transaction);
    account.getCards().add(card);

    return card;
  }
}
