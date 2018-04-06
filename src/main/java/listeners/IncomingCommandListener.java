package listeners;

import core.Buddy;
import java.io.InputStream;


public interface IncomingCommandListener {

  public void onCommand(Buddy buddy, String command, InputStream is);
}
