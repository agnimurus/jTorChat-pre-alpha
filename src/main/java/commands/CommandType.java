package commands;

public enum CommandType {
  PING("ping"),
  PONG("pong"),
  CLIENT("client"),
  VERSION("version"),
  PROFILE_NAME("profile_name"),
  PROFILE_TEXT("profile_text"),
  ADD_ME("add_me"),
  STATUS("status");

  private String commandName;

  CommandType(String commandName) {
    this.commandName = commandName;
  }

  @Override
  public String toString() {
    return commandName;
  }

  public String toDebugString() {
    return "CommandType{" +
        "commandName='" + commandName + '\'' +
        '}';
  }

  /**
   * Case-insensititve way to extract CommandType enum from a string value.
   *
   * @param value: A string representation of a CommandType constant
   * @return The corresponding CommandType constant
   * @throws IllegalArgumentException If no constant with a corresponding value is found
   */
  public static CommandType fromValue(String value) throws IllegalArgumentException {
    for (CommandType cmd : CommandType.values()) {
      if (cmd.toString().equalsIgnoreCase(value)) {
        return cmd;
      }
    }
    throw new IllegalArgumentException("No constant with value \"" + value + "\" found");
  }

}
