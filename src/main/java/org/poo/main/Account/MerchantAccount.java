package org.poo.main.Account;

import org.poo.main.Entities.Merchant;

public final class MerchantAccount extends Account {
  private final Merchant merchant;

  public MerchantAccount(final Merchant merchant) {
    this.merchant = merchant;
    this.setCurrency("RON");
    this.setBalance(0);
    this.setType("merchant");
    this.setIban(merchant.getAccount());
  }

  public Merchant getMerchant() {
    return merchant;
  }

  public String getMerchantName() {
    return merchant.getName();
  }

  @Override
  public String toString() {
    return String.format("%s %s %s %s ",
            this.getIban(), this.getBalance(), this.getCurrency(), this.getMerchantName());
  }
}
