package commands;

import core.Buddy;
import gui.GuiChatWindow;
import util.ChatWindow;
import util.LogWriter;
import util.MessageType;

public class in_log {

  public static void command(Buddy buddy, String command, GuiChatWindow window) {

    String filename;
    String buddyName = buddy.getName();
    if (buddyName == null || buddyName.equals("")) {
      buddyName = buddy.getAddress();
    }
    filename = LogWriter.logWrite(window.get_textPane1().getText(), 1, buddyName);
    ChatWindow.update_window(MessageType.PRIVATE, window,
        "This Chat log here " + filename + " in the Log Folder.", "", "", false);
  }
}
