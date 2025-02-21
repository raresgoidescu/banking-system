package org.poo.main.Bank;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class MonetaryValue {
  private double beforeFeeOrCashback;
  private double amount;
  private String currency;

  public MonetaryValue(final double amount, final String currency) {
    this.amount = amount;
    this.currency = currency;
    this.beforeFeeOrCashback = amount;
  }

  /**
   * Convert the monetary value to another currency
   *
   * @param newCurrency the currency to convert to
   * @return the converted monetary value
   */
  public MonetaryValue convertTo(final String newCurrency) {
    Double rate = ExchangeGraph.getInstance().getRate(this.currency, newCurrency);

    Objects.requireNonNull(rate);

    MonetaryValue converted = new MonetaryValue(this.amount * rate, newCurrency);
    converted.setBeforeFeeOrCashback(this.beforeFeeOrCashback * rate);

    return converted;
  }
}
