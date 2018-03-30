package core;


import java.io.File;
import java.util.Properties;

import util.ConfigWriter;
import util.DataControl;
import util.Util;

public class Config {

  // What Client and Version
  public static final String CLIENT = "JTC [T2]";
  public static final String VERSIONA = "0";
  public static final String VERSIONB = "7";
  public static final String VERSIONC = "12";
  // Test for connections
  public static final int DEAD_CONNECTION_TIMEOUT = 240;
  public static final int KEEPALIVE_INTERVAL = (int) (Math.random() * 120); //120;
  public static final int MAX_UNANSWERED_PINGS = 4;
  public static final int CONNECT_TIMEOUT = 70;
  // Compile with
  public static final String BUILD = VERSIONA + "." + VERSIONB + "." + VERSIONC;
  // Config or Control Variables
  public static final Properties prop;
  private static String comment = "";
  public static final String VERSION = BUILD + " " + comment;
  // Important Folders
  private static String BASE_DIR = "";
  private static String BUDDY_DIR = "";
  private static String CONFIG_DIR = "";
  private static String DOWNLOAD_DIR = "";
  private static String LOG_DIR = "";
  private static String MESSAGE_DIR = "";
  private static String PAGE_DIR = "";
  private static String TOR_DIR = "";
  private static String LANG_DIR = "";
  private static String DATA_DIR = "";
  private static String ICON_DIR = "";
  private static String ICON_DIR_MAIN = "";
  private static int loadTor;
  private static int SOCKS_PORT;
  private static int LOCAL_PORT;
  private static int alert_on_message;
  private static int alert_on_status_change;
  private static int visiblelog;
  private static int fulllog;
  private static int buddyStart;
  private static int updateStart;

  private static TCPortStatus updateStatus = TCPortStatus.AVAILABLE;

  private static int firststart;
  private static int pageactive;
  private static int transferonstart;
  private static int allcheckupdate;
  private static int obfsproxy;
  private static int ClickableLinks;
  private static int offlineMod;
  private static int image_size;
  private static int icon_size;
  private static int icon_space;
  private static String icon_folder;
  private static String sync;
  private static String update;
  private static String us;
  private static String nowstart = "";
  private static String nowstartupdate = "";
  private static String LastCheck;
  private static String lang;//language selection TODO change to enum
  private static String dlang = "en";//TODO try to understand what dlang is.
  private static String answer;
  private static String os;
  // Paths to all Imortant Files
  private static String TorLin = "linux/jtor.lin";
  private static String TorLinobf = "linux/jtorobf.lin";
  private static String TorLinlib = "linux/";
  private static String TorWin = "windows/jtor.exe";
  private static String TorWinobf = "windows/jtorobf.exe";
  private static String Torbinary;
  private static String controlfile = "controlfile";
  // Linux only
  private static String Torclose = "linux/torclose.sh";
  private static String torpid = Integer.toString(Util.myRandom(1000, 9999));
  private static String TorWINLINtorrc = "torrc.txt";
  private static String TorWINLINtorrcobf = "torrcobf.txt";
  private static String Tortorrc;

  static {
    os = System.getProperty("os.name").toLowerCase();
    DataControl.init();
    prop = new Properties();
    ConfigWriter.loadall();

    // Choose the right Folder with Icons
    Config.ICON_DIR = Config.ICON_DIR_MAIN + icon_folder;
    if (!new File(ICON_DIR).exists()) {
      Config.ICON_DIR = Config.ICON_DIR_MAIN + "juan.icon";
    }

    Logger.log(Logger.NOTICE, "Config", "Using " + ICON_DIR + " as ICON_DIR");
    if (Config.buddyStart == 1 & Config.offlineMod == 0) {
      nowstart = sync;
    }

    if (Config.updateStart == 1 & Config.offlineMod == 0) {
      nowstartupdate = update;
    }

    // Choose the right binary for every system
    if (os.contains("win")) {
      if (obfsproxy == 0) {
        Torbinary = TorWin;
        Tortorrc = TorWINLINtorrc;
      } else {
        Torbinary = TorWinobf;
        Tortorrc = TorWINLINtorrcobf;
      }
    } else if (os.contains("nix") || os.contains("nux")) {
      if (obfsproxy == 0) {
        Torbinary = TorLin;
        Tortorrc = TorWINLINtorrc;
      } else {
        Torbinary = TorLinobf;
        Tortorrc = TorWINLINtorrcobf;
      }
    }

    answer = Language.loadlang();
    Logger.log(Logger.INFO, "Config", "Using " + SOCKS_PORT + " as socks port.");
    Logger.log(Logger.INFO, "Config", "Using " + LOCAL_PORT + " as local port.");
  }

  // GETTERS AND SETTERS

  public static String getComment() {
    return comment;
  }

  public static void setComment(String comment) {
    Config.comment = comment;
  }

  public static String getBaseDir() {
    return BASE_DIR;
  }

  public static void setBaseDir(String baseDir) {
    BASE_DIR = baseDir;
  }

  public static String getBuddyDir() {
    return BUDDY_DIR;
  }

  public static void setBuddyDir(String buddyDir) {
    BUDDY_DIR = buddyDir;
  }

  public static String getConfigDir() {
    return CONFIG_DIR;
  }

  public static void setConfigDir(String configDir) {
    CONFIG_DIR = configDir;
  }

  public static String getDownloadDir() {
    return DOWNLOAD_DIR;
  }

  public static void setDownloadDir(String downloadDir) {
    DOWNLOAD_DIR = downloadDir;
  }

  public static String getLogDir() {
    return LOG_DIR;
  }

  public static void setLogDir(String logDir) {
    LOG_DIR = logDir;
  }

  public static String getMessageDir() {
    return MESSAGE_DIR;
  }

  public static void setMessageDir(String messageDir) {
    MESSAGE_DIR = messageDir;
  }

  public static String getPageDir() {
    return PAGE_DIR;
  }

  public static void setPageDir(String pageDir) {
    PAGE_DIR = pageDir;
  }

  public static String getTorDir() {
    return TOR_DIR;
  }

  public static void setTorDir(String torDir) {
    TOR_DIR = torDir;
  }

  public static String getLangDir() {
    return LANG_DIR;
  }

  public static void setLangDir(String langDir) {
    LANG_DIR = langDir;
  }

  public static String getDataDir() {
    return DATA_DIR;
  }

  public static void setDataDir(String dataDir) {
    DATA_DIR = dataDir;
  }

  public static String getIconDir() {
    return ICON_DIR;
  }

  public static void setIconDir(String iconDir) {
    ICON_DIR = iconDir;
  }

  public static String getIconDirMain() {
    return ICON_DIR_MAIN;
  }

  public static void setIconDirMain(String iconDirMain) {
    ICON_DIR_MAIN = iconDirMain;
  }

  public static int getLoadTor() {
    return loadTor;
  }

  public static void setLoadTor(int loadTor) {
    Config.loadTor = loadTor;
  }

  public static int getSocksPort() {
    return SOCKS_PORT;
  }

  public static void setSocksPort(int socksPort) {
    SOCKS_PORT = socksPort;
  }

  public static int getLocalPort() {
    return LOCAL_PORT;
  }

  public static void setLocalPort(int localPort) {
    LOCAL_PORT = localPort;
  }

  public static int getAlert_on_message() {
    return alert_on_message;
  }

  public static void setAlert_on_message(int alert_on_message) {
    Config.alert_on_message = alert_on_message;
  }

  public static int getAlert_on_status_change() {
    return alert_on_status_change;
  }

  public static void setAlert_on_status_change(int alert_on_status_change) {
    Config.alert_on_status_change = alert_on_status_change;
  }

  public static int getVisiblelog() {
    return visiblelog;
  }

  public static void setVisiblelog(int visiblelog) {
    Config.visiblelog = visiblelog;
  }

  public static int getFulllog() {
    return fulllog;
  }

  public static void setFulllog(int fulllog) {
    Config.fulllog = fulllog;
  }

  public static int getBuddyStart() {
    return buddyStart;
  }

  public static void setBuddyStart(int buddyStart) {
    Config.buddyStart = buddyStart;
  }

  public static int getUpdateStart() {
    return updateStart;
  }

  public static void setUpdateStart(int updateStart) {
    Config.updateStart = updateStart;
  }

  public static TCPortStatus getUpdateStatus() {
    return updateStatus;
  }

  public static void setUpdateStatus(TCPortStatus updateStatus) {
    Config.updateStatus = updateStatus;
  }

  public static int getFirststart() {
    return firststart;
  }

  public static void setFirststart(int firststart) {
    Config.firststart = firststart;
  }

  public static int getPageactive() {
    return pageactive;
  }

  public static void setPageactive(int pageactive) {
    Config.pageactive = pageactive;
  }

  public static int getTransferonstart() {
    return transferonstart;
  }

  public static void setTransferonstart(int transferonstart) {
    Config.transferonstart = transferonstart;
  }

  public static int getAllcheckupdate() {
    return allcheckupdate;
  }

  public static void setAllcheckupdate(int allcheckupdate) {
    Config.allcheckupdate = allcheckupdate;
  }

  public static int getObfsproxy() {
    return obfsproxy;
  }

  public static void setObfsproxy(int obfsproxy) {
    Config.obfsproxy = obfsproxy;
  }

  public static int getClickableLinks() {
    return ClickableLinks;
  }

  public static void setClickableLinks(int clickableLinks) {
    ClickableLinks = clickableLinks;
  }

  public static int getOfflineMod() {
    return offlineMod;
  }

  public static void setOfflineMod(int offlineMod) {
    Config.offlineMod = offlineMod;
  }

  public static int getImage_size() {
    return image_size;
  }

  public static void setImage_size(int image_size) {
    Config.image_size = image_size;
  }

  public static int getIcon_size() {
    return icon_size;
  }

  public static void setIcon_size(int icon_size) {
    Config.icon_size = icon_size;
  }

  public static int getIcon_space() {
    return icon_space;
  }

  public static void setIcon_space(int icon_space) {
    Config.icon_space = icon_space;
  }

  public static String getIcon_folder() {
    return icon_folder;
  }

  public static void setIcon_folder(String icon_folder) {
    Config.icon_folder = icon_folder;
  }

  public static String getSync() {
    return sync;
  }

  public static void setSync(String sync) {
    Config.sync = sync;
  }

  public static String getUpdate() {
    return update;
  }

  public static void setUpdate(String update) {
    Config.update = update;
  }

  public static String getUs() {
    return us;
  }

  public static void setUs(String us) {
    Config.us = us;
  }

  public static String getNowstart() {
    return nowstart;
  }

  public static void setNowstart(String nowstart) {
    Config.nowstart = nowstart;
  }

  public static String getNowstartupdate() {
    return nowstartupdate;
  }

  public static void setNowstartupdate(String nowstartupdate) {
    Config.nowstartupdate = nowstartupdate;
  }

  public static String getLastCheck() {
    return LastCheck;
  }

  public static void setLastCheck(String lastCheck) {
    LastCheck = lastCheck;
  }

  public static String getLang() {
    return lang;
  }

  public static void setLang(String lang) {
    Config.lang = lang;
  }

  public static String getDlang() {
    return dlang;
  }

  public static void setDlang(String dlang) {
    Config.dlang = dlang;
  }

  public static String getAnswer() {
    return answer;
  }

  public static void setAnswer(String answer) {
    Config.answer = answer;
  }

  public static String getOs() {
    return os;
  }

  public static void setOs(String os) {
    Config.os = os;
  }

  public static String getTorLin() {
    return TorLin;
  }

  public static void setTorLin(String torLin) {
    TorLin = torLin;
  }

  public static String getTorLinobf() {
    return TorLinobf;
  }

  public static void setTorLinobf(String torLinobf) {
    TorLinobf = torLinobf;
  }

  public static String getTorLinlib() {
    return TorLinlib;
  }

  public static void setTorLinlib(String torLinlib) {
    TorLinlib = torLinlib;
  }

  public static String getTorWin() {
    return TorWin;
  }

  public static void setTorWin(String torWin) {
    TorWin = torWin;
  }

  public static String getTorWinobf() {
    return TorWinobf;
  }

  public static void setTorWinobf(String torWinobf) {
    TorWinobf = torWinobf;
  }

  public static String getTorbinary() {
    return Torbinary;
  }

  public static void setTorbinary(String torbinary) {
    Torbinary = torbinary;
  }

  public static String getControlfile() {
    return controlfile;
  }

  public static void setControlfile(String controlfile) {
    Config.controlfile = controlfile;
  }

  public static String getTorclose() {
    return Torclose;
  }

  public static void setTorclose(String torclose) {
    Torclose = torclose;
  }

  public static String getTorpid() {
    return torpid;
  }

  public static void setTorpid(String torpid) {
    Config.torpid = torpid;
  }

  public static String getTorWINLINtorrc() {
    return TorWINLINtorrc;
  }

  public static void setTorWINLINtorrc(String torWINLINtorrc) {
    TorWINLINtorrc = torWINLINtorrc;
  }

  public static String getTorWINLINtorrcobf() {
    return TorWINLINtorrcobf;
  }

  public static void setTorWINLINtorrcobf(String torWINLINtorrcobf) {
    TorWINLINtorrcobf = torWINLINtorrcobf;
  }

  public static String getTortorrc() {
    return Tortorrc;
  }

  public static void setTortorrc(String tortorrc) {
    Tortorrc = tortorrc;
  }

}
