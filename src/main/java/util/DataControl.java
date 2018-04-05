package util;

import gui.GuiLog;
import gui.GuiTorLoading;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

import core.Config;
import core.Logger;
import core.TCPort;
import core.TorLoader;


public class DataControl {

  private static final String CLASS_NAME = TorLoader.class.getName();

  public static void init() {
    Config.setBaseDir(get_base_pwd());
    Config.setDataDir("data/");
    Config.setConfigDir(Config.getBaseDir() + Config.getDataDir() + "config/");
    Config.setDownloadDir(Config.getBaseDir() + Config.getDataDir() + "downloads/");
    Config.setLogDir(Config.getBaseDir() + Config.getDataDir() + "log/");
    Config.setMessageDir(Config.getBaseDir() + Config.getDataDir() + "offlinemsgs/");
    Config.setPageDir(Config.getBaseDir() + Config.getDataDir() + "page/");
    Config.setTorDir(Config.getBaseDir() + Config.getDataDir() + "Tor/");
    Config.setLangDir(Config.getBaseDir() + Config.getDataDir() + "lang/");
    Config.setIconDirMain(Config.getBaseDir() + Config.getDataDir() + "icon/");
    Config.setBuddyDir(Config.getBaseDir() + Config.getDataDir() + "buddy/");

    checkData();

    Logger.log(Logger.NOTICE, CLASS_NAME, "Using " + Config.getConfigDir() + " as CONFIG_DIR");
    Logger.log(Logger.NOTICE, CLASS_NAME, "Using " + Config.getDownloadDir() + " as DOWNLOAD_DIR");
    Logger.log(Logger.NOTICE, CLASS_NAME, "Using " + Config.getLogDir() + " as LOG_DIR");
    Logger.log(Logger.NOTICE, CLASS_NAME, "Using " + Config.getMessageDir() + " as MESSAGE_DIR");
    Logger.log(Logger.NOTICE, CLASS_NAME, "Using " + Config.getPageDir() + " as PAGE_DIR");
    Logger.log(Logger.NOTICE, CLASS_NAME, "Using " + Config.getTorDir() + " as TOR_DIR");
    Logger.log(Logger.NOTICE, CLASS_NAME, "Using " + Config.getLangDir() + " as LANG_DIR");
    Logger.log(Logger.NOTICE, CLASS_NAME, "Using " + Config.getBuddyDir() + " as BUDDY_DIR");


  }

  public static String get_base_pwd() {
    String os = System.getProperty("os.name").toLowerCase();
    String BASE_DIR = null;

    try {
      if (TCPort.externalSourcePath.length > 0) {
        BASE_DIR = TCPort.externalSourcePath[0];
        BASE_DIR = new File(Config.getBaseDir()).getCanonicalPath() + "/";
      } else {
        String path = TCPort.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        BASE_DIR = URLDecoder.decode(path, "UTF-8");
        Logger.log(Logger.NOTICE, "ConfigWriter", BASE_DIR);

        if (os.contains("win")) {
          BASE_DIR = BASE_DIR.split("/", 2)[1];
        }

        File f1 = new File(BASE_DIR);

        // is the destination a file then delete this part from the string
        if (f1.isFile()) {
          BASE_DIR = Util.reverse(BASE_DIR);
          BASE_DIR = BASE_DIR.split("/", 2)[1];
          BASE_DIR = Util.reverse(BASE_DIR);
          BASE_DIR = BASE_DIR + "/";
        }
        // else it is a directory in the output folder and run direct over eclipse
        else {
          BASE_DIR = BASE_DIR + "../../../";
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return BASE_DIR;
  }


  //Check consistent of the Data Folder --> not complete
  public static void checkData() {
    final String controlFilePath =
        Config.getBaseDir() + Config.getDataDir() + Config.getControlfile();
    boolean exists = (new File(controlFilePath)).exists(); // check base dir
    if (!exists) {
      Logger.log(Logger.WARNING, CLASS_NAME,
          "Wrong base dir, the controlfile is not found: " + controlFilePath);
      final GuiTorLoading tl = new GuiTorLoading();
      tl.setVisible(true);
      tl.getProgressBar1().setValue(0);
      tl.getProgressBar1().setIndeterminate(false);
      tl.gettextArea1().setText("Wrong base dir, the controlfile is not found!");
      GuiLog.getGuiLog().setVisible(true);
      while (true) {
        try {
          Thread.sleep(10000);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
  }


}
