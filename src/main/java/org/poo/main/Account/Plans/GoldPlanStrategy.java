package org.poo.main.Account.Plans;

import org.poo.main.Bank.MonetaryValue;
import org.poo.main.Entities.User;

public final class GoldPlanStrategy implements AccountPlanStrategy {
  @Override
  public double calculateFee(final MonetaryValue monetaryValue) {
    return 0;
  }

  @Override
  public boolean canUpgradeTo(final String newPlanType) {
    return false;
  }

  @Override
  public double getUpgradeCost(final String newPlanType) throws Exception {
    throw new Exception("Connot upgrade from Gold plan");
  }

  @Override
  public boolean tryUpgrade(final User user, final MonetaryValue monetaryValue) {
    return false;
  }

  @Override
  public String toString() {
    return "gold";
  }
}
