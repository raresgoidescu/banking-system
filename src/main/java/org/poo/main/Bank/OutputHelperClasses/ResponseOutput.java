package org.poo.main.Bank.OutputHelperClasses;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Represents a response output object used for generating responses in command executions.
 * This class is designed to hold information such as success messages, errors, descriptions,
 * status, and timestamps related to a specific operation or command execution.
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public final class ResponseOutput {
  private String description;
  private String success;
  private String status;
  private String error;
  private int timestamp;

  public ResponseOutput() {
  }

  /**
   * Sets the success message for the response object.
   *
   * @param text the success message to be set
   * @return the current instance of the ResponseOutput class
   */
  public ResponseOutput setSuccess(final String text) {
    this.success = text;
    return this;
  }

  /**
   * Sets an error message for the response object.
   *
   * @param text the error message to be set
   * @return the current instance of the ResponseOutput class
   */
  public ResponseOutput setError(final String text) {
    this.error = text;
    return this;
  }

  /**
   * Sets the description for the response object.
   *
   * @param text the description to be set
   * @return the current instance of the ResponseOutput class
   */
  public ResponseOutput setDescription(final String text) {
    this.description = text;
    return this;
  }

  /**
   * Sets the status for the response object.
   *
   * @param text the status text to be set
   * @return the current instance of the ResponseOutput class
   */
  public ResponseOutput setStatus(final String text) {
    this.status = text;
    return this;
  }

  /**
   * Sets the timestamp for the response object.
   *
   * @param value the timestamp to be set in the response object
   * @return the current instance of the ResponseOutput class
   */
  public ResponseOutput setTimestamp(final int value) {
    this.timestamp = value;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public String getSuccess() {
    return success;
  }

  public String getStatus() {
    return status;
  }

  public String getError() {
    return error;
  }

  public int getTimestamp() {
    return timestamp;
  }
}
