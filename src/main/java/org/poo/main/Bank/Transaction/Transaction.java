package org.poo.main.Bank.Transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Transaction {
  private int timestamp;
  private String description;
  private String error;

  protected Transaction(final Builder<?> builder) {
    this.timestamp = builder.timestamp;
    this.description = builder.description;
    this.error = builder.error;
  }

  public abstract static class Builder<T extends Builder<T>> {
    private int timestamp;
    private String description;
    private String error;

    /**
     * Sets the timestamp of the Transaction object
     */
    public T timestamp(final int ts) {
      this.timestamp = ts;
      return self();
    }

    /**
     * Sets the description of the Transaction object
     */
    public T description(final String desc) {
      this.description = desc;
      return self();
    }

    /**
     * Sets the error message of the Transaction object
     */
    public T error(final String err) {
      this.error = err;
      return self();
    }

    protected abstract T self();

    /**
     * Builds the Transaction object
     *
     * @return Transaction object
     */
    public abstract Transaction build();
  }

  /**
   * @return String representation of the Transaction object
   */
  @Override
  public String toString() {
    return "Transaction{"
            + "timestamp=" + timestamp
            + ", description='" + description + '\''
            + ", error='" + error + '\''
            + '}';
  }
}
