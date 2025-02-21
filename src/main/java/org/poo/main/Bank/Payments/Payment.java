package org.poo.main.Bank.Payments;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Payment {
  private boolean accepted = false;
  private boolean rejected = false;

  /**
   * Accepts the payment.
   *
   * @return true if the payment was accepted, false otherwise.
   */
  public abstract boolean accept();

  /**
   * Rejects the payment.
   *
   * @return true if the payment was rejected, false otherwise.
   */
  public abstract boolean reject();

  /**
   * Processes the payment.
   *
   * @throws Exception if the payment could not be processed.
   */
  public abstract void process() throws Exception;
}
