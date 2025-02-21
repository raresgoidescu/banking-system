package org.poo.main.Account.Card;

import org.poo.main.Account.Account;
import org.poo.main.Entities.Merchant;
import org.poo.main.Entities.User;

public class OneTimeUseCard extends Card {
  public OneTimeUseCard(final Account account, final User owner) {
    super(account, owner);
  }

  /**
   * Processes an online payment using the one-time use card.
   * If the payment is successful, the current cardNumber is replaced with a new one.
   */
  @Override
  public final Card onlinePayment(final User user,
                                  final double amount, final String currency,
                                  final Merchant merchant,
                                  final String description,
                                  final int timestamp) throws Exception {
    super.onlinePayment(user, amount, currency, merchant, description, timestamp);

    this.getAccount().deleteCard(this, timestamp, true);

    return CardFactory.createCard(user, this.getAccount(), "createOneTimeCard", timestamp);
  }
}
