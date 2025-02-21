package org.poo.main.Account.Plans;

import org.poo.main.Bank.MonetaryValue;
import org.poo.main.Entities.User;

public interface AccountPlanStrategy {
  /**
   * Calculates the fee for the account plan
   *
   * @param monetaryValue the monetary value to calculate the fee
   * @return the fee
   */
  double calculateFee(MonetaryValue monetaryValue);

  /**
   * Checks if the account plan can be upgraded to a new plan
   *
   * @param newPlanType the new plan type
   * @return true if the account plan can be upgraded to the new plan, false otherwise
   */
  boolean canUpgradeTo(String newPlanType);

  /**
   * Gets the cost to upgrade to a new plan
   *
   * @param newPlanType the new plan type
   * @return the cost to upgrade to the new plan
   * @throws Exception if the account plan can't be upgraded to the new plan
   */
  double getUpgradeCost(String newPlanType) throws Exception;

  /**
   * Upgrades the account plan to a new plan
   *
   * @param user          the user to upgrade the account plan
   * @param monetaryValue the monetary value to upgrade the account plan
   * @return true if the account plan was upgraded, false otherwise
   */
  boolean tryUpgrade(User user, MonetaryValue monetaryValue);

  /**
   * Gets the account plan type
   *
   * @return the account plan type
   */
  String toString();
}
