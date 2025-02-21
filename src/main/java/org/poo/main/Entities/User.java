package org.poo.main.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.UserInput;
import org.poo.main.Account.Account;
import org.poo.main.Account.BusinessAccount;
import org.poo.main.Account.Card.Card;
import org.poo.main.Account.Card.CardFactory;
import org.poo.main.Account.Plans.AccountPlanStrategy;
import org.poo.main.Account.Plans.StandardPlanStrategy;
import org.poo.main.Account.Plans.StudentPlanStrategy;
import org.poo.main.Account.SavingsAccount;
import org.poo.main.Bank.Payments.SimplePayment;
import org.poo.main.Bank.Transaction.SimpleTransaction;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Getter
@Setter
public final class User {
  private String email;
  private String lastName;
  private String firstName;
  @JsonIgnore
  private String birthDate;
  @JsonIgnore
  private int age;
  @JsonIgnore
  private String occupation;
  @JsonIgnore
  private AccountPlanStrategy planStrategy;

  @JsonIgnore
  private Queue<SimplePayment> splitPaymentsQueue = new LinkedList<>();

  private List<Account> accounts = new ArrayList<>();

  public User(final UserInput userInput) {
    email = userInput.getEmail();
    lastName = userInput.getLastName();
    firstName = userInput.getFirstName();
    birthDate = userInput.getBirthDate();
    age = getAge();
    occupation = userInput.getOccupation();

    planStrategy = isStudent() ? new StudentPlanStrategy() : new StandardPlanStrategy();
  }

  /**
   * Adds a card to the user's account
   *
   * @param account   the account to which the card will be added
   * @param command   the command to be executed by the card
   * @param timestamp the timestamp of the card creation
   * @return the created card
   */
  public Card addCard(final Account account, final String command,
                      final int timestamp) throws Exception {
    if (account.getOwner().equals(this)) {
      return CardFactory.createCard(this, account, command, timestamp);
    } else {
      throw new Exception("Not this user's account");
    }
  }

  /**
   * Adds a new account to the user
   *
   * @param type         the type of the account
   * @param currency     the currency of the account
   * @param interestRate the interest rate of the account
   * @param timestamp    the timestamp of the account creation
   * @return the created account
   */
  public Account addAccount(final String type, final String currency,
                            final double interestRate, final int timestamp) {
    Account account;

    switch (type) {
      case "savings" -> account = new SavingsAccount(this, currency, type, interestRate);
      case "business" -> account = new BusinessAccount(this, currency, type);
      default -> account = new Account(this, currency, type);
    }

    SimpleTransaction transaction = new SimpleTransaction.Builder()
            .timestamp(timestamp)
            .description("New account created")
            .build();

    account.addTransaction(transaction);

    accounts.add(account);

    return account;
  }

  /**
   * Gets the account by type and currency
   *
   * @param type     the type of the account
   * @param currency the currency of the account
   * @return the account with the given type and currency
   */
  public Account getAccountByTypeAndCurrency(final String type, final String currency) {
    return accounts.stream()
            .filter(account -> account.getType().equals(type)
                    && account.getCurrency().equals(currency))
            .findFirst()
            .orElse(null);
  }

  /**
   * Deletes the account if the balance is 0
   *
   * @param account   the account to be deleted
   * @param timestamp the timestamp of the account deletion
   * @throws Exception if the account is not found or if the balance is not 0
   */
  public void deleteAccount(final Account account, final int timestamp) throws Exception {
    if (account.getBalance() == 0) {
      if (accounts.contains(account)) {
        account.getCards().clear();
        accounts.remove(account);
      } else {
        throw new Exception("Account not found | Not this user's account");
      }
    } else {
      SimpleTransaction transaction = new SimpleTransaction.Builder()
              .timestamp(timestamp)
              .description("Account couldn't be deleted - there are funds remaining")
              .build();

      account.getTransactions().add(transaction);

      throw new Exception(
              "Account couldn't be deleted - see org.poo.transactions for details");
    }
  }

  /**
   * Get the age of the user
   *
   * @return the age of the user
   */
  public int getAge() {
    LocalDate parsedBirthDate = LocalDate.parse(birthDate, DateTimeFormatter.ISO_LOCAL_DATE);
    LocalDate currentDate = LocalDate.now();

    return Period.between(parsedBirthDate, currentDate).getYears();
  }

  @JsonIgnore
  public String getUsername() {
    return String.format("%s %s", lastName, firstName);
  }

  @JsonIgnore
  public boolean isStudent() {
    return occupation.equals("student");
  }

  @Override
  public String toString() {
    return String.format("%s - %d - %s", email, age, planStrategy.toString());
  }
}
