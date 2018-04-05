package core;

import gui.Gui;
import gui.GuiLog;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import util.Status;


public class TCPort {

  // Direct compile and run from source for fixing the path
  public static String profile_name;
  public static String profile_text;
  private static TCPortStatus status = TCPortStatus.AVAILABLE;
  public static String[] externalSourcePath;
  private static boolean launched;

  @SuppressWarnings("unused")
  private static boolean halted;


  public static void main(String[] args) {
    GuiLog.initLogInstance();
    externalSourcePath = args;

    try {

      if (Config.getVisiblelog() == 1) {
        GuiLog.getGuiLog().setVisible(true);
      }

      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        Logger.setOverride(true);
        Logger.stopGLog();
        Logger.log(Logger.INFO, "Shutdown", "Starting...");
        // for (Buddy b : BuddyList.buds.values()) {
        // try {
        // b.disconnect();
        // } catch (IOException e) {
        // System.err.println("Error disconnecting " + b.getAddress() + ": " + e.getLocalizedMessage());
        // }
        // }
        TorLoader.cleanUp();
        try {
          if (launched) {
            BuddyList.saveBuddies();
          }
          BuddyList.disconnect_all();
        } catch (IOException e) {
          //TODO Log exception
          e.printStackTrace();
        }
      }));

      if (Config.getLoadTor() == 1) {
        TorLoader.loadTor();
      }

      TCServ.init();

      new Gui().init();

      launched = true;
      try {
        BuddyList.loadBuddies();
      } catch (FileNotFoundException fnfe) {
        fnfe.printStackTrace();
        // no buddylist file
      }

      if (Config.getUs() == null || Config.getUs().length() != 16) {
        Logger.log(Logger.FATAL, "TCPort", "Config.us: " + Config.getUs() + " is invalid.");
        JTextField jtf = new JTextField();
        jtf.setEditable(false);
        jtf.setText("Config.us: " + Config.getUs() + " is invalid.");
        JOptionPane.showMessageDialog(null, jtf, "Fatal Error", JOptionPane.PLAIN_MESSAGE);
        // System.exit(-1);
      }

      new fileTransfer.FileTransfer();

      ThreadManager.registerWork(ThreadManager.DAEMON, () -> {
        try {
          Scanner scanner = new Scanner(System.in);
          try {
            while (scanner.hasNextLine()) {
              String nextLine = scanner.nextLine();
              if (nextLine.startsWith("tskill ")) { // kills theirsock of the buddy following tskill
                BuddyList.buds.get(nextLine.split(" ")[1]).getTheirSock().close();
                BuddyList.buds.get(nextLine.split(" ")[1])
                    .setTheirSock(null);//FIXME is this necessary?
              } else if (nextLine
                  .startsWith("oskill ")) { // kills oursock of the buddy following oskill
                BuddyList.buds.get(nextLine.split(" ")[1]).getOurSock().close();
                BuddyList.buds.get(nextLine.split(" ")[1])
                    .setOurSock(null);//FIXME is this necessary (since we're closing our sock?
              } else if (nextLine.startsWith("msg ")) { // send messaage to a buddy
                BuddyList.buds.get(nextLine.split(" ")[1]).sendMessage(nextLine.split(" ", 3)[2]);
              } else if (nextLine.startsWith("raw ")) { // send raw messaage to a buddy
                BuddyList.buds.get(nextLine.split(" ")[1]).sendRaw(nextLine.split(" ", 3)[2]);
              }
            }
          } catch (Exception ex) {
            ex.printStackTrace();
          }
          scanner.close();
        } catch (Exception stackTrace) {
          stackTrace.printStackTrace();
        }
      }, "Starting console.", "Console thread");

      ThreadManager.registerWork(ThreadManager.DAEMON, () -> {
        while (true) {
          try {

            if (!Config.getNowstart().equals("")) { //Is this supposed to be .equals() instead?
              BuddyList.loadBuddiesRemote(Config.getNowstart());
              Config.setNowstart("");//TODO why is this necessary - 4/4?
            }

            if (!Config.getNowstartupdate().equals("")) {
              Config.setLastCheck(Update.loadUpdate(Config.getNowstartupdate()));

              if (!Config.getLastCheck().equals("close")) {
                JTextField jtf = new JTextField();
                jtf.setEditable(false);
                jtf.setText(Config.getLastCheck());
                JOptionPane.showMessageDialog(null, jtf, "Update Check", JOptionPane.PLAIN_MESSAGE);
              }

              Config.setNowstartupdate("");
            }

            for (Buddy buddy : BuddyList.buds.values()) {
              if (buddy.getConnectTime() != -1
                  && System.currentTimeMillis() - buddy.getConnectTime()
                  > Config.CONNECT_TIMEOUT * 1000) {
                // checks if buddy hasnt finished connecting within CONNECT_TIMEOUT seconds
                // if it hasnt then reset
                if (buddy.getOurSock() != null) {
                  buddy.getOurSock().close();
                }
                if (buddy.getTheirSock() != null) {
                  buddy.getTheirSock().close();
                }

                {
                /*
								Warning: Setting Objects to null
								 */
                  buddy.setOurSock(null);
                  buddy.setTheirSock(null);
                }

                buddy.setStatus(Status.OFFLINE);
                buddy.connect();
                Logger.log(Logger.INFO, "Status Thread",
                    "Connection reset for " + buddy.getAddress());
              }

              if (buddy.getStatus() >= Status.ONLINE && (
                  buddy.getOurSock() == null || buddy.getTheirSock() == null || buddy.getOurSock()
                      .isClosed() || buddy.getTheirSock().isClosed())) {
                if (buddy.getOurSock() != null) {
                  buddy.getOurSock().close();
                }

                buddy.setOurSock(null);
                if (buddy.getTheirSock() != null) {
                  buddy.getTheirSock().close();
                }

                buddy.setTheirSock(null);
                buddy.setStatus(Status.OFFLINE);
              } else if (buddy.getStatus() == Status.HANDSHAKE && (buddy.getOurSock() == null
                  || buddy
                  .getOurSock().isClosed())) {
                if (buddy.getOurSock() != null) {
                  buddy.getOurSock().close();
                }

                buddy.setOurSock(null);
                buddy.setStatus(Status.OFFLINE);
              }
              // TODO check unsanswered pings
              if (buddy.getUnansweredPings() > 5) {
                buddy.disconnect();
              }

              if (buddy.getOurSock() != null && buddy.getOurSockOut() != null && buddy
                  .getTheirSock() != null && buddy.getStatus() >= Status.ONLINE
                  && System.currentTimeMillis() - buddy
                  .getLastStatusRecieved() > Config.DEAD_CONNECTION_TIMEOUT * 1000) {
                Logger.log(Logger.INFO, "Status Thread", "");
                buddy.disconnect();
              }
              if (buddy.getOurSock() != null && buddy.getTheirSock() != null && buddy
                  .getReceivedPong()
                  && buddy.getTimeSinceLastStatus() > (Config.KEEPALIVE_INTERVAL - 20) * 1000) {
                buddy.sendStatus(); // Sends status every 100 seconds supposed to be every
              }
              // KEEPALIVE_INTERVAL but 20 seconds shorter just incase :\
              if (buddy.getOurSock() != null && buddy.getOurSockOut() != null && !buddy
                  .getReceivedPong() && (System.currentTimeMillis() - buddy.getLastPing())
                  > (Config.KEEPALIVE_INTERVAL / 4) * 1000) {
                buddy.sendPing();
              }

              if (buddy.getReconnectAt() != -1
                  && System.currentTimeMillis() - buddy.getReconnectAt() > 0) {
                // Retries connection after having waited it out
                Logger.log(Logger.INFO, "Status Thread",
                    "Retrying connection to " + buddy.getAddress() + " as it is past "
                        + buddy.getReconnectAt());
                buddy.connect();
              }
            }
            Thread.sleep(5000);
            // System.out.println("Status Ping");
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }, "Starting status thread.", "Status thread");

      Logger.log(Logger.INFO, "Init", "Done.");
      Logger.setOverride(false);
      try {
        Thread.sleep(2500);
      } catch (InterruptedException e) {
        // ignored
        //TODO Do something with this ignored catch block :D
      }

    } catch (Exception e) {
      halt(e);
    }
  }

  @SuppressWarnings("unchecked")
  public static void halt(Exception e) {
    halted = true;
    System.err.println("*** Error during startup, Halting! ***");
    e.printStackTrace();
    TCServ.halt();
    if (GuiLog.getGuiLog() != null) {
      GuiLog.getGuiLog().setVisible(true);
      GuiLog.getGuiLog().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      if (launched) {
        try {
          BuddyList.saveBuddies();
        } catch (IOException e1) {
          Logger
              .log(Logger.WARNING, "The End", "Error saving buddies: " + e1.getLocalizedMessage());
        }
        for (Buddy b : ((HashMap<String, Buddy>) BuddyList.buds.clone()).values()) {
          try {
            b.remove();
          } catch (IOException e1) {
            Logger.log(Logger.WARNING, "The End",
                "Error removing buddy " + (b == null ? "null" : b.toString(true)) + ": " + e1
                    .getLocalizedMessage());
          }
        }
        launched = false;
      }
      try {
        Class<?> c = Class.forName("gui.Gui");
        Object i = c.getDeclaredField("instance").get(null);
        Field f = c.getDeclaredField("f");
        f.setAccessible(true);
        ((JFrame) f.get(i)).dispose();
      } catch (Exception ee) {
        ee.printStackTrace(); // should be ignored
      }
    }
    System.err.println("*** Error during startup, Halted! ***");
  }


  public static void sendMyInfo() {
    for (Buddy buddy : BuddyList.buds.values()) {
      if (buddy.getStatus() >= Status.ONLINE) {
        try {
          buddy.sendClient();
          buddy.sendVersion();
          buddy.sendProfileName();
          buddy.sendProfileText();
          buddy.sendStatus();
        } catch (IOException ioe) {
          try {
            ioe.printStackTrace();
            buddy.disconnect(); // something is iffy if we error out
          } catch (IOException e) {
            // TODO integrate a log
            e.printStackTrace();
          }
        }
      }
    }
  }

  public static void sendMyProfile() /*  There's a typo in the method name (I think)!  */ {
    for (Buddy buddy : BuddyList.buds.values()) {
      if (buddy.getStatus() >= Status.ONLINE) {
        try {
          buddy.sendProfileName();
          buddy.sendProfileText();
        } catch (IOException ioe) {
          try {
            ioe.printStackTrace();
            buddy.disconnect(); // something is iffy if we error out
          } catch (IOException e) {
            // ignored
            //plzzz this is badd practice - DW
          }
        }
      }
    }
  }

  public static void sendMyStatus() {
    for (Buddy buddy : BuddyList.buds.values()) {
      if (buddy.getStatus() >= Status.ONLINE) {
        try {
          buddy.sendStatus();
        } catch (IOException ioe) {
          try {
            ioe.printStackTrace();
            buddy.disconnect(); // something is iffy if we error out
          } catch (IOException e) {
            // TODO Log exception
          }
        }
      }
    }
  }

  public static TCPortStatus getStatus() {
    return status;
  }

  public static void setStatus(TCPortStatus statusToSet) {
    status = statusToSet;
  }

  public static void setStatus(String statusToSet) throws IllegalArgumentException {
    //TCPortStatus.fromValue(statusToSet) may throw an IllegalArgumentException
    status = TCPortStatus.fromValue(statusToSet);
  }


}
