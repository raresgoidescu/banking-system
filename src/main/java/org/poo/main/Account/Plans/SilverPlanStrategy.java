package org.poo.main.Account.Plans;

import org.poo.main.Bank.MonetaryValue;
import org.poo.main.Entities.User;

public final class SilverPlanStrategy implements AccountPlanStrategy {
  private static final double SILVER_TO_GOLD_TRANSACTION_THRESHOLD = 300;
  private static final double SILVER_TO_GOLD_COST = 250;
  private static final double SILVER_TRANSACTION_FEE = 0.001;
  private static final double SILVER_TRANSACTION_FEE_THRESHOLD = 500;
  private static final int TRANSACTIONS_TO_GOLD = 5;

  private int currentTransactionsToGold;

  @Override
  public double calculateFee(final MonetaryValue monetaryValue) {
    MonetaryValue monetaryValueInRON = monetaryValue.convertTo("RON");

    return monetaryValueInRON.getAmount() < SILVER_TRANSACTION_FEE_THRESHOLD
            ? 0.0 : monetaryValue.getAmount() * SILVER_TRANSACTION_FEE;
  }

  @Override
  public boolean canUpgradeTo(final String newPlanType) {
    return "gold".equals(newPlanType);
  }

  @Override
  public double getUpgradeCost(final String newPlanType) throws Exception {
    return switch (newPlanType) {
      case "gold" -> SILVER_TO_GOLD_COST;
      default -> throw new Exception("Invalid planType: " + newPlanType);
    };
  }

  @Override
  public boolean tryUpgrade(final User user, final MonetaryValue monetaryValue) {
    MonetaryValue monetaryValueInRON = monetaryValue.convertTo("RON");

    if (monetaryValueInRON.getAmount() >= SILVER_TO_GOLD_TRANSACTION_THRESHOLD) {
      currentTransactionsToGold++;
    }

    if (currentTransactionsToGold >= TRANSACTIONS_TO_GOLD) {
      user.setPlanStrategy(new GoldPlanStrategy());
      return true;
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return "silver";
  }
}
