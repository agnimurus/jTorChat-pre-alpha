package commands;

public enum Command {
  PING("ping");

  private String commandName;

  Command(String commandName) {
    this.commandName = commandName;
  }

  @Override
  public String toString() {
    return commandName;
  }

  public String toDebugString() {
    return "Command{" +
        "commandName='" + commandName + '\'' +
        '}';
  }

  /**
   * Case-insensitive way to extract Command enum from a string value.
   * @param value: A string representation of a Command constant
   * @return The corresponding Command constant
   * @throws IllegalArgumentException If no constant with a corresponding value is found
   */
  public static Command fromValue(String value) throws IllegalArgumentException {
    for (Command cmd : Command.values()) {
      if (cmd.toString().equalsIgnoreCase(value))
        return cmd;
    }
    throw new IllegalArgumentException("No constant with value \"" + value + "\" found");
  }

}
