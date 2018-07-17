package util;

import core.Buddy;
import core.Config;
import core.Logger;
import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;


@SuppressWarnings("serial")
public class TCIconRenderer extends DefaultTreeCellRenderer {

  public static Image newimg;
  public static Image img;
  public static ImageIcon awayIcon;
  public static ImageIcon handshakeIcon;
  public static ImageIcon offlineIcon;
  public static ImageIcon onlineIcon;
  public static ImageIcon xaIcon;
  public static Image awayImage;
  public static Image handshakeImage;
  public static Image offlineImage;
  public static Image onlineImage;
  public static Image xaImage;
  public static Image awayImagebase;
  public static Image handshakeImagebase;
  public static Image offlineImagebase;
  public static Image onlineImagebase;
  public static Image xaImagebase;

  static {
    Logger.log(Logger.INFO, "Gui", "Loading status icons.");

    awayIcon = new ImageIcon(Config.getIconDir() + "/aw.png");
    handshakeIcon = new ImageIcon(Config.getIconDir() + "/co.png");
    offlineIcon = new ImageIcon(Config.getIconDir() + "/of.png");
    onlineIcon = new ImageIcon(Config.getIconDir() + "/on.png");
    xaIcon = new ImageIcon(Config.getIconDir() + "/xa.png");

    awayImagebase = getImg(Config.getIconDir() + "/aw.png");
    handshakeImagebase = getImg(Config.getIconDir() + "/co.png");
    offlineImagebase = getImg(Config.getIconDir() + "/of.png");
    onlineImagebase = getImg(Config.getIconDir() + "/on.png");
    xaImagebase = getImg(Config.getIconDir() + "/xa.png");

    awayImage = awayImagebase.getScaledInstance(Config.getImage_size(), Config.getImage_size(),
        Image.SCALE_SMOOTH);
    handshakeImage = handshakeImagebase.getScaledInstance(Config.getImage_size(),
        Config.getImage_size(), Image.SCALE_SMOOTH);
    offlineImage = offlineImagebase
        .getScaledInstance(Config.getImage_size(), Config.getImage_size(),
            Image.SCALE_SMOOTH);
    onlineImage = onlineImagebase.getScaledInstance(Config.getImage_size(), Config.getImage_size(),
        Image.SCALE_SMOOTH);
    xaImage = xaImagebase.getScaledInstance(Config.getImage_size(), Config.getImage_size(),
        Image.SCALE_SMOOTH);

    img = awayIcon.getImage();
    newimg = img.getScaledInstance(Config.getIcon_size(), Config.getIcon_size(),
        Image.SCALE_SMOOTH);
    awayIcon = new ImageIcon(newimg);

    img = handshakeIcon.getImage();
    newimg = img.getScaledInstance(Config.getIcon_size(), Config.getIcon_size(),
        Image.SCALE_SMOOTH);
    handshakeIcon = new ImageIcon(newimg);

    img = offlineIcon.getImage();
    newimg = img.getScaledInstance(Config.getIcon_size(), Config.getIcon_size(),
        Image.SCALE_SMOOTH);
    offlineIcon = new ImageIcon(newimg);

    img = onlineIcon.getImage();
    newimg = img.getScaledInstance(Config.getIcon_size(), Config.getIcon_size(),
        Image.SCALE_SMOOTH);
    onlineIcon = new ImageIcon(newimg);

    img = xaIcon.getImage();
    newimg = img.getScaledInstance(Config.getIcon_size(), Config.getIcon_size(),
        Image.SCALE_SMOOTH);
    xaIcon = new ImageIcon(newimg);

  }

  @SuppressWarnings("unused")
  private JTree t;

  public TCIconRenderer(JTree t) {
    this.t = t;
  }

  public static Image getImg(URL u) {
    try {
      Image x = ImageIO.read(u);
//			System.out.println("Loaded: " + x);
      return x;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static Image getImg(String s) {
    try {
      Image x = ImageIO.read(new File(s));
//			System.out.println("Loaded: " + x);
      return x;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static ImageIcon getStatusIcon(int status) {
    // TODO Auto-generated method stub
    if (status == Status.AWAY) {
      return awayIcon;
    } else if (status == Status.HANDSHAKE) {
      return handshakeIcon;
    } else if (status == Status.OFFLINE) {
      return offlineIcon;
    } else if (status == Status.ONLINE) {
      // System.out .println("onlie");
      return onlineIcon;
    } else if (status == Status.XA) {
      return xaIcon;
    }
    return null;
  }

  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value,
      boolean sel, boolean expanded, boolean leaf, int row,
      boolean hasFocus) {

    super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
        row, hasFocus);

    Object nodeObj = ((DefaultMutableTreeNode) value).getUserObject();
    if (nodeObj instanceof Buddy) {
      Buddy b = ((Buddy) nodeObj);
      int status = b.getStatus();
      String s = "<html>" + b.getAddress();
      if (b.getProfile_name() != null && !b.getProfile_name().equals("")) {
        s += "<BR>" + b.getProfile_name();
      }
      if (b.getProfile_text() != null && !b.getProfile_text().equals("")) {
        s += "<BR>" + b.getProfile_text();
      }
      if (b.getClient() != null && !b.getClient().equals("")) {
        s += "<BR>" + b.getClient() + " " + b.getVersion();
      }

      s += "</html>";
      this.setToolTipText(s);
      setIcon(getStatusIcon(status));
    }
    return this;
  }
}