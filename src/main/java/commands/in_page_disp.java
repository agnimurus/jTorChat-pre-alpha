package commands;


import core.Buddy;
import gui.GuiChatWindow;
import util.ChatWindow;
import util.MessageType;


public class in_page_disp {

  public static void command(Buddy buddy, String command, GuiChatWindow window) {

    if (command.length() < 12) {// When nothing is choosen use index
      command = "/page_disp index";
    }

    command = "/page_disp " + command.substring(11)
        .replaceAll("[^a-zA-Z]", ""); // Replace all special letters

    String msg = util_page.read(command.substring(11));
    if (msg == "")//See comment T1 in ChatWindow
    {
      ChatWindow.update_window(MessageType.PRIVATE, window,
          "The page '" + command.substring(11) + "' do not exist", "", "", false);
    } else {
      ChatWindow
          .update_window(MessageType.PRIVATE, window, "Get " + command.substring(11) + msg, "", "",
              false);
    }

  }
}
