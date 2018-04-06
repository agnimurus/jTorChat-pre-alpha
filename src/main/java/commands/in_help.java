package commands;

import core.Buddy;
import core.Language;
import gui.GuiChatWindow;
import util.ChatWindow;
import util.MessageType;

public class in_help {

  public static void command(Buddy buddy, String command, GuiChatWindow window) {

    String help;
    help = "\n" + Language.langtext[57] + "\n";
    help += "/page_disp site\n";
    help += "/page_send site\n";
    help += "/page site\n";
    help += "/log\n";

    ChatWindow.update_window(MessageType.PRIVATE, window, help, "", "", false);

  }
}
