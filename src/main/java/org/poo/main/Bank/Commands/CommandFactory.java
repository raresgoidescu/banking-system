package org.poo.main.Bank.Commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.Bank.Commands.SpecificCommands.AcceptSplitPaymentCommand;
import org.poo.main.Bank.Commands.SpecificCommands.AddAccountCommand;
import org.poo.main.Bank.Commands.SpecificCommands.AddFundsCommand;
import org.poo.main.Bank.Commands.SpecificCommands.AddInterestCommand;
import org.poo.main.Bank.Commands.SpecificCommands.AddNewBusinessAssociateCommand;
import org.poo.main.Bank.Commands.SpecificCommands.BusinessReportCommand;
import org.poo.main.Bank.Commands.SpecificCommands.CashWithdrawalCommand;
import org.poo.main.Bank.Commands.SpecificCommands.ChangeDepositLimitCommand;
import org.poo.main.Bank.Commands.SpecificCommands.ChangeInterestRateCommand;
import org.poo.main.Bank.Commands.SpecificCommands.ChangeSpendingLimitCommand;
import org.poo.main.Bank.Commands.SpecificCommands.CheckCardStatusCommand;
import org.poo.main.Bank.Commands.SpecificCommands.CreateCardCommand;
import org.poo.main.Bank.Commands.SpecificCommands.DeleteAccountCommand;
import org.poo.main.Bank.Commands.SpecificCommands.DeleteCardCommand;
import org.poo.main.Bank.Commands.SpecificCommands.PayOnlineCommand;
import org.poo.main.Bank.Commands.SpecificCommands.PrintTransactionsCommand;
import org.poo.main.Bank.Commands.SpecificCommands.PrintUsersCommand;
import org.poo.main.Bank.Commands.SpecificCommands.RejectSplitPaymentCommand;
import org.poo.main.Bank.Commands.SpecificCommands.ReportCommand;
import org.poo.main.Bank.Commands.SpecificCommands.SendMoneyCommand;
import org.poo.main.Bank.Commands.SpecificCommands.SetAliasCommand;
import org.poo.main.Bank.Commands.SpecificCommands.SetMinBalanceCommand;
import org.poo.main.Bank.Commands.SpecificCommands.SpendingsReportCommand;
import org.poo.main.Bank.Commands.SpecificCommands.SplitPaymentCommand;
import org.poo.main.Bank.Commands.SpecificCommands.UpgradePlanCommand;
import org.poo.main.Bank.Commands.SpecificCommands.WithdrawSavingsCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.poo.main.Utils.Utils.err;

/**
 * The CommandFactory class is responsible for creating and managing a mapping of command strings
 * to their corresponding execution logic. It provides a mechanism to handle various operations
 * represented by different command strings, delegating the execution to specific command classes.
 */
public class CommandFactory {
  private final Map<String, Function<CommandInput, ObjectNode>> commandMap = new HashMap<>();

  /* Lambda expressions yay */

  /**
   * Constructs a CommandFactory instance and initializes the mapping of command strings
   * to their respective execution logic.
   */
  public CommandFactory() {
    commandMap.put("printUsers", input ->
            new PrintUsersCommand().execute(input));
    commandMap.put("printTransactions", input ->
            new PrintTransactionsCommand().execute(input));
    commandMap.put("addAccount", input ->
            new AddAccountCommand().execute(input));
    commandMap.put("addFunds", input ->
            new AddFundsCommand().execute(input));
    commandMap.put("createCard", input ->
            new CreateCardCommand().execute(input));
    commandMap.put("createOneTimeCard", input ->
            new CreateCardCommand().execute(input));
    commandMap.put("deleteAccount", input ->
            new DeleteAccountCommand().execute(input));
    commandMap.put("deleteCard", input ->
            new DeleteCardCommand().execute(input));
    commandMap.put("setMinBalance", input ->
            new SetMinBalanceCommand().execute(input));
    commandMap.put("checkCardStatus", input ->
            new CheckCardStatusCommand().execute(input));
    commandMap.put("payOnline", input ->
            new PayOnlineCommand().execute(input));
    commandMap.put("sendMoney", input ->
            new SendMoneyCommand().execute(input));
    commandMap.put("setAlias", input ->
            new SetAliasCommand().execute(input));
    commandMap.put("splitPayment", input ->
            new SplitPaymentCommand().execute(input));
    commandMap.put("addInterest", input ->
            new AddInterestCommand().execute(input));
    commandMap.put("changeInterestRate", input ->
            new ChangeInterestRateCommand().execute(input));
    commandMap.put("report", input ->
            new ReportCommand().execute(input));
    commandMap.put("spendingsReport", input ->
            new SpendingsReportCommand().execute(input));
    commandMap.put("withdrawSavings", input ->
            new WithdrawSavingsCommand().execute(input));
    commandMap.put("upgradePlan", input ->
            new UpgradePlanCommand().execute(input));
    commandMap.put("cashWithdrawal", input ->
            new CashWithdrawalCommand().execute(input));
    commandMap.put("acceptSplitPayment", input ->
            new AcceptSplitPaymentCommand().execute(input));
    commandMap.put("rejectSplitPayment", input ->
            new RejectSplitPaymentCommand().execute(input));
    commandMap.put("addNewBusinessAssociate", input ->
            new AddNewBusinessAssociateCommand().execute(input));
    commandMap.put("changeSpendingLimit", input ->
            new ChangeSpendingLimitCommand().execute(input));
    commandMap.put("changeDepositLimit", input ->
            new ChangeDepositLimitCommand().execute(input));
    commandMap.put("businessReport", input ->
            new BusinessReportCommand().execute(input));
  }

  /**
   * Processes a given command by delegating it to the appropriate command logic
   * based on the provided command string.
   */
  public final ObjectNode handleCommand(final String command, final CommandInput commandInput) {
    /* Find the appropriate command based on the command string */
    Function<CommandInput, ObjectNode> commandFunction = commandMap.get(command);

    if (commandFunction != null) {
      return commandFunction.apply(commandInput);
    } else {
      err("\u001B[1;31m[%04d] unknown command: %s\u001B[0m\n",
              commandInput.getTimestamp(), command);
      return null;
    }
  }
}
