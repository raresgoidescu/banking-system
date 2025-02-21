package org.poo.main.Account.Plans;

import org.poo.main.Bank.MonetaryValue;
import org.poo.main.Entities.User;

public final class StudentPlanStrategy implements AccountPlanStrategy {
  private static final double STUDENT_TRANSACTION_FEE = 0.0;
  private static final double STUDENT_TO_SILVER_COST = 100;
  private static final double STUDENT_TO_GOLD_COST = 350;

  @Override
  public double calculateFee(final MonetaryValue monetaryValue) {
    return STUDENT_TRANSACTION_FEE;
  }

  @Override
  public boolean canUpgradeTo(final String newPlanType) {
    return "silver".equals(newPlanType) || "gold".equals(newPlanType);
  }

  @Override
  public double getUpgradeCost(final String newPlanType) throws Exception {
    return switch (newPlanType) {
      case "silver" -> STUDENT_TO_SILVER_COST;
      case "gold" -> STUDENT_TO_GOLD_COST;
      default -> throw new Exception("Invalid planType: " + newPlanType);
    };
  }

  @Override
  public boolean tryUpgrade(final User user, final MonetaryValue monetaryValue) {
    return false;
  }

  @Override
  public String toString() {
    return "student";
  }
}
