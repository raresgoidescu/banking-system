package org.poo.main.Bank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;
import org.poo.fileio.ExchangeInput;
import org.poo.fileio.UserInput;
import org.poo.main.Account.Account;
import org.poo.main.Account.Card.Card;
import org.poo.main.Account.MerchantAccount;
import org.poo.main.Bank.Commands.CommandFactory;
import org.poo.main.Bank.OutputHelperClasses.ResponseOutput;
import org.poo.main.Entities.Merchant;
import org.poo.main.Entities.User;
import org.poo.main.Utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.poo.main.Utils.Utils.info;

public final class Bank {
  // A graph that holds all the known currencies, used to determine
  // the exchangeRate between two different currencies
  private final ExchangeGraph exchangeGraph = ExchangeGraph.getInstance();

  // A *global* (I still don't understand why would I do this) list of aliases
  @Getter
  private final List<String> aliases = new ArrayList<>();

  @Getter
  private final List<User> users = new ArrayList<>();
  private final List<Merchant> merchants = new ArrayList<>();

  // Maps that provide easy access to objects (Account, Card, User)
  // based on the provided identification in the input (IBAN, cardNumber, email)
  private final Map<String, Account> ibanToAccount = new HashMap<>();
  private final Map<String, Card> numberToCard = new HashMap<>();
  private final Map<String, User> emailToUser = new HashMap<>();
  private final Map<String, Merchant> nameToMerchant = new HashMap<>();

  // Global mapper to reduce duplicate code
  private final ObjectMapper mapper = new ObjectMapper();
  // Global ArrayNode used to map the output
  private final ArrayNode output;

  private static volatile Bank instance;
  private final CommandFactory commandFactory = new CommandFactory();

  private Bank(final ArrayNode output) {
    this.output = output;
  }

  /**
   * Provides a thread-safe singleton instance of the Bank class.
   * If no instance exists, it initializes a new one with the given output.
   *
   * @return the singleton instance of the Bank class
   */
  public static Bank getInstance(final ArrayNode output) {
    if (instance == null) {
      synchronized (Bank.class) {
        if (instance == null) {
          instance = new Bank(output);
        }
      }
    }
    return instance;
  }

  /**
   * Resets the Singleton instance of the Bank class.
   */
  public static void resetInstance() {
    instance = null;
  }

  /**
   * Handles the execution of a given command using the provided commandInput.
   * The command is processed through a command factory, and its result, if not null,
   * is added to the output.
   */
  public void handleCommand(final CommandInput commandInput) {
    String command = commandInput.getCommand();

    String text = String.format("[%04d] %s ", commandInput.getTimestamp(), command);
    info("\033[1;32m" + text + "\033[0m");

    ObjectNode result = commandFactory.handleCommand(command, commandInput);

    if (result != null) {
      output.add(result);
    }
  }

  /**
   * Generates and adds a command output stating that the related account is not
   * a savings account.
   */
  public void notSavingsAccountOutput(final CommandInput commandInput) {
    ObjectNode commandOutput = Utils.prepareCommandOutput(commandInput);

    ResponseOutput responseOutput = new ResponseOutput().
            setDescription("This is not a savings account").
            setTimestamp(commandInput.getTimestamp());

    commandOutput.set("output", mapper.valueToTree(responseOutput));

    output.add(commandOutput);
  }

  /**
   * Adds multiple users to the system based on the provided array of UserInput objects.
   */
  public void addUsers(final UserInput[] userInputs) {
    Arrays.stream(userInputs).map(User::new).forEach(user -> {
      users.add(user);
      emailToUser.put(user.getEmail(), user);
    });
  }

  /**
   * Adds multiple currency exchange rates to the exchange graph.
   */
  public void addExchangeRates(final ExchangeInput[] exchangeInputs) {
    Arrays.stream(exchangeInputs).forEach(exchangeInput -> exchangeGraph.addExchange(
            exchangeInput.getFrom(),
            exchangeInput.getTo(),
            exchangeInput.getRate()
    ));
  }

  /**
   * Adds multiple merchants to the database.
   */
  public void addMerchants(final CommerciantInput[] merchantInputs) {
    Arrays.stream(merchantInputs).map(Merchant::new).forEach(merchant -> {
      Account account = new MerchantAccount(merchant);

      merchants.add(merchant);
      ibanToAccount.put(merchant.getAccount(), account);
      nameToMerchant.put(merchant.getName(), merchant);
    });
  }

  /**
   * Retrieves the Account object associated with the specified IBAN.
   *
   * @return the Account object associated with the specified IBAN,
   * or null if no account is found
   */
  public Account getAccount(final String iban) {
    return ibanToAccount.get(iban);
  }

  /**
   * Adds the specified account to the Bank's internal mapping of IBANs to Account objects.
   */
  public void addAccount(final Account account) {
    ibanToAccount.put(account.getIban(), account);
  }

  /**
   * Adds an account to the internal mapping of aliases to accounts.
   */
  public void addAccount(final String alias, final Account account) {
    ibanToAccount.put(alias, account);
  }

  /**
   * Removes the specified account from the internal mapping of IBANs to accounts.
   */
  public void removeAccount(final Account account) {
    ibanToAccount.remove(account.getIban());
  }

  /**
   * Retrieves a Card object associated with the specified card number.
   *
   * @return the Card object associated with the specified card number,
   * or null if no card is found
   */
  public Card getCard(final String number) {
    return numberToCard.get(number);
  }

  /**
   * Adds a card to the system's internal mapping of card numbers to card objects.
   */
  public void addCard(final Card card) {
    numberToCard.put(card.getCardNumber(), card);
  }

  /**
   * Removes the specified card from the system's
   * internal mapping of card numbers to card objects.
   */
  public void removeCard(final Card card) {
    numberToCard.remove(card.getCardNumber());
  }

  /**
   * Retrieves the merchant associated with the specified name.
   *
   * @param name the name of the merchant to retrieve
   * @return the Merchant object associated with the provided name, or null if no merchant found
   */
  public Merchant getMerchant(final String name) {
    return nameToMerchant.get(name);
  }

  /**
   * Retrieves the user associated with the specified email address.
   *
   * @return the User object associated with the provided email, or null if no user is found
   */
  public User getUser(final String email) {
    return emailToUser.get(email);
  }
}
