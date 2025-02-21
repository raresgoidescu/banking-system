package org.poo.main.Bank.OutputHelperClasses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.poo.main.Bank.Transaction.Transaction;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Report {
  @JsonProperty("IBAN")
  private String iban;
  private double balance;
  private String currency;
  private final List<Transaction> transactions;

  public Report() {
    transactions = new ArrayList<>();
  }

  /**
   * Sets the IBAN of the report.
   *
   * @return the current instance of the Report class
   */
  public final Report setIban(final String accountIban) {
    this.iban = accountIban;
    return this;
  }

  /**
   * Updates the balance of the report.
   *
   * @return the current instance of the Report class
   */
  public final Report setBalance(final double amount) {
    this.balance = amount;
    return this;
  }

  /**
   * Sets the currency of the report.
   *
   * @return the current instance of the Report class
   */
  public final Report setCurrency(final String curr) {
    this.currency = curr;
    return this;
  }

  /**
   * Adds a transaction to the list of transactions if it is not already present.
   */
  public final void addTransaction(final Transaction t) {
    if (!transactions.contains(t)) {
      transactions.add(t);
    }
  }

  public final String getIban() {
    return iban;
  }

  public final double getBalance() {
    return balance;
  }

  public final String getCurrency() {
    return currency;
  }

  public final List<Transaction> getTransactions() {
    return transactions;
  }
}
