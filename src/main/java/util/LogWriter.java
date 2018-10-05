package util;

import core.Config;
import core.Logger;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import lombok.NonNull;


public class LogWriter {

  /**
   * Saves {@code what} to logfile where filename is formatted as follows:
   * <br>
   * 1. System time in seconds <br>
   * 2. IFF saving as a main log --> Random number between 100 and 1000 <br>
   * 3. dash followed by {@code filePrefix} <br>
   * 4. followed by file extension (  {@code .log}  ) <br>
   * @param what
   * @param filePrefix
   * @return
   */
  private static String logWrite(String what, @NonNull String filePrefix) {
    // File anlegen
    FileWriter writer;
    File file;

    // TODO: replace hard coded String and integer values with value from a config

    if (filePrefix.isEmpty())
      filePrefix = "main";

    String time = "" + (System.currentTimeMillis() / 1000);

    String timeSuffix = "";

    if (filePrefix.equals("main"))
      timeSuffix = "" + Util.myRandom(100, 1000);




    String filename = String.format(time + timeSuffix + "-%s.log", filePrefix);

    file = new File(Config.getLogDir() + filename);
    try {

      writer = new FileWriter(file, true);

      writer.write(what);

      //   writer.write(System.getProperty("line.separator"));
      writer.flush();
      writer.close();
      Logger.log(Logger.INFO, "Log", "Log saved to " + filename);

    } catch (IOException e) {
      e.printStackTrace();
    }
    return filename;
  }

  /**
   * Saves {@code textToWrite} as a main log file <br>
   * (See {@code LogWriter::logWrite(String what, String filePrefix)})
   * @param textToWrite
   * @return
   */
  public static String logWrite(String textToWrite) {
    // TODO: replace hard coded String values with value from a config
    return logWrite(textToWrite, "main");
  }

}

