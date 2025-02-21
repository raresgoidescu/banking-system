package org.poo.main.Entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommerciantInput;
import org.poo.main.Entities.CashbackStrategy.CashbackStrategy;
import org.poo.main.Entities.CashbackStrategy.NrOfTransactionsCashback;
import org.poo.main.Entities.CashbackStrategy.SpendingThresholdCashback;

@Getter
@Setter
public class Merchant {
  @JsonProperty("commerciant")
  private String name;
  private int id;
  private String account;
  private String typeString;
  private MerchantType type;
  private String cashbackStrategyString;
  private CashbackStrategy cashbackStrategy;
  private int numberTransactions;

  public Merchant(final CommerciantInput merchantInput) {
    this.name = merchantInput.getCommerciant();
    this.id = merchantInput.getId();
    this.account = merchantInput.getAccount();
    this.typeString = merchantInput.getType();
    this.cashbackStrategyString = merchantInput.getCashbackStrategy();

    // Set the cashback strategy
    switch (this.cashbackStrategyString) {
      case "nrOfTransactions":
        this.cashbackStrategy = new NrOfTransactionsCashback();
        break;
      case "spendingThreshold":
        this.cashbackStrategy = new SpendingThresholdCashback();
        break;
      default:
        throw new IllegalArgumentException("Invalid cashback strategy");
    }

    this.type = MerchantType.valueOf(typeString.toUpperCase());
  }
}
