package org.poo.main.Bank.Commands.SpecificCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Account.Account;
import org.poo.main.Bank.Bank;
import org.poo.main.Bank.Commands.Command;
import org.poo.main.Bank.Transaction.Transaction;
import org.poo.main.Entities.User;
import org.poo.main.Utils.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PrintTransactionsCommand implements Command {
  private final Bank bank = Bank.getInstance(null);

  /**
   * The command is executed by getting the user's email from the input object
   * and then getting the user from the bank. Then, the user's accounts are
   * retrieved and the transactions of each account are added to a list. The
   * transactions are then sorted by timestamp and added to the output object.
   *
   * @param input CommandInput object with the command and the user's email
   * @return ObjectNode with the command and the transactions of the user
   */
  @Override
  public ObjectNode execute(final CommandInput input) {
    User user = bank.getUser(input.getEmail());
    List<Account> accounts = user.getAccounts();

    Comparator<Transaction> transactionComparator =
            Comparator.comparingInt(Transaction::getTimestamp);
    List<Transaction> transactions = new ArrayList<>();

    accounts.forEach(account -> transactions.addAll(account.getTransactions()));

    transactions.sort(transactionComparator);

    ObjectNode commandMapper = Utils.prepareCommandOutput(input);

    commandMapper.set("output", MAPPER.valueToTree(transactions));

    return commandMapper;
  }
}
