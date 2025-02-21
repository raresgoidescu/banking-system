package org.poo.main.Bank.Commands.SpecificCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account.Account;
import org.poo.main.Bank.Bank;
import org.poo.main.Bank.Commands.Command;
import org.poo.main.Bank.OutputHelperClasses.Report;
import org.poo.main.Bank.OutputHelperClasses.ResponseOutput;
import org.poo.main.Bank.Transaction.Transaction;
import org.poo.main.Utils.Utils;

import java.util.List;

public class ReportCommand implements Command {
  private final Bank bank = Bank.getInstance(null);

  /**
   * Executes a reporting command based on the provided input. This method retrieves account
   * details using the account identifier (IBAN) from the input, generates a summary of account
   * balance and transactions within the specified time range,
   * and returns the result in a structured JSON format.
   * If the account is not found, it provides an appropriate error message in the output.
   */
  @Override
  public ObjectNode execute(final CommandInput input) {
    int startTimestamp = input.getStartTimestamp();
    int endTimestamp = input.getEndTimestamp();
    String iban = input.getAccount();
    Account account = bank.getAccount(iban);

    ObjectNode commandOutput = Utils.prepareCommandOutput(input);

    if (account == null) {
      ResponseOutput responseOutput = new ResponseOutput()
              .setDescription("Account not found")
              .setTimestamp(input.getTimestamp());

      commandOutput.set("output", MAPPER.valueToTree(responseOutput));

      return commandOutput;
    }

    Report report = new Report()
            .setIban(iban)
            .setCurrency(account.getCurrency())
            .setBalance(account.getBalance());

    List<Transaction> transactions = bank.getAccount(iban).getTransactions();

    transactions.stream()
            .filter(transaction -> transaction.getTimestamp() >= startTimestamp
                    && transaction.getTimestamp() <= endTimestamp)
            .forEach(report::addTransaction);

    commandOutput.set("output", MAPPER.valueToTree(report));

    return commandOutput;
  }
}
