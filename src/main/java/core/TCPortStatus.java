package core;

public enum TCPortStatus {
  AVAILABLE("available"), // int value 1 if former codebase
  AWAY("away"), // int value 2
  XA("xa"), // int value 3
  STATUS_SENT("status_sent"); // referred to as int value 0 FIXME This is a placeholder. Not sure if needed.

  private String status;

  TCPortStatus(String status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return status;
  }

  /**
   * Debugging toString representation
   * @return a verbose String representation of the TCPortStatus constant
   */
  public String toDebugString() {
    return "TCPortStatus{" +
        "status='" + status + '\'' +
        '}';
  }

  /**
   * Case-insensititve way to extract TCPortStatus enum from a string value.
   * @param value: A string representation of a TCPortStatus constant
   * @return The corresponding TCPortStatus constant
   * @throws IllegalArgumentException If no constant with a corresponding value is found
   */
  public static TCPortStatus fromValue(String value) throws IllegalArgumentException{
    for (TCPortStatus status : TCPortStatus.values()) {
      if (status.toString().equalsIgnoreCase(value))
        return status;
    }

    throw new IllegalArgumentException("No constant with value \"" + value + "\" found");
  }
}
