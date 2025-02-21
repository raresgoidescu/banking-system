package org.poo.main.Entities.CashbackStrategy;

import org.poo.main.Account.Account;
import org.poo.main.Account.Plans.AccountPlanStrategy;
import org.poo.main.Bank.MonetaryValue;
import org.poo.main.Entities.Merchant;
import org.poo.main.Entities.MerchantType;

import java.util.Map;

import static org.poo.main.Entities.CashbackStrategy.CashbackConstants.FIRST_GOLD_RATE_ST;
import static org.poo.main.Entities.CashbackStrategy.CashbackConstants.FIRST_SILVER_RATE_ST;
import static org.poo.main.Entities.CashbackStrategy.CashbackConstants.FIRST_STANDARD_RATE_ST;
import static org.poo.main.Entities.CashbackStrategy.CashbackConstants.FIRST_THESHOLD_RATE_NT;
import static org.poo.main.Entities.CashbackStrategy.CashbackConstants.FIRST_THRESHOLD_NT;
import static org.poo.main.Entities.CashbackStrategy.CashbackConstants.FIRST_THRESHOLD_ST;
import static org.poo.main.Entities.CashbackStrategy.CashbackConstants.SECOND_GOLD_RATE_ST;
import static org.poo.main.Entities.CashbackStrategy.CashbackConstants.SECOND_SILVER_RATE_ST;
import static org.poo.main.Entities.CashbackStrategy.CashbackConstants.SECOND_STANDARD_RATE_ST;
import static org.poo.main.Entities.CashbackStrategy.CashbackConstants.SECOND_THRESHOLD_NT;
import static org.poo.main.Entities.CashbackStrategy.CashbackConstants.SECOND_THRESHOLD_RATE_NT;
import static org.poo.main.Entities.CashbackStrategy.CashbackConstants.SECOND_THRESHOLD_ST;
import static org.poo.main.Entities.CashbackStrategy.CashbackConstants.THIRD_GOLD_RATE_ST;
import static org.poo.main.Entities.CashbackStrategy.CashbackConstants.THIRD_SILVER_RATE_ST;
import static org.poo.main.Entities.CashbackStrategy.CashbackConstants.THIRD_STANDARD_RATE_ST;
import static org.poo.main.Entities.CashbackStrategy.CashbackConstants.THIRD_THRESHOLD_NT;
import static org.poo.main.Entities.CashbackStrategy.CashbackConstants.THIRD_THRESHOLD_RATE_NT;
import static org.poo.main.Entities.CashbackStrategy.CashbackConstants.THIRD_THRESHOLD_ST;
import static org.poo.main.Utils.Utils.info;

/**
 * Cashback strategy for spending threshold.
 * The cashback rate is determined based on the total amount spent and the account plan.
 */
public final class SpendingThresholdCashback implements CashbackStrategy {
  public SpendingThresholdCashback() {
  }

  /**
   * Calculates the cashback for a given monetary value, account and merchant.
   * The cashback is calculated based on the total amount spent and the account plan.
   * If the merchant is of a certain type and the account has made a certain number of transactions
   * with that type of merchant, the cashback rate is increased.
   *
   * @param monetaryValue The monetary value to calculate the cashback
   * @param account       The account to calculate the cashback
   * @param merchant      The merchant to calculate the cashback
   * @return The cashback value
   */
  @Override
  public double getCashback(final MonetaryValue monetaryValue,
                            final Account account,
                            final Merchant merchant) {
    double total = account.getStTotalRON();
    double rate = determineCashbackRate(total, account.getOwner().getPlanStrategy());
    double cashback = monetaryValue.getAmount() * rate;

    // This is bonkers but modern problems require modern solutions :)
    if (!account.getNtUsedCashbacks().contains(merchant.getType())) {
      double possibleNTCashback = 0;
      MerchantType type = merchant.getType();

      for (Map.Entry<Merchant, Integer> entry : account.getNtMerchantTransactions()
              .entrySet()) {
        if (entry.getKey().getType() == type) {
          int count = entry.getValue();
          double rateNT = switch (type) {
            case FOOD -> count >= FIRST_THRESHOLD_NT ? FIRST_THESHOLD_RATE_NT : 0.0;
            case CLOTHES -> count >= SECOND_THRESHOLD_NT ? SECOND_THRESHOLD_RATE_NT : 0.0;
            case TECH -> count >= THIRD_THRESHOLD_NT ? THIRD_THRESHOLD_RATE_NT : 0.0;
          };

          if (rateNT == 0) {
            continue;
          }

          possibleNTCashback = monetaryValue.getAmount() * rateNT;
          account.useCashback(type);

          break;
        }
      }

      info("Possible cashback for number of transactions: %.2f", possibleNTCashback);

      cashback += possibleNTCashback;
    }

    info(String.format("Cashback for spending threshold: %.2f", cashback));

    return cashback;
  }

  /**
   * Determines the cashback rate based on the total amount spent and the account plan.
   *
   * @param total total amount spent
   * @param plan  account plan
   * @return the cashback rate
   */
  private double determineCashbackRate(final double total, final AccountPlanStrategy plan) {
    if (total >= THIRD_THRESHOLD_ST) {
      return switch (plan.toString()) {
        case "standard", "student" -> THIRD_STANDARD_RATE_ST;
        case "silver" -> THIRD_SILVER_RATE_ST;
        case "gold" -> THIRD_GOLD_RATE_ST;
        default -> 0;
      };
    } else if (total >= SECOND_THRESHOLD_ST) {
      return switch (plan.toString()) {
        case "standard", "student" -> SECOND_STANDARD_RATE_ST;
        case "silver" -> SECOND_SILVER_RATE_ST;
        case "gold" -> SECOND_GOLD_RATE_ST;
        default -> 0;
      };
    } else if (total >= FIRST_THRESHOLD_ST) {
      return switch (plan.toString()) {
        case "standard", "student" -> FIRST_STANDARD_RATE_ST;
        case "silver" -> FIRST_SILVER_RATE_ST;
        case "gold" -> FIRST_GOLD_RATE_ST;
        default -> 0;
      };
    }
    return 0;
  }
}
