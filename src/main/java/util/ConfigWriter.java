package util;

import core.Buddy;
import core.Config;
import core.Logger;
import core.TCPort;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


public class ConfigWriter {

  private FileOutputStream fos;

  public ConfigWriter(FileOutputStream fos) {
    this.fos = fos;
  }

  private static int assignInt(String string, int def, Properties prop) {
    String x = (String) prop.get(string);
    int i = def;
    if (x != null) {
      try {
        i = Integer.parseInt(x);
      } catch (NumberFormatException nfe) {
        System.err.println(nfe.getLocalizedMessage());
      }
    }
    return i;
  }

  public static String assign(String key, String defaultReturnValue, Properties prop) {

    //FIXME I believe each occurence of this method can be replaced with a prop.getProperty(key,default) call
    String x = (String) prop.get(key);

    //TODO use ternary operator instead (for concision)
    String ret = defaultReturnValue;
    if (x != null) {
      ret = x;
    }
    return ret;
  }

  public static void saveall(int really) {
    Config.prop.clear();
    if (really == 0 || really == 2) {
      Config.prop.put("SOCKS_PORT", Config.getSocksPort() + "");
      Config.prop.put("LOCAL_PORT", Config.getLocalPort() + "");
      Config.prop.put("ourId", Config.getUs() == null ? "" : Config.getUs() + "");
      Config.prop.put("sync", Config.getSync() + "");
      Config.prop.put("update", Config.getUpdate() + "");
      Config.prop.put("lang", Config.getLang() + "");
      Config.prop.put("alert_on_message", Config.getAlert_on_message() + "");
      Config.prop.put("alert_on_status_change", Config.getAlert_on_status_change() + "");
      Config.prop.put("loadPortableTor", Config.getLoadTor() + "");
      Config.prop.put("OnStartBuddySync", Config.getBuddyStart() + "");
      Config.prop.put("OnStartUpdateCheck", Config.getUpdateStart() + "");
      Config.prop.put("transferonstart", Config.getTransferonstart() + "");
      Config.prop.put("pageactive", Config.getPageactive() + "");
      Config.prop.put("OnStartLoggerDisplay", Config.getVisiblelog() + "");
      Config.prop.put("EnableFullLoggerMode", Config.getFulllog() + "");
      Config.prop.put("obfsproxy", Config.getObfsproxy() + "");
      Config.prop.put("ClickableLinks", Config.getClickableLinks() + "");
      Config.prop.put("offlineMod", Config.getOfflineMod() + "");
    }
    if (really == 1 || really == 2) {
      Config.prop.put("icon_size", Config.getIcon_size() + "");
      Config.prop.put("icon_space", Config.getIcon_space() + "");
      Config.prop.put("ICON", Config.getIcon_folder() + "");
      Config.prop.put("image_size", Config.getImage_size() + "");
    }
    if (really == 3 || really == 2) {
      Config.prop.put("profile_name", TCPort.profile_name + "");
      Config.prop.put("profile_text", TCPort.profile_text + "");
    }

    try {
      Config.prop.store(new FileOutputStream(Config.getConfigDir() + "settings.ini"), null);
    } catch (FileNotFoundException fnfe) {
      fnfe.printStackTrace();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  public static void loadall() {
    File settingsFile;
    boolean save = false;

    boolean exists = (new File(Config.getConfigDir() + "settings.ini")).exists();
    if (exists) {
      settingsFile = new File(Config.getConfigDir() + "settings.ini");
      Logger.log(Logger.NOTICE, "ConfigWriter", "Load from settings.ini");
    } else {
      settingsFile = new File(Config.getConfigDir() + "backup/settings.ini");
      Logger.log(Logger.NOTICE, "ConfigWriter", "Load from backup");
      save = true;
    }

    Config.prop.clear();
    if (settingsFile.exists() && settingsFile.isFile() && settingsFile.canRead()) {
      try {
        Config.prop.load(new FileInputStream(settingsFile));
      } catch (IOException e) {

      }
    }

    Config.setUs(assign("ourId", null, Config.prop));
    Config.setSocksPort(assignInt("SOCKS_PORT", 11160, Config.prop));
    Config.setLocalPort(assignInt("LOCAL_PORT", 8978, Config.prop));
    TCPort.profile_name = assign("profile_name", "", Config.prop);
    TCPort.profile_text = assign("profile_text", "", Config.prop);
    Config.setLang(assign("lang", Config.getDlang(), Config.prop));
    Config.setSync(assign("sync", "", Config.prop));
    Config.setUpdate(assign("update", "", Config.prop));
    Config.setAlert_on_message(assignInt("alert_on_message", 1, Config.prop));
    Config.setAlert_on_status_change(assignInt("alert_on_status_change", 1, Config.prop));
    Config.setLoadTor(assignInt("loadPortableTor", 1, Config.prop));
    Config.setBuddyStart(assignInt("OnStartBuddySync", 0, Config.prop));
    Config.setUpdateStart(assignInt("OnStartUpdateCheck", 0, Config.prop));
    Config.setFirststart(assignInt("firststart", 0, Config.prop));
    Config.setVisiblelog(assignInt("OnStartLoggerDisplay", 1, Config.prop));
    Config.setFulllog(assignInt("EnableFullLoggerMode", 1, Config.prop));
    Config.setPageactive(assignInt("pageactive", 0, Config.prop));
    Config.setTransferonstart(assignInt("transferonstart", 0, Config.prop));
    Config.setObfsproxy(assignInt("obfsproxy", 0, Config.prop));
    Config.setClickableLinks(assignInt("ClickableLinks", 0, Config.prop));
    Config.setOfflineMod(assignInt("offlineMod", 0, Config.prop));
    Config.setImage_size(assignInt("image_size", 16, Config.prop));
    Config.setIcon_size(assignInt("icon_size", 16, Config.prop));
    Config.setIcon_space(assignInt("icon_space", 2, Config.prop));
    Config.setIcon_folder(assign("ICON", "juan.icon", Config.prop));

    if (save) {
      saveall(2);
    }
  }

  public static void deletebuddy(Buddy b) {
    File old = new File(Config.getBuddyDir() + b.getAddress());
    old.delete();
  }

  public static void savebuddy(Buddy b) {
    Config.prop.clear();

    if (b.getProfile_name() != null) {
      Config.prop.put("profile_name", b.getProfile_name() + "");
    }
    if (b.getProfile_text() != null) {
      Config.prop.put("profile_text", b.getProfile_text() + "");
    }
    if (b.getClient() != null) {
      Config.prop.put("client", b.getClient() + "");
    }
    if (b.getVersion() != null) {
      Config.prop.put("version", b.getVersion() + "");
    }

    try {
      Config.prop.store(new FileOutputStream(Config.getBuddyDir() + b.getAddress()), null);
    } catch (FileNotFoundException fnfe) {
      fnfe.printStackTrace();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  public static void loadbuddy(Buddy b) {

    Config.prop.clear();
    File settingsFile = new File(Config.getBuddyDir() + b.getAddress());
    if (settingsFile.exists() && settingsFile.isFile() && settingsFile.canRead()) {
      try {
        Config.prop.load(new FileInputStream(settingsFile));
      } catch (IOException e) {

      }
    }

    b.setProfileName(assign("profile_name", "", Config.prop));
    b.setProfileText(assign("profile_text", "", Config.prop));
    b.setClient(assign("client", "", Config.prop));
    b.setVersion(assign("version", "", Config.prop));

  }

  public void write(String h) throws IOException {
    fos.write((h + "\n").getBytes());
    fos.flush();
  }

  public void close() throws IOException {
    fos.close();
  }
}


