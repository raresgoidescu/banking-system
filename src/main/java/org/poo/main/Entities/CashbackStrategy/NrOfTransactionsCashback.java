package org.poo.main.Entities.CashbackStrategy;

import org.poo.main.Account.Account;
import org.poo.main.Bank.MonetaryValue;
import org.poo.main.Entities.Merchant;

import static org.poo.main.Entities.CashbackStrategy.CashbackConstants.FIRST_THESHOLD_RATE_NT;
import static org.poo.main.Entities.CashbackStrategy.CashbackConstants.FIRST_THRESHOLD_NT;
import static org.poo.main.Entities.CashbackStrategy.CashbackConstants.SECOND_THRESHOLD_NT;
import static org.poo.main.Entities.CashbackStrategy.CashbackConstants.SECOND_THRESHOLD_RATE_NT;
import static org.poo.main.Entities.CashbackStrategy.CashbackConstants.THIRD_THRESHOLD_NT;
import static org.poo.main.Entities.CashbackStrategy.CashbackConstants.THIRD_THRESHOLD_RATE_NT;
import static org.poo.main.Utils.Utils.info;
import static org.poo.main.Utils.Utils.warn;

/**
 * Cashback strategy based on the number of transactions made by the account holder.
 * The cashback rate is determined by the number of transactions made with a specific merchant.
 */
public final class NrOfTransactionsCashback implements CashbackStrategy {
  @Override
  public double getCashback(final MonetaryValue monetaryValue,
                            final Account account,
                            final Merchant merchant) {
    if (account.getNtUsedCashbacks().contains(merchant.getType())) {
      warn("Already used cashback for type: " + merchant.getTypeString());
      return 0;
    }

    int count = account.getMerchantNOfTransactions(merchant);
    double rate = switch (merchant.getType()) {
      case FOOD -> count >= FIRST_THRESHOLD_NT ? FIRST_THESHOLD_RATE_NT : 0.0;
      case CLOTHES -> count >= SECOND_THRESHOLD_NT ? SECOND_THRESHOLD_RATE_NT : 0.0;
      case TECH -> count >= THIRD_THRESHOLD_NT ? THIRD_THRESHOLD_RATE_NT : 0.0;
    };
    double cashback = monetaryValue.getAmount() * rate;

    if (rate != 0.0) {
      account.useCashback(merchant.getType());
      info(String.format("Merchant name %s type %s", merchant.getName(), merchant.getType()));
      info(String.format("Cashback for number of transactions: %.2f", cashback));
    }

    return cashback;
  }
}
