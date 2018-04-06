package commands;

import core.Buddy;
import gui.GuiChatWindow;
import util.ChatWindow;
import util.MessageType;

public class out_pa {

  public static void command(Buddy buddy, String command, GuiChatWindow window, boolean withDelay) {
    ChatWindow
        .update_window(MessageType.RECEIVE_PAGE, window, command.substring(4), "", "", withDelay);
  }
}
