package commands;

import core.Config;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class util_page {

  public static String read(String page) {

    String msg = "";
    try {
      if (page.indexOf('.') == -1 || page.indexOf('/') == -1) {
        Scanner scannerObj;
        File f1 = new File(Config.getPageDir() + page + ".txt");

        if (f1.isFile() && f1.canRead()) {
          scannerObj = new Scanner(new FileInputStream(Config.getPageDir() + page + ".txt"));

          while (scannerObj.hasNextLine()) {
            msg += "\n" + scannerObj.nextLine();
          }
          scannerObj.close();

          if (msg.trim().endsWith("\n")) {
            msg.substring(0, msg.length() - 6);
          }

        }
      }


    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return msg;
  }

}


