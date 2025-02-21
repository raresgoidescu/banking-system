package org.poo.main.Bank.OutputHelperClasses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.poo.main.Bank.Transaction.OnlinePaymentTransaction;
import org.poo.main.Bank.Transaction.Transaction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public final class SpendingsReport extends Report {
  /**
   * Represents the expenses of a specific merchant.
   */
  @Getter
  private static class Expenses {
    @Setter
    private double total;
    @JsonProperty("commerciant")
    private final String merchant;

    Expenses(final double total, final String merchant) {
      this.total = total;
      this.merchant = merchant;
    }

  }

  @JsonProperty("commerciants")
  private final List<Expenses> merchants;

  @JsonIgnore
  private final Set<Expenses> merchantsSet;

  public SpendingsReport() {
    super();
    merchants = new ArrayList<>();
    Comparator<Expenses> merchantComparator =
            (o1, o2) -> o1.getMerchant().compareTo(o2.getMerchant());
    merchantsSet = new TreeSet<>(merchantComparator);
  }

  /**
   * Updates the list of merchants and their associated expenses based on the current
   * transactions. The method processes the transactions and aggregates the total expense
   * for each unique merchant. If a merchant already exists in the current set of results,
   * their total expense is updated by adding the new transaction amount. Otherwise, a new
   * entry is created for the merchant.
   */
  public void setMerchants() {
    List<Transaction> transactions = this.getTransactions();

    if (transactions == null) {
      return;
    }

    for (Transaction transaction : transactions) {
      String merchant = ((OnlinePaymentTransaction) transaction).getMerchant();
      double amount = ((OnlinePaymentTransaction) transaction).getAmount();

      if (merchant != null) {
        Expenses existing = merchantsSet
                .stream()
                .filter(e -> e.getMerchant().equals(merchant))
                .findFirst()
                .orElse(null);

        if (existing == null) {
          Expenses newExpense =
                  new Expenses(amount, merchant);

          merchantsSet.add(newExpense);
        } else {
          merchantsSet.remove(existing);
          existing.setTotal(amount + existing.getTotal());
          merchantsSet.add(existing);
        }
      }
    }

    merchants.addAll(merchantsSet);
  }
}
