package core;


import commands.CommandType;
import core.exceptions.BlacklistedException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;
import java.util.Scanner;

import util.ConfigWriter;
import util.Status;
import util.StatusUtils;
import util.Tray;


public class Buddy {

  private static Random random = new Random();
  private String address;
  private String name; // wtf is this for?, custom name? todo use this somewhere -- used


  private String hostName = "127.0.0.1";
  private volatile Socket ourSock = null; // Our socket to them - the output sock
  private volatile OutputStreamWriter ourSockOut = null;
  private volatile Socket theirSock = null; // Their socket to us - the input sock
  private String cookie;
  private String theirCookie;
  private byte status = Status.OFFLINE;
  private int connectFailCount = 0;
  private boolean receivedPong;
  private boolean sentPong;
  private String profile_name;
  private String client = "";

  private String version = "";
  private String profile_text;
  private Object connectLock = new Object();

  private long connectedAt;
  private long connectTime;
  private long reconnectAt;// Time of reconnection ?
  private long lastPing = -1;
  private long lastStatus = -1;
  private long lastStatusRecieved;
  private int unansweredPings;

  private int npe1Count;

  private Object OSO_LOCK = new Object(); // OurSock Outputstream lock
  private Object TSO_LOCK = new Object(); // TheirSock Outputstream lock

  public Buddy(String address, String name, Boolean now) {

    this.address = address;
    this.name = name;

    this.cookie = generateCookie();

    if (now) // Prevent flooding
    {
      BuddyList.addBuddy(this);
    }
  }

  public static String generateCookie() {
    String cookie = "";
    String alphaNumeric = "abcdefghijklmnopqrstuvwxyz1234567890";

    for (int i = 0; i < 77; i++) {
      cookie += alphaNumeric.charAt(random.nextInt(alphaNumeric.length()));
    }

    return cookie;
  }

  public static Boolean isInBlackList(String address) {
    return BuddyList.black.containsKey(address);
  }

  public static Boolean isInHolyList(String address) {
    return BuddyList.holy.containsKey(address);
  }

  public static boolean checkSock(Socket socket) {
    return socket != null && socket.isConnected() && !socket.isClosed();
  }

  public static String getStatusName(byte statusByte) {
    return StatusUtils.getStatusName(statusByte);
  }

  public static Random getRandom() {
    return random;
  }

  public static void setRandom(Random random) {
    Buddy.random = random;
  }

  public void onFullyConnected() throws IOException {
    sendClient();
    sendVersion();
    sendProfileName();
    sendProfileText();
    sendAddMe();
    sendStatus();

    if (!BuddyList.buds.containsKey(this.address)) {
      BuddyList.addBuddy(this);
    }
  }

  public void connect() {
    if (isInBlackList(this.address)) {
      return;
    }

    if (ourSock != null) {
      reconnectAt = -1;
      Logger.log(Logger.WARNING, this, "Connect(V)V was called but ourSock isn't null!");
      Thread.dumpStack();
      return;
    }

    // maybe store sock connection in another variable then move it to
    // ourSock when connected - fixed with ourSockOut
    String workMessage = "Connect to " + address,
        workName = "Connection thread for " + address;
    ThreadManager.registerWork(ThreadManager.NORMAL, this::connectWork, workMessage, workName);
  }

  private void connectWork() {
    connect_outboundSegment();
    connectLockNotifyAll();

    if (ourSockOut != null) {
      connect_inboundSegment();
    }
  }

  private void connect_outboundSegment() {
    try {
      reconnectAt = -1;
      connectTime = System.currentTimeMillis();
      initOurSockAndHandshake();
      Logger.log(Logger.INFO, Buddy.this, "Connected to {" + address + ", " + name + "}");
      ourSockOut = new OutputStreamWriter(ourSock.getOutputStream(), "UTF-8");

      sendPing();

      if (theirCookie != null) {
        sendPong(theirCookie);
        Logger.log(Logger.DEBUG, Buddy.this, "Sent " + address + " a cached pong");
        theirCookie = null; // cbb to clear properly elsewhere
      }
      connectTime = -1;

      //This should be unecessary but I'm sure this was added for debugging
      if (ourSock == null) {
        Logger.log(Logger.SEVERE, Buddy.this,
            "Wtf?! ourSock is null, but we just connected");
      }
      connectFailCount = 0;
      // System.err.println(ourSockOut);
    } catch (Exception e) {

      connectFailCount++;
      if (ourSock != null) {
        try {
          ourSock.close();
        } catch (IOException e1) {
          //TODO Add statement to log
          // we should'nt have to worry about this
        }
      }

      ourSock = null;
      ourSockOut = null;
      setStatus(Status.OFFLINE);
      Logger.log(Logger.WARNING, Buddy.this,
          "Failed to connect to " + address + " : " + e.getMessage() + " | Retry in " + (
              reconnectAt - System.currentTimeMillis()));
      // e.printStackTrace();
      connectTime = -1;
    }
  }

  private void connect_inboundSegment() {
    try {
      InputStream inputStream = ourSock.getInputStream();
      initInputStream(inputStream);
    } catch (Exception e) {
      e.printStackTrace();
      try {
        disconnect();
      } catch (IOException ioe) {
        //TODO add logger
      }
    }
  }

  private void connectLockNotifyAll() {
    synchronized (connectLock) {
      connectLock
          .notifyAll(); // incase something messed up we clear it out of the way so it should work next time
    }
  }

  private void initOurSockAndHandshake() throws IOException {
    int onionPort = 11009;
    ourSock = new Socket(
        new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(hostName,
            Config.getSocksPort())));
    ourSock.connect(InetSocketAddress.createUnresolved(address + ".onion", onionPort));
    setStatus(Status.HANDSHAKE);
  }

  private void initInputStream(InputStream inputStream) throws IOException {
    byte b;
    String input = "";
    while ((b = (byte) inputStream.read()) != -1) {
      if ((char) b == '\n') { // shouldnt happen
        Logger.log(Logger.SEVERE, Buddy.this.getClass(),
            "Recieved unknown '" + input + "' on ourSock from " + Buddy.this
                .toString(true));
        input = "";
        continue;
      }

      input += (char) b;
      if ((char) b == ' ' && !input.substring(0, input.length() - 1).contains(" ")) {
        BuddyIncoming.init_outin(input, Buddy.this, inputStream);
        input = "";
      }
    }

    //FIXME Is this in the right place?
    Logger.log(Logger.SEVERE, Buddy.this.getClass(), "BROKEN - " + address);
  }

  public void setMyStatus(String status) {

    TCPort.setStatus(status);

    Logger.log(Logger.INFO, "Buddy", "Status set " + TCPort.getStatus().toString());

        /*
            try {
                sendStatus();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        this might be useful later (???)
        */
  }

  public void setMyStatus(TCPortStatus status) {
    TCPort.setStatus(status);
    Logger.log(Logger.INFO, "Buddy", "Status set " + TCPort.getStatus().toString());
  }

  public void sendPing() throws IOException {
    sendRaw(CommandType.PING.toString() + " " + Config.getUs() + " "
        + cookie);//IOException can occur here
    unansweredPings++;//Umm, always? Maybe I'm misunderstanding
    if (status == Status.OFFLINE || status == Status.HANDSHAKE) {
      lastPing = System.currentTimeMillis();
    }
  }

  public void sendRaw(String command) throws IOException {

    if (!isInBlackList(this.address)) {
      synchronized (OSO_LOCK) {
        try {
          Logger.log(Logger.DEBUG, this, "Send " + address + " " + command);
          ourSockOut.write((command + ((char) 10)));
          ourSockOut.flush();
        } catch (IOException ioe) {
          Logger.log(Logger.WARNING, this,
              "[" + address + "] ourSock = null; theirSock = null; " + ioe.getLocalizedMessage());
          disconnect();
          throw ioe;
        }
      }
    }
  }

  public void sendPong(String pong) throws IOException {
    sendRaw(CommandType.PONG.toString() + " " + pong);
    sentPong = true;
  }

  public void sendClient() throws IOException {
    sendRaw(CommandType.CLIENT.toString() + " " + Config.CLIENT);
  }

  public void sendVersion() throws IOException {
    sendRaw(CommandType.VERSION.toString() + " " + Config.VERSION);
  }

  public void sendProfileName() throws IOException {
    sendRaw(CommandType.PROFILE_NAME.toString() + " " + TCPort.profile_name);
  }

  public void sendProfileText() throws IOException {
    sendRaw(CommandType.PROFILE_TEXT.toString() + " " + TCPort.profile_text);
  }

  public void sendAddMe() throws IOException {
    sendRaw(CommandType.ADD_ME.toString());
  }

  public void sendStatus() throws IOException {

    /*if (Config.updateStatus > 0 && Config.updateStatus < 4) {
      setMyStatus(Config.updateStatus);
      Config.updateStatus = 0;
    } //TODO I have no idea what happening here. No documentation. Revisit later. Placeholder below
    */

    if (Config.getUpdateStatus() != TCPortStatus.STATUS_SENT) {
      setMyStatus(Config.getUpdateStatus());
      Config.setUpdateStatus(TCPortStatus.STATUS_SENT);
    }

    sendRaw(CommandType.STATUS.toString() + " " + TCPort.getStatus());
    lastStatus = System.currentTimeMillis();

  }


  public void attach(Socket socket, Scanner sc) throws IOException, BlacklistedException {
    //TODO If I'm not mistaken, this method is supposed to connect/attach to Socket socket

    if (isInBlackList(this.address)) {
      throw new BlacklistedException(String.format("Address \"%s\" is blacklisted.", this.address));
    }

    if (theirSock != null) {
      disconnect();
      connect(); // TODO might need to do something about this entire block
      synchronized (connectLock) {
        try {
          Logger.log(Logger.NOTICE, this, "Waiting...");
          // connectLock.wait(45000); // 45sec wait for conenct
          connectLock.wait(); // wait for connect to notify
          // !NOTE! notify is called regardless of success or failure
        } catch (InterruptedException e) {
          //TODO Log exception
        }
      }
    }

    if (status == Status.OFFLINE && connectTime != -1) {
      // connect() method is trying to connect atm

      // is severe so its printed on err
      Logger.log(Logger.SEVERE, this, "status == Status.OFFLINE && connectTime != -1");
				/*
				synchronized(connectLock) {
				try {
				// connectLock.wait(45000); // 45sec wait for conenct
				Logger.log(Logger.NOTICE, this, "Waiting...");
				connectLock.wait(); // wait for conenct
				// !NOTE! notify is called regardless of success or failure
				} catch (InterruptedException e) { }
				}
				*/
    }
    // FIXME really need to fix replying to commands before we're connected
    this.theirSock = socket;
    this.receivedPong = false;
    try {
      while (sc.hasNext()) {
        String next = sc.next();
        if (!sentPong && theirCookie != null) {
          try {
            sendPong(theirCookie);
          } catch (NullPointerException npe) {
            Logger.log(Logger.INFO, Buddy.this, "Caught NullPointerException on " + address);
            if (npe1Count++ > 5) {
              disconnect();
              connect();
              return;
            }
          }
        }
        BuddyIncoming.init(next, this);
      }
    } catch (SocketException se) {
      Logger.log(Logger.DEBUG, this,
          "[" + address + "] attach() " + se.getLocalizedMessage() + " | " + se
              .getStackTrace()[0]);
      // SocketExceptions are quite common and generally nothing to worry about
    } catch (IOException ioe) {
      Logger.log(Logger.WARNING, this,
          "[" + address + "] theirSock = null; ourSock = null; " + ioe.getLocalizedMessage() + " | "
              + ioe.getStackTrace()[0]);
      disconnect();
      throw ioe;
    }

  }

  public void sendMessage(String string) throws IOException {
    sendRaw("message " + string);
  }

  public void sendDisconnect() throws IOException {
    sendRaw("disconnect");
  }

  public long getTimeSinceLastStatus() {
    return System.currentTimeMillis() - lastStatus;
  }

  @Override
  public String toString() {
    return (name != null && name.length() > 0) ? name :
        (profile_name != null && profile_name.length() > 0) ? profile_name :
            "[" + address + "]"; // + " (" + address + ")";
  }

  public String toString(boolean b) {
        /*
        TODO This method returns a boolean, but it's cast to String. Check if it's appropriate to change return type.
        */

    return address.equals(Config.getUs()) ? Language.langtext[61] :
        (profile_name != null && profile_name.length() > 0) ? profile_name + " (" + address + ")" :
            (name != null && name.length() > 0) ? name + " (" + address + ")" :
                "[" + address + "]"; // + " (" + address + ")";
  }

  public void disconnect() throws IOException { // should be used with caution
    if (ourSock != null) {
      ourSock.close();
    }

    ourSock = null;//Why set to null. Isn't it closed?
    if (theirSock != null) {
      theirSock.close();
    }

    theirSock = null;
    setStatus(Status.OFFLINE);
    Logger.log(Logger.NOTICE, this,
        "Disconnect called on " + address + " | Retry in " + (reconnectAt - System
            .currentTimeMillis()));
  }

  public void remove() throws IOException {
    ConfigWriter.deletebuddy(this);
    BuddyList.buds.remove(this.address);
    try {
      if (this.isFullyConnected()) {
        this.sendRaw("remove_me");
      }
    } catch (IOException e) {
      //TODO REMOVE OR DO SOMETHING IN THIS EMPTY CATCH BLOCK
    }
    disconnect();
    APIManager.fireBuddyRemoved(this);
  }

  //GETTERS AND SETTERS
  public byte getStatus() {
    return status;
  }

  protected void setStatus(byte status) {
    if (status == Status.OFFLINE) {
      // if (address.equals("jutujsy2ufg33ckl"))
      // Thread.dumpStack();
      reconnectAt = connectFailCount < 4 ? (15) : connectFailCount < 15 ? (5 * 60) : (30 * 60);
      reconnectAt *= (7 + random.nextInt(13)) / 10d;
      reconnectAt *= 1000;
      reconnectAt += System.currentTimeMillis();
      sentPong = false;
      receivedPong = false;
      unansweredPings = 0;
      ourSockOut = null;

      if (this.status >= Status.ONLINE) {
        Logger
            .log(Logger.INFO, this, address + " connected for " + (connectedAt == -1 ? " Not Set" :
                ((System.currentTimeMillis() - connectedAt) / 1000)));
        connectedAt = -1;
      }
    }

    //NOTE A: THE DIFFERENCE BETWEEN status and this.status in the if conditions
    noteA:
    {
      // During handshake lastStatus is used as lastPing
      if (status == Status.HANDSHAKE && this.status == Status.OFFLINE) {
        lastPing = System.currentTimeMillis();
      }

      // if connection just finished
      if (status >= Status.ONLINE && this.status <= Status.HANDSHAKE) {
        connectedAt = System.currentTimeMillis();
      }
    }

    if (this.status != status) {
      APIManager.fireStatusChange(this, status, this.status);
      this.status = status;
      Tray.updateTray();
    }
  }

  public String getHostName() {
    return hostName;
  }

  public void setHostName(String hostName) {
    this.hostName = hostName;
  }

  public String getTheirCookie() {
    return theirCookie;
  }

  public void setTheirCookie(String theirCookie) {
    this.theirCookie = theirCookie;
  }

  public int getConnectFailCount() {
    return connectFailCount;
  }

  public void setConnectFailCount(int connectFailCount) {
    this.connectFailCount = connectFailCount;
  }

  public Object getOsoLock() {
    return OSO_LOCK;
  }

  public void setOsoLock(Object osoLock) {
    OSO_LOCK = osoLock;
  }

  public Object getTsoLock() {
    return TSO_LOCK;
  }

  public void setTsoLock(Object tsoLock) {
    TSO_LOCK = tsoLock;
  }

  public String getProfile_name() {
    return profile_name;
  }

  public void setProfile_name(String profile_name) {
    this.profile_name = profile_name;
  }

  public String getClient() {
    return client;
  }

  public void setClient(String text) {
    this.client = text;
  }

  public String getProfile_text() {
    return profile_text;
  }

  public void setProfile_text(String profile_text) {
    this.profile_text = profile_text;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String text) {
    this.version = text;
  }

  public long getConnectTime() {
    return connectTime;
  }

  public void setConnectTime(long connectTime) {
    this.connectTime = connectTime;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getName() {
    return name;
  }

  public void setName(String text) {
    this.name = text;
  }

  public boolean isFullyConnected() {
    return ourSockOut != null && ourSock != null && ourSock.isConnected() && !ourSock.isClosed()
        && theirSock != null && theirSock.isConnected() && !theirSock.isClosed();
  }

  public void setProfileName(String text) {
    this.profile_name = text;
  }

  public void setProfileText(String text) {
    this.profile_text = text;
  }

  public Socket getOurSock() {
    return ourSock;
  }

  public void setOurSock(Socket ourSock) {
    this.ourSock = ourSock;
  }

  public OutputStreamWriter getOurSockOut() {
    return ourSockOut;
  }

  public void setOurSockOut(OutputStreamWriter ourSockOut) {
    this.ourSockOut = ourSockOut;
  }

  public Socket getTheirSock() {
    return theirSock;
  }

  public void setTheirSock(Socket theirSock) {
    this.theirSock = theirSock;
  }

  public String getCookie() {
    return cookie;
  }

  public void setCookie(String cookie) {
    this.cookie = cookie;
  }

  public boolean isReceivedPong() {
    return receivedPong;
  }

  public void setReceivedPong(boolean receivedPong) {
    this.receivedPong = receivedPong;
  }

  public boolean isSentPong() {
    return sentPong;
  }

  public void setSentPong(boolean sentPong) {
    this.sentPong = sentPong;
  }

  public Object getConnectLock() {
    return connectLock;
  }

  public void setConnectLock(Object connectLock) {
    this.connectLock = connectLock;
  }

  public long getConnectedAt() {
    return connectedAt;
  }

  public void setConnectedAt(long connectedAt) {
    this.connectedAt = connectedAt;
  }

  public long getReconnectAt() {
    return reconnectAt;
  }

  public void setReconnectAt(long reconnectAt) {
    this.reconnectAt = reconnectAt;
  }

  public long getLastPing() {
    return lastPing;
  }

  public void setLastPing(long lastPing) {
    this.lastPing = lastPing;
  }

  public long getLastStatus() {
    return lastStatus;
  }

  public void setLastStatus(long lastStatus) {
    this.lastStatus = lastStatus;
  }

  public long getLastStatusRecieved() {
    return lastStatusRecieved;
  }

  public void setLastStatusRecieved(long lastStatusRecieved) {
    this.lastStatusRecieved = lastStatusRecieved;
  }

  public int getUnansweredPings() {
    return unansweredPings;
  }

  public void setUnansweredPings(int unansweredPings) {
    this.unansweredPings = unansweredPings;
  }

  public int getNpe1Count() {
    return npe1Count;
  }

  public void setNpe1Count(int npe1Count) {
    this.npe1Count = npe1Count;
  }

}
