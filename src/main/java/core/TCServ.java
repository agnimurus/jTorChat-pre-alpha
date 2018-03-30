package core;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;


public class TCServ {
	private static ServerSocket serverSock;
	protected static boolean running = true;
  private static final Object initLock = new Object();

	public static void init() {

		ThreadManager.registerWork(ThreadManager.DAEMON, () -> {
			try {
				try {
					serverSock = new ServerSocket(Config.getLocalPort());
				} catch (IOException e) {
					Logger.log(Logger.FATAL, "TCServ", "Failed to start local server: " + e.getLocalizedMessage());
					return;
				}

				synchronized(initLock) {
					initLock.notifyAll();
				}
				while (serverSock.isBound() && !serverSock.isClosed() && running) {

					final Socket sock = serverSock.accept();
					if (!running) {//Won't this always be false here?
						sock.close();
						return;
					}

					// Should this really be in a while loop?
					ThreadManager.registerWork(ThreadManager.NORMAL, () -> {
            try {
              //Are we sure there shouldnt be a dash in "UTF8"? Lets make it a constant
              Scanner sc = new Scanner(new InputStreamReader(sock.getInputStream(), "UTF8"));
              sc.useDelimiter("\\n");//two '\'s?
              String line = sc.next();
              if (line == null) {
                Logger.log(Logger.SEVERE, "TCServ", "wtf");
                sock.close();
                sc.close();
                return;
              }
              if (!line.startsWith("ping ")) {
                Logger.log(Logger.SEVERE, "TCServ", line + " doesnt start with ping?!");
                sock.close();
                sc.close();
                return;
              }
              if (BuddyList.buds.containsKey(line.split(" ")[1])) { // TODO add check to see if its different cookie from last time
                Logger.log(Logger.INFO, "TCServ", "Got ping from " + line.split(" ")[1] + " with cookie " + line.split(" ")[2]);
                Buddy buddy = BuddyList.buds.get(line.split(" ")[1]);
                Logger.log(Logger.INFO, "TCServ", "Match " +  line.split(" ")[1] + " to " + buddy.getAddress());
                buddy.setTheirCookie(line.split(" ")[2]);
                if (buddy.getOurSock() == null)
                  buddy.connect();
                else if (buddy.getOurSockOut() != null)
                  try {
                    buddy.sendPong(line.split(" ")[2]); // TODO FIXME URGENT check if not connected!
                  } catch (SocketException se) {
                    // ignored
                  } catch (IOException ioe) {
                    ioe.printStackTrace();
                  }
                buddy.attach(sock, sc);
              } else {

                if (line.split(" ")[1].length() == 16)  // first defend for flooding ping
                {
                  if (!line.split(" ")[1].equals(Config.getUs())){
                    Buddy buddy = new Buddy(line.split(" ")[1], null, false);

                    Logger.log(Logger.INFO, "TCServ", "Got ping from unknown address " + line.split(" ")[1] + " with cookie " + line.split(" ")[2]);
                    buddy.setTheirCookie(line.split(" ")[2]);
                    if (buddy.getOurSock() == null)
                      buddy.connect();
                    else
                      buddy.sendPong(line.split(" ")[2]); // TODO FIXME URGENT check if not connected!
                    buddy.attach(sock, sc);
                  }
                }


                return;
              }
            } catch (SocketException se) {
              // ignored
              try {
                sock.close();
              } catch (IOException e1) {
                e1.printStackTrace();
              }
            } catch (Exception e) {
              e.printStackTrace();
              try {
                sock.close();
              } catch (IOException e1) {
                e1.printStackTrace();
              }
            }
          }, null, null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, "Starting local server on " + Config.getLocalPort() + ".", "Server thread");

		try {
			synchronized(initLock) {
				initLock.wait();
			}
		} catch (InterruptedException e) {
			//TODO remove or do something
		}
		return;
	}

	public static void halt() {
		running = false;
		try {
			if (serverSock != null) {
				serverSock.close();
				Logger.log(Logger.NOTICE, "TCServ", "Terminated.");
			} else
				Logger.log(Logger.SEVERE, "TCServ", "ss == null!!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
