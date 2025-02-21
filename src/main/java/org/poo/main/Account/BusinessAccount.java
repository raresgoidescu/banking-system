package org.poo.main.Account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.main.Bank.MonetaryValue;
import org.poo.main.Bank.Transaction.OnlinePaymentTransaction;
import org.poo.main.Bank.Transaction.Transaction;
import org.poo.main.Entities.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.poo.main.Utils.Utils.info;
import static org.poo.main.Utils.Utils.warn;

@Getter
@Setter
public class BusinessAccount extends Account {
  private static final int DEPOSIT_LIMIT_DEFAULT = 500;
  private static final int SPENDING_LIMIT_DEFAULT = 500;

  @JsonIgnore
  private List<User> managers = new ArrayList<>();
  @JsonIgnore
  private List<User> employees = new ArrayList<>();

  @JsonIgnore
  private MonetaryValue depositLimit =
          new MonetaryValue(DEPOSIT_LIMIT_DEFAULT, "RON").convertTo(this.getCurrency());
  @JsonIgnore
  private MonetaryValue spendingLimit =
          new MonetaryValue(SPENDING_LIMIT_DEFAULT, "RON").convertTo(this.getCurrency());

  @JsonIgnore
  private Map<User, Double> spendingsByUser = new HashMap<>();
  @JsonIgnore
  private Map<User, Double> depositsByUser = new HashMap<>();

  @JsonIgnore
  private double totalDeposited = 0;
  @JsonIgnore
  private double totalSpent = 0;

  public BusinessAccount(final User owner, final String currency, final String type) {
    super(owner, currency, type);

    spendingsByUser.put(owner, 0.0);
    depositsByUser.put(owner, 0.0);
  }

  /**
   * Set the deposit limit for the business account.
   *
   * @param user   The user that wants to set the deposit limit.
   * @param amount The amount of money that the deposit limit will be set to.
   */
  public void setDepositLimit(final User user, final double amount) {
    if (isOwner(user)) {
      this.depositLimit = new MonetaryValue(amount, this.getCurrency());
    } else {
      throw new IllegalArgumentException(
              "You must be owner in order to change deposit limit.");
    }
  }

  /**
   * Set the spending limit for the business account.
   *
   * @param user   The user that wants to set the spending limit.
   * @param amount The amount of money that the spending limit will be set to.
   */
  public void setSpendingLimit(final User user, final double amount) {
    if (isOwner(user)) {
      this.spendingLimit = new MonetaryValue(amount, this.getCurrency());
    } else {
      throw new IllegalArgumentException(
              "You must be owner in order to change spending limit.");
    }
  }

  /**
   * Deposit money into the account and keep track of the total amount deposited.
   *
   * @param user          the user that wants to deposit
   * @param monetaryValue the amount to deposit
   * @throws Exception if the deposit limit is exceeded
   */
  @Override
  public void deposit(final User user, final MonetaryValue monetaryValue) throws Exception {
    MonetaryValue unconvertedMonetaryValue = monetaryValue.convertTo(this.getCurrency());

    if (isEmployee(user) && unconvertedMonetaryValue.getAmount() > depositLimit.getAmount()) {
      warn("Deposit limit exceeded.");
      throw new IllegalArgumentException("Deposit limit exceeded.");
    }

    super.deposit(unconvertedMonetaryValue);

    addDeposit(user, unconvertedMonetaryValue.getAmount());

    if (!user.equals(getOwner())) {
      totalDeposited += unconvertedMonetaryValue.getAmount();
    }

    info("User %s deposited %.2f %s", user.getUsername(),
            unconvertedMonetaryValue.getAmount(), unconvertedMonetaryValue.getCurrency());
  }

  /**
   * Withdraw money from the account and keep track of the total amount spent.
   *
   * @param user          the user that wants to withdraw
   * @param monetaryValue the amount to withdraw
   * @return the amount withdrawn
   * @throws Exception if the spending limit is exceeded
   */
  @Override
  public double withdraw(final User user, final MonetaryValue monetaryValue) throws Exception {
    MonetaryValue unconvertedMonetaryValue = monetaryValue.convertTo(this.getCurrency());

    if (isEmployee(user) && unconvertedMonetaryValue.getAmount() > spendingLimit.getAmount()) {
      warn("Spending limit exceeded.");
      throw new IllegalArgumentException("Spending limit exceeded.");
    }

    double withdrawn = super.withdraw(unconvertedMonetaryValue);

    addSpending(user, unconvertedMonetaryValue.getBeforeFeeOrCashback());

    if (!user.equals(getOwner())) {
      totalSpent += unconvertedMonetaryValue.getBeforeFeeOrCashback();
    }

    info("User %s withdrew %.2f %s", user.getUsername(), withdrawn,
            unconvertedMonetaryValue.getCurrency());

    return withdrawn;
  }

  /**
   * Add a spending to the account.
   *
   * @param user   the user that made the spending
   * @param amount the amount spent
   */
  public void addSpending(final User user, final double amount) {
    spendingsByUser.put(user, spendingsByUser.getOrDefault(user, 0.0) + amount);
  }

  /**
   * Add a deposit to the account.
   *
   * @param user   the user that made the deposit
   * @param amount the amount deposited
   */
  public void addDeposit(final User user, final double amount) {
    depositsByUser.put(user, depositsByUser.getOrDefault(user, 0.0) + amount);
  }

  /**
   * Add a manager to the business account.
   *
   * @param user the user to add as manager
   */
  public void addManager(final User user) {
    if (isAssociate(user)) {
      warn("User is already an associate.");
      return;
    }
    managers.add(user);
    spendingsByUser.put(user, 0.0);
    depositsByUser.put(user, 0.0);
  }

  /**
   * Check if a user is an associate of the business account.
   *
   * @param user the user to check
   * @return true if the user is an associate, false otherwise
   */
  public boolean isAssociate(final User user) {
    return (isOwner(user) || isEmployee(user) || isManager(user));
  }

  /**
   * Add an employee to the business account.
   *
   * @param user the user to add as employee
   */
  public void addEmployee(final User user) {
    if (isAssociate(user)) {
      warn("User is already an associate.");
      return;
    }
    employees.add(user);
    spendingsByUser.put(user, 0.0);
    depositsByUser.put(user, 0.0);
  }

  /**
   * Check if a user has access to the business account.
   *
   * @param user the user to check
   * @return true if the user has access, false otherwise
   */
  @Override
  public boolean checkAccess(final User user) {
    if (isManager(user) || isEmployee(user)) {
      return true;
    }

    return super.checkAccess(user);
  }

  /**
   * Generate a report for the business account.
   *
   * @param start the start of the report
   * @param end   the end of the report
   * @return the report
   * @throws Exception if the report type is invalid
   */
  @Override
  public ObjectNode generateReport(final int start, final int end) throws Exception {
    throw new Exception("Need to specify the type of report for a business account.");
  }

  /**
   * Generate a report for the business account.
   *
   * @param start the start of the report
   * @param end   the end of the report
   * @param type  the type of report
   * @return the report
   * @throws Exception if the report type is invalid
   */
  public ObjectNode generateReport(final int start, final int end,
                                   final String type) throws Exception {
    return switch (type) {
      case "transaction" -> generateTransactionReport(start, end);
      case "commerciant" -> generateMerchantReport(start, end);
      default -> throw new IllegalArgumentException("Invalid report type.");
    };
  }

  /**
   * Generate a transaction report for the business account.
   *
   * @param start the start of the report
   * @param end   the end of the report
   * @return the report
   */
  public ObjectNode generateTransactionReport(final int start, final int end) {
    ObjectMapper mapper = new ObjectMapper();

    // generate the report
    ObjectNode report = mapper.createObjectNode();
    report.put("statistics type", "transaction");
    report.put("IBAN", this.getIban());
    report.put("balance", this.getBalance());
    report.put("currency", this.getCurrency());
    report.put("deposit limit", this.depositLimit.convertTo(this.getCurrency()).getAmount());
    report.put("spending limit", this.spendingLimit.convertTo(this.getCurrency()).getAmount());

    // add the managers
    ArrayNode managersArray = mapper.createArrayNode();

    managers.forEach(manager -> {
      ObjectNode managerNode = mapper.createObjectNode();
      managerNode.put("username", manager.getUsername());
      managerNode.put("deposited", depositsByUser.getOrDefault(manager, 0.0));
      managerNode.put("spent", spendingsByUser.getOrDefault(manager, 0.0));
      managersArray.add(managerNode);
    });

    report.set("managers", managersArray);

    // add the employees
    ArrayNode employeesArray = mapper.createArrayNode();

    employees.forEach(employee -> {
      ObjectNode employeeNode = mapper.createObjectNode();
      employeeNode.put("username", employee.getUsername());
      employeeNode.put("deposited", depositsByUser.getOrDefault(employee, 0.0));
      employeeNode.put("spent", spendingsByUser.getOrDefault(employee, 0.0));
      employeesArray.add(employeeNode);
    });

    report.set("employees", employeesArray);

    report.put("total deposited", totalDeposited);
    report.put("total spent", totalSpent);

    return report;
  }

  /**
   * Generate a merchant report for the business account.
   *
   * @param start the start of the report
   * @param end   the end of the report
   * @return the report
   */
  public ObjectNode generateMerchantReport(final int start, final int end) {
    ObjectMapper mapper = new ObjectMapper();

    // generate the report
    ObjectNode report = mapper.createObjectNode();
    report.put("statistics type", "commerciant");
    report.put("IBAN", this.getIban());
    report.put("balance", this.getBalance());
    report.put("currency", this.getCurrency());
    report.put("deposit limit", this.depositLimit.convertTo(this.getCurrency()).getAmount());
    report.put("spending limit", this.spendingLimit.convertTo(this.getCurrency()).getAmount());

    @Getter
    class MerchantReport {
      @JsonProperty("commerciant")
      private String merchant;
      @JsonProperty("total received")
      private double totalSpent;
      private final List<String> employees = new ArrayList<>();
      private final List<String> managers = new ArrayList<>();

      MerchantReport(final String merchant) {
        this.merchant = merchant;
      }
    }

    // comparator to sort the merchant reports by name
    Comparator<MerchantReport> merchantComparator = Comparator.comparing(mr -> mr.merchant);
    Set<MerchantReport> merchantReports = new TreeSet<>(merchantComparator);

    List<Transaction> transactions = getTransactions();

    for (Transaction transaction : transactions) {
      if (transaction.getTimestamp() >= start && transaction.getTimestamp() <= end) {
        try {
          OnlinePaymentTransaction onlinePaymentTransaction =
                  (OnlinePaymentTransaction) transaction;
          String merchant = onlinePaymentTransaction.getMerchant();

          MerchantReport merchantReport =
                  merchantReports.stream().filter(mr -> mr.merchant.equals(merchant))
                          .findFirst().orElse(null);

          if (merchantReport == null) {
            merchantReport = new MerchantReport(merchant);
            merchantReports.add(merchantReport);
          }
          if (!isOwner(onlinePaymentTransaction.getUser())) {
            merchantReport.totalSpent += onlinePaymentTransaction.getAmount();
          }
          if (isManager(onlinePaymentTransaction.getUser())) {
            merchantReport.managers.add(
                    onlinePaymentTransaction.getUser().getUsername());
          } else if (isEmployee(onlinePaymentTransaction.getUser())) {
            merchantReport.employees.add(
                    onlinePaymentTransaction.getUser().getUsername());
          }
        } catch (Exception e) {
          warn(e.getMessage());
        }
      }
    }

    report.set("commerciants", mapper.valueToTree(merchantReports));

    return report;
  }

  private boolean isManager(final User user) {
    return managers.contains(user);
  }

  private boolean isEmployee(final User user) {
    return employees.contains(user);
  }

  private boolean isOwner(final User user) {
    return user.equals(getOwner());
  }
}
