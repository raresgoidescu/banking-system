package org.poo.main.Entities.CashbackStrategy;

import org.poo.main.Account.Account;
import org.poo.main.Bank.MonetaryValue;
import org.poo.main.Entities.Merchant;

public interface CashbackStrategy {
  /**
   * Get the cashback value for a given monetary value, account and merchant
   *
   * @param monetaryValue The monetary value to calculate the cashback
   * @param account       The account to calculate the cashback
   * @param merchant      The merchant to calculate the cashback
   * @return The cashback value
   */
  double getCashback(MonetaryValue monetaryValue, Account account, Merchant merchant);
}
