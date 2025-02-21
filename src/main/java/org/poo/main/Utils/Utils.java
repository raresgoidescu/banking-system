package org.poo.main.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;

public final class Utils {
  public static final class Debug {
    @Getter
    @Setter
    private static boolean enabled = false;

    private Debug() {
      throw new IllegalStateException("Utility class");
    }
  }

  private Utils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Prints an info message to the console.
   *
   * @param message the message to be printed
   */
  public static void info(final String message) {
    if (Debug.enabled) {
      System.out.println("[i]: " + message);
    }
  }

  /**
   * Prints an info message to the console like printf
   *
   * @param message the message to be printed
   * @param args    the arguments to be formatted
   */
  public static void info(final String message, final Object... args) {
    if (Debug.enabled) {
      System.out.printf("[i]: " + message + "\n", args);
    }
  }

  /**
   * Prints an error message to the console.
   *
   * @param message the message to be printed
   */
  public static void err(final String message) {
    System.out.println("[-]: " + message);
  }

  /**
   * Prints an error message to the console.
   *
   * @param message the message to be printed
   * @param args    the arguments to be formatted
   */
  public static void err(final String message, final Object... args) {
    System.out.printf("[-]: " + message + "\n", args);
  }

  /**
   * Prints a success message to the console.
   *
   * @param message the message to be printed
   */
  public static void success(final String message) {
    if (Debug.enabled) {
      System.out.println("[+]: " + message);
    }
  }

  /**
   * Prints a success message to the console.
   *
   * @param message the message to be printed
   * @param args    the arguments to be formatted
   */
  public static void success(final String message, final Object... args) {
    if (Debug.enabled) {
      System.out.printf("[+]: " + message + "\n", args);
    }
  }

  /**
   * Prints a warning message to the console.
   *
   * @param message the message to be printed
   */
  public static void warn(final String message) {
    if (Debug.enabled) {
      System.out.println("[!]: " + message);
    }
  }

  /**
   * Prints a warning message to the console.
   *
   * @param message the message to be printed
   * @param args    the arguments to be formatted
   */
  public static void warn(final String message, final Object... args) {
    if (Debug.enabled) {
      System.out.printf("[!]: " + message + "\n", args);
    }
  }

  /**
   * Prepares the command output with the command and timestamp.
   *
   * @param commandInput the command input
   *                     containing the command and timestamp
   *                     to be added to the output
   * @return the command output
   */
  public static ObjectNode prepareCommandOutput(final CommandInput commandInput) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode commandOutput = mapper.createObjectNode();

    commandOutput.put("command", commandInput.getCommand());
    commandOutput.put("timestamp", commandInput.getTimestamp());

    return commandOutput;
  }
}
