package org.poo.main.Account.Plans;

import org.poo.main.Bank.MonetaryValue;
import org.poo.main.Entities.User;

public final class StandardPlanStrategy implements AccountPlanStrategy {
  private static final double STANDARD_TRANSACTION_FEE = 0.002;
  private static final double STANDARD_TO_SILVER_COST = 100;
  private static final double STANDARD_TO_GOLD_COST = 350;

  @Override
  public double calculateFee(final MonetaryValue monetaryValue) {
    return monetaryValue.getAmount() * STANDARD_TRANSACTION_FEE;
  }

  @Override
  public boolean canUpgradeTo(final String newPlanType) {
    return "silver".equals(newPlanType) || "gold".equals(newPlanType);
  }

  @Override
  public double getUpgradeCost(final String newPlanType) throws Exception {
    return switch (newPlanType) {
      case "silver" -> STANDARD_TO_SILVER_COST;
      case "gold" -> STANDARD_TO_GOLD_COST;
      default -> throw new Exception("Invalid planType: " + newPlanType);
    };
  }

  @Override
  public boolean tryUpgrade(final User user, final MonetaryValue monetaryValue) {
    return false;
  }

  @Override
  public String toString() {
    return "standard";
  }
}
