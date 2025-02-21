package org.poo.main.Bank.Commands.SpecificCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account.Account;
import org.poo.main.Bank.Bank;
import org.poo.main.Bank.Commands.Command;
import org.poo.main.Bank.OutputHelperClasses.ResponseOutput;
import org.poo.main.Bank.OutputHelperClasses.SpendingsReport;
import org.poo.main.Bank.Transaction.OnlinePaymentTransaction;
import org.poo.main.Bank.Transaction.Transaction;
import org.poo.main.Utils.Utils;

import java.util.List;

public class SpendingsReportCommand implements Command {
  private final Bank bank = Bank.getInstance(null);

  /**
   * Executes the spending report command based on the input parameters.
   * Generates a spending report for a given account within a specified time range.
   * If the account does not exist or is of type "savings", appropriate error responses
   * are included in the output.
   */
  @Override
  public ObjectNode execute(final CommandInput input) {
    int startTimestamp = input.getStartTimestamp();
    int endTimestamp = input.getEndTimestamp();
    String iban = input.getAccount();
    Account target = bank.getAccount(iban);

    ObjectNode commandMapper = Utils.prepareCommandOutput(input);

    if (target == null) {
      ResponseOutput responseOutput = new ResponseOutput()
              .setDescription("Account not found")
              .setTimestamp(input.getTimestamp());

      commandMapper.set("output", MAPPER.valueToTree(responseOutput));

      return commandMapper;
    }

    if (target.getType().equals("savings")) {
      ResponseOutput responseOutput = new ResponseOutput()
              .setError("This kind of report is not supported for a saving account");

      commandMapper.set("output", MAPPER.valueToTree(responseOutput));

      return commandMapper;
    }

    SpendingsReport spendingsReport = (SpendingsReport) new SpendingsReport()
            .setIban(iban)
            .setBalance(target.getBalance())
            .setCurrency(target.getCurrency());

    List<Transaction> transactions = target.getTransactions();

    transactions.stream()
            .filter(transaction ->
                    transaction.getTimestamp() >= startTimestamp
                            && transaction.getTimestamp() <= endTimestamp)
            .filter(transaction -> transaction instanceof OnlinePaymentTransaction)
            .map(transaction -> (OnlinePaymentTransaction) transaction)
            .filter(transaction -> transaction.getMerchant() != null)
            .forEach(spendingsReport::addTransaction);

    spendingsReport.setMerchants();

    commandMapper.set("output", MAPPER.valueToTree(spendingsReport));

    return commandMapper;
  }
}
