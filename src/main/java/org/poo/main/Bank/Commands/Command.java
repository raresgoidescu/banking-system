package org.poo.main.Bank.Commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;

/**
 * Implementations of this interface define specific command behaviors and
 * logic that interact with various components of the bank system.
 */
public interface Command {
  ObjectMapper MAPPER = new ObjectMapper();

  /**
   * Executes a specific command based on the provided input.
   *
   * @return a JSON object containing the result of the command execution, which
   * may include the command metadata, output data, and other relevant
   * information specific to the executed command.
   */
  ObjectNode execute(CommandInput input);
}
