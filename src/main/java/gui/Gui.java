package gui;

import commands.list_of_commands;
import core.APIManager;
import core.Buddy;
import core.BuddyList;
import core.Config;
import core.Language;
import core.Logger;
import core.TCPort;
import core.TCPortStatus;
import core.TorLoader;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import listeners.APIListener;
import util.ChatWindow;
import util.MessageType;
import util.Status;
import util.TCIconRenderer;
import util.Tray;

public class Gui {

  public static JFrame f;
  public static Gui instance;
  private static DefaultMutableTreeNode root;
  private static DefaultMutableTreeNode buddyNodeholy;
  private static DefaultMutableTreeNode buddyNodeon;
  private static DefaultMutableTreeNode buddyNode;
  private static DefaultMutableTreeNode buddyNodeblack;
  private static HashMap<String, MutableTreeNode> nodeMap = new HashMap<String, MutableTreeNode>();
  private static HashMap<String, GuiChatWindow> windowMap = new HashMap<String, GuiChatWindow>();
  private static JTree jt;
  public HashMap<String, GuiListener> cmdListeners = new HashMap<String, GuiListener>();
  public int extraSpace;
  private Listener listener;
  private GuiAlert alert;

  /**
   * Sets the look and feel of the JFrame
   * @param string
   */
  public static void setLAF(String string) {
    try {
      LookAndFeelInfo[] lookAndFeelInfos = UIManager.getInstalledLookAndFeels();
      boolean validInfo = false;
      LookAndFeelInfo info;

      for (int index = 0; index < lookAndFeelInfos.length && !validInfo; index++) {
        info = lookAndFeelInfos[index];
        if (string.equals(info.getName())) {
          UIManager.setLookAndFeel(info.getClassName());
          validInfo = true;
        }
      }

    } catch (UnsupportedLookAndFeelException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
      e.printStackTrace();
    }
  }

  public static Gui getInstance() {
    return instance;
  }

  public static GuiChatWindow getChatWindow(Buddy b, boolean createIfNotExist, boolean setVis) {
    GuiChatWindow w = windowMap.get(b.getAddress());
    if (w == null && !createIfNotExist) {
      return null;
    }
    if (w == null) {
      w = new GuiChatWindow(b);
      windowMap.put(b.getAddress(), w);
    }
    w.setTitle(b.toString(true));

    w.setFocusableWindowState(false);
    if (setVis) {
      w.setVisible(true);
    }

    w.setFocusableWindowState(true);
    return w;
  }

  public static void blacklist(Buddy buddy) {

    MutableTreeNode node = nodeMap.remove(buddy.getAddress());
    if (node != null) // remove entry in the gui
    {
      ((DefaultTreeModel) jt.getModel()).removeNodeFromParent(node);
    }

    node = nodeMap.get(buddy.getAddress());
    if (node != null) {
      node.removeFromParent();
    }
    nodeMap.put(buddy.getAddress(), node = new DefaultMutableTreeNode(buddy));

    if (buddy.getAddress().equals(Config.getUs())) {
      ((DefaultTreeModel) jt.getModel()).insertNodeInto(node, buddyNodeblack, 0);
    } else {
      ((DefaultTreeModel) jt.getModel())
          .insertNodeInto(node, buddyNodeblack, buddyNodeblack.getChildCount());
    }

    if (buddyNodeblack.getChildCount() == 1) {
      jt.expandRow(0);
    }

  }

  public static void holylist(Buddy buddy) {
    if (buddy.isFullyConnected()) {
      MutableTreeNode node = nodeMap.remove(buddy.getAddress());
      if (node != null) // remove entry in the gui
      {
        ((DefaultTreeModel) jt.getModel()).removeNodeFromParent(node);
      }

      node = nodeMap.get(buddy.getAddress());
      if (node != null) {
        node.removeFromParent();
      }
      nodeMap.put(buddy.getAddress(), node = new DefaultMutableTreeNode(buddy));

      if (buddy.getAddress().equals(Config.getUs())) {
        ((DefaultTreeModel) jt.getModel()).insertNodeInto(node, buddyNodeholy, 0);
      } else {
        ((DefaultTreeModel) jt.getModel())
            .insertNodeInto(node, buddyNodeholy, buddyNodeholy.getChildCount());
      }

      if (buddyNodeholy.getChildCount() == 1) {
        jt.expandRow(0);
      }
    }

  }

  public static void pardon(Buddy buddy) {

    if (buddy.getStatus() >= Status.ONLINE) {

      MutableTreeNode node = nodeMap.remove(buddy.getAddress());
      if (node != null) // remove entry in the gui
      {
        ((DefaultTreeModel) jt.getModel()).removeNodeFromParent(node);
      }

      node = nodeMap.get(buddy.getAddress());
      if (node != null) {
        node.removeFromParent();
      }
      nodeMap.put(buddy.getAddress(), node = new DefaultMutableTreeNode(buddy));

      if (Buddy.isInHolyList(((Buddy) buddy).getAddress())) {
        if (buddy.getAddress().equals(Config.getUs())) {
          ((DefaultTreeModel) jt.getModel()).insertNodeInto(node, buddyNodeholy, 0);
        } else {
          ((DefaultTreeModel) jt.getModel())
              .insertNodeInto(node, buddyNodeholy, buddyNodeholy.getChildCount());
        }
        if (buddyNodeholy.getChildCount() == 1) {
          jt.expandRow(0);
        }
      } else {
        if (buddy.getAddress().equals(Config.getUs())) {
          ((DefaultTreeModel) jt.getModel()).insertNodeInto(node, buddyNodeon, 0);
        } else {
          ((DefaultTreeModel) jt.getModel())
              .insertNodeInto(node, buddyNodeon, buddyNodeon.getChildCount());
        }
        if (buddyNodeon.getChildCount() == 1) {
          jt.expandRow(0);
        }
      }

    } else {

      MutableTreeNode node = nodeMap.remove(buddy.getAddress());
      if (node != null) // remove entry in the gui
      {
        ((DefaultTreeModel) jt.getModel()).removeNodeFromParent(node);
      }

      node = nodeMap.get(buddy.getAddress());
      if (node != null) {
        node.removeFromParent();
      }
      nodeMap.put(buddy.getAddress(), node = new DefaultMutableTreeNode(buddy));

      if (buddy.getAddress().equals(Config.getUs())) {
        ((DefaultTreeModel) jt.getModel()).insertNodeInto(node, buddyNode, 0);
      } else {
        ((DefaultTreeModel) jt.getModel())
            .insertNodeInto(node, buddyNode, buddyNode.getChildCount());
      }

      if (buddyNode.getChildCount() == 1) {
        jt.expandRow(0);
      }

    }

  }

  public void init() {
    int w = 268, h = -1;
    if (listener != null) {
      Logger.log(Logger.WARNING, this.getClass(), "init(V)V called twice?");
      return;
    }
    instance = this;
    setLAF("Nimbus");

    if ((TCPort.profile_name == null && TCPort.profile_text == null)
        || Config.getFirststart() == 1) {
      Logger.log(Logger.WARNING, this.getClass(), "Start setting window");

      GuiSettings guis = new GuiSettings();
      guis.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      guis.setVisible(true);
      try {
        synchronized (guis) {
          guis.wait();
        }
      } catch (InterruptedException e) {
        // ignored
      }
    } else if (Config.getSocksPort() < 1 || Config.getLocalPort() < 1 || Config.getUs() == null) {
      Logger.log(Logger.WARNING, this.getClass(), "Start setting window on advanced");
      GuiSettings guis = new GuiSettings();
      guis.getTabbedPane1().setSelectedIndex(1);
      guis.setVisible(true);
      try {
        synchronized (guis) {
          guis.wait();
        }
      } catch (InterruptedException e) {
        // ignored
      }
    }

    /**
     * extraSpace notes. 0 - should be fine for metal LAF 4 - should be fine for Nimbus
     *
     * TODO - really should find a proper fix for this.
     */
    extraSpace = 4;
    listener = new Listener();
    APIManager.addEventListener(listener);
    f = new JFrame(Config.getUs() + " - Buddy List");
    f.setLayout(new BorderLayout());

    // Change HIDE_ON_CLOSE or EXIT_ON_CLOSE when it works
    Tray.init();

    JMenuBar jmb = new JMenuBar();
    JMenu jmStatus = new JMenu(Language.langtext[1]);
    JMenu jmHelp = new JMenu(Language.langtext[2]);
    final JMenuItem jmiHelpLink = new JMenuItem(Language.langtext[11]);
    final JMenuItem jmiVersionName = new JMenuItem("Version");
    // note - the link is copiable so as to not open the link in the users normal browser automatically which could tip off anyone sniffing the network that they are using jtorcat
    jmiHelpLink.addActionListener(e -> {
      JTextField jtf = new JTextField();
      jtf.setEditable(false);
      jtf.setText("https://github.com/jtorchat/jtorchat/wiki");
      JOptionPane.showMessageDialog(null, jtf, "Wiki link", JOptionPane.PLAIN_MESSAGE);

    });
    jmHelp.add(jmiHelpLink);

    JMenuItem jmiLog = new JMenuItem(Language.langtext[12]);
    jmiLog.addActionListener(e -> GuiLog.getGuiLog().setVisible(!GuiLog.getGuiLog().isVisible()));
    jmHelp.add(jmiLog);

    // note - the link is copiable so as to not open the link in the users normal browser automatically which could tip off anyone sniffing the network that they are using jtorcat
    jmiVersionName.addActionListener(e -> {
      JTextField jtf = new JTextField();
      jtf.setEditable(false);
      jtf.setText(Config.CLIENT + " " + Config.VERSION);
      JOptionPane.showMessageDialog(null, jtf, "Version", JOptionPane.PLAIN_MESSAGE);

    });
    jmHelp.add(jmiVersionName);

    final JMenuItem jmiOnline = new JMenuItem(Language.langtext[7]);
    //See lang000007 property in a language .ini file --> Online property
    jmiOnline.addActionListener(onlineActionListener -> {
      Config.setUpdateStatus(TCPortStatus.AVAILABLE);
      TCPort.sendMyStatus();
    });
    jmStatus.add(jmiOnline);

    final JMenuItem jmiAway = new JMenuItem(Language.langtext[9]);
    //See lang000009 --> Away
    jmiAway.addActionListener(awayActionEvent -> {
      Config.setUpdateStatus(TCPortStatus.AWAY);
      TCPort.sendMyStatus();
    });
    jmStatus.add(jmiAway);

    final JMenuItem jmiXa = new JMenuItem(Language.langtext[10]);
    //See lang000010 --> Far away
    //TODO INSIGHT INTO WHAT XA MEANS: far away? (high latency)
    jmiXa.addActionListener(xaActionEvent -> {
      Config.setUpdateStatus(TCPortStatus.XA);
      TCPort.sendMyStatus();
    });
    jmStatus.add(jmiXa);

    JMenu jmFile = new JMenu(Language.langtext[0]);

    JMenuItem jmiAddContact = new JMenuItem(Language.langtext[3]);
    JMenuItem jmiPingAttack = new JMenuItem("Flooding ping attack");
    JMenuItem jmiSettings = new JMenuItem(Language.langtext[4]);
    JMenuItem jmiGUISettings = new JMenuItem(Language.langtext[80]);
    JMenuItem jmiProfileSettings = new JMenuItem(Language.langtext[81]);
    JMenuItem jmiExit = new JMenuItem(Language.langtext[5]);

    jmiAddContact.addActionListener(e -> {
      GuiContactAdd guica = new GuiContactAdd();
      // guica.setBuddy(null); // for when editing a buddy
      guica.setVisible(true);
    });

    jmiPingAttack.addActionListener(e -> {
      GuiPingAttack guica = new GuiPingAttack();
      // guica.setBuddy(null); // for when editing a buddy
      guica.setVisible(true);
    });

    jmiSettings.addActionListener(e -> {
      // JOptionPane.showMessageDialog(null, "Not Implemented", "Not Implemented", JOptionPane.ERROR_MESSAGE);
      GuiSettings guis = new GuiSettings();
      guis.setVisible(true);
    });
    jmiGUISettings.addActionListener(e -> {
      GuiIcon guis = new GuiIcon();
      guis.setVisible(true);
    });
    jmiProfileSettings.addActionListener(e -> {

      GuiProfile guis = new GuiProfile(null, true);
      guis.setVisible(true);
    });
    jmiExit.addActionListener(e -> {
      /**
       * We try to disconnect all our contacts
       * before exiting, this kills all process
       */

      BuddyList.disconnect_all(); //disconnect from all buddies to stop processes
      TorLoader.cleanUp(); //clean up tor process
      System.exit(0);
    });

    //jmFile.add(jmiPingAttack);
    jmFile.add(jmiAddContact);
    jmFile.add(jmiSettings);
    jmFile.add(jmiGUISettings);
    jmFile.add(jmiProfileSettings);
    jmFile.add(new JSeparator());
    jmFile.add(jmiExit);
    jmb.add(jmFile);
    jmb.add(jmStatus);
    jmb.add(jmHelp);
    f.setJMenuBar(jmb);

    JScrollPane jsp = new JScrollPane();
    jsp.setHorizontalScrollBar(null); // no horizontal scrollbar
    jt = new JTree();
    jsp.getViewport().add(jt);
    f.getContentPane().add(jsp, BorderLayout.CENTER);

    root = new DefaultMutableTreeNode("[root]");
    buddyNodeholy = new DefaultMutableTreeNode(Language.langtext[40]);
    buddyNodeon = new DefaultMutableTreeNode(Language.langtext[7]);
    buddyNode = new DefaultMutableTreeNode(Language.langtext[8]);
    buddyNodeblack = new DefaultMutableTreeNode(Language.langtext[39]);
    root.add(buddyNodeholy);
    root.add(buddyNodeon);
    root.add(buddyNode);
    root.add(buddyNodeblack);
    jt.setModel(new DefaultTreeModel(root));
    jt.setLargeModel(true);
    jt.setRootVisible(false);
    jt.setCellRenderer(new TCIconRenderer(jt));

    jt.setRowHeight(Config.getIcon_size() + Config.getIcon_space());
    ToolTipManager.sharedInstance().registerComponent(jt);

    jt.addMouseListener(new MouseListener() {

      @Override
      public void mouseClicked(MouseEvent e) {
      }

      @Override
      public void mousePressed(MouseEvent e) {
        int selRow = jt.getRowForLocation(e.getX(), e.getY());
        TreePath selPath = jt.getPathForLocation(e.getX(), e.getY());
        if (selRow != -1) {
          if (e.getClickCount() == 2 && !e.isPopupTrigger()) {
            doAction(selPath);
            // myDoubleClick(selRow, selPath);
          }
        }
        if (e.isPopupTrigger()) // works only here on Linux
        {
          doPopup(e);
        }
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) // works only here on Windows
        {
          doPopup(e);
        }

      }

      @Override
      public void mouseEntered(MouseEvent e) {
      }

      @Override
      public void mouseExited(MouseEvent e) {
      }

    });

    f.pack();
    if (h == -1) {
      h = f.getHeight();
    }
    f.setSize(w, h);
    f.setVisible(true);
  }

  public boolean isVisible() {
    return f.isVisible();
  }

  public void setVisible(boolean b) {
    f.setVisible(b);
  }

  private JMenuItem getNewMenuItem(String s, ActionListener al) {
    JMenuItem menuItem = new JMenuItem(s);
    menuItem.setActionCommand(s.toUpperCase());
    menuItem.addActionListener(al);
    return menuItem;
  }

  protected void doPopup(MouseEvent e) {
    final int x = e.getX();
    final int y = e.getY();
    JTree tree = (JTree) e.getSource();
    TreePath path = tree.getPathForLocation(x, y);
    if (path == null) {
      return;
    }

    tree.setSelectionPath(path);
    JPopupMenu popup = new JPopupMenu();
    DefaultMutableTreeNode d = (DefaultMutableTreeNode) path.getLastPathComponent();
    final Object o = d.getUserObject();
    if (o instanceof Buddy) {

      popup.add(getNewMenuItem(Language.langtext[73], new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          // String ac = e.getActionCommand();
          // TreePath path = jt.getPathForLocation(x, y);
          openChatWindow((Buddy) o);
        }
      }));

      popup.add(getNewMenuItem("Display Profile", new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          GuiProfile guis = new GuiProfile((Buddy) o, false);
          guis.setVisible(true);
        }
      }));

      if (Buddy.isInBlackList(((Buddy) o).getAddress())) {
        popup.add(getNewMenuItem(Language.langtext[75], new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            BuddyList.black.remove(((Buddy) o).getAddress());
            pardon(((Buddy) o));
          }
        }));
      } else {
        if (Buddy.isInHolyList(((Buddy) o).getAddress())) {

          popup.add(getNewMenuItem("Not Holy contact", new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
              BuddyList.holy.remove(((Buddy) o).getAddress());
              pardon(((Buddy) o));
            }
          }));
        } else {
          popup.add(getNewMenuItem("Holy contact.", new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
              BuddyList.addHoly(((Buddy) o));
            }
          }));

          popup.add(getNewMenuItem(Language.langtext[76], new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
              BuddyList.addBlack(((Buddy) o));
              if (getChatWindow(((Buddy) o), false, false) != null) {
                windowMap.remove(((Buddy) o).getAddress()).dispose();
              }
            }
          }));
        }
      }

      popup.add(getNewMenuItem(Language.langtext[77], new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          try {
            ((Buddy) o).remove();
          } catch (IOException e1) {
            e1.printStackTrace();
          }
        }
      }));

      popup.add(new JPopupMenu.Separator());
    }

    popup.show(tree, x, y);
  }

  private void doAction(TreePath x) {
    DefaultMutableTreeNode o = (DefaultMutableTreeNode) x.getLastPathComponent();
    if (o.getUserObject() instanceof Buddy) {
      Buddy b = (Buddy) o.getUserObject();
      openChatWindow(b);
    }
  }

  private void openChatWindow(Buddy b) {
    if (!Buddy.isInBlackList(((Buddy) b).getAddress())) {
      getChatWindow(b, true, true).toFront();
    }
  }

  public JMenu getFileMenu() {
    return f.getJMenuBar().getMenu(0);
  }

  private class Listener implements APIListener {

    @Override
    public void onStatusChange(Buddy buddy, byte newStatus, byte oldStatus) {
      jt.repaint();
      Logger.oldOut.println(
          buddy + " changed from " + Buddy.getStatusName(oldStatus) + " to " + Buddy
              .getStatusName(newStatus));

      if (newStatus == Status.ONLINE && oldStatus != newStatus) {
        if (Config.getAlert_on_status_change() == 1) {
          GuiAlert alert;
          alert = new GuiAlert(buddy.toString() + " is online.");
          alert.start();
        }
      }

      if (newStatus == Status.AWAY && oldStatus != newStatus) {
        if (Config.getAlert_on_status_change() == 1) {
          GuiAlert alert;
          alert = new GuiAlert(buddy.toString() + " is away.");
          alert.start();
        }
      }

      if (newStatus == Status.XA && oldStatus != newStatus) {
        if (Config.getAlert_on_status_change() == 1) {
          GuiAlert alert;
          alert = new GuiAlert(buddy.toString() + " is far away.");
          alert.start();
        }
      }

      if (newStatus >= Status.ONLINE && oldStatus <= Status.HANDSHAKE) {

        if (!BuddyList.black.containsKey(buddy.getAddress())) {
          MutableTreeNode node = nodeMap.remove(buddy.getAddress());
          if (node != null) // remove entry in the gui
          {
            ((DefaultTreeModel) jt.getModel()).removeNodeFromParent(node);
          }

          node = nodeMap.get(buddy.getAddress());
          if (node != null) {
            node.removeFromParent();
          }
          nodeMap.put(buddy.getAddress(), node = new DefaultMutableTreeNode(buddy));

          if (Buddy.isInHolyList(((Buddy) buddy).getAddress())) {
            if (buddy.getAddress().equals(Config.getUs())) {
              ((DefaultTreeModel) jt.getModel()).insertNodeInto(node, buddyNodeholy, 0);
            } else {
              ((DefaultTreeModel) jt.getModel())
                  .insertNodeInto(node, buddyNodeholy, buddyNodeholy.getChildCount());
            }

            if (buddyNodeholy.getChildCount() == 1) {
              jt.expandRow(0);
            }
          } else {
            if (buddy.getAddress().equals(Config.getUs())) {
              ((DefaultTreeModel) jt.getModel()).insertNodeInto(node, buddyNodeon, 0);
            } else {
              ((DefaultTreeModel) jt.getModel())
                  .insertNodeInto(node, buddyNodeon, buddyNodeon.getChildCount());
            }

            if (buddyNodeon.getChildCount() == 1) {
              jt.expandRow(0);
            }
          }

          if (new File(Config.getMessageDir() + buddy.getAddress() + ".txt").exists()) {
            try {
              Scanner sc = new Scanner(new FileInputStream(
                  Config.getMessageDir() + buddy.getAddress() + ".txt"));
              while (sc.hasNextLine()) {
                try {
                  buddy.sendMessage(sc.nextLine());
                } catch (IOException ioe) {
                  buddy.disconnect();
                  break;
                }
              }
              sc.close();
              new File(Config.getMessageDir() + buddy.getAddress() + ".txt").delete();
              getChatWindow(buddy, true, true).append("Time Stamp", "Delayed messages sent.\n");
            } catch (IOException ioe) {
              ioe.printStackTrace();
            }
          }

        }

      } else if (oldStatus >= Status.ONLINE && newStatus <= Status.HANDSHAKE) {

        if (Config.getAlert_on_status_change() == 1) {
          GuiAlert alert;
          alert = new GuiAlert(buddy.toString() + " is offline");
          alert.start();
        }

        if (!BuddyList.black.containsKey(buddy.getAddress())) {
          MutableTreeNode node = nodeMap.remove(buddy.getAddress());
          if (node != null) // remove entry in the gui
          {
            ((DefaultTreeModel) jt.getModel()).removeNodeFromParent(node);
          }

          node = nodeMap.get(buddy.getAddress());
          if (node != null) {
            node.removeFromParent();
          }
          nodeMap.put(buddy.getAddress(), node = new DefaultMutableTreeNode(buddy));

          if (buddy.getAddress().equals(Config.getUs())) {
            ((DefaultTreeModel) jt.getModel()).insertNodeInto(node, buddyNode, 0);
          } else {
            ((DefaultTreeModel) jt.getModel())
                .insertNodeInto(node, buddyNode, buddyNode.getChildCount());
          }

          if (buddyNode.getChildCount() == 1) {
            jt.expandRow(0);
          }
        }
      }

    }

    @Override
    public void onProfileNameChange(Buddy buddy, String newName, String oldName) {
      jt.repaint();
      jt.setCellRenderer(null); // this is stupid, but it works
      jt.setCellRenderer(new TCIconRenderer(jt));
    }

    @Override
    public void onProfileTextChange(Buddy buddy, String newText, String oldText) {

    }

    @Override
    public void onAddMe(Buddy buddy) {
      jt.repaint();
    }

    @Override
    public void onMessage(Buddy buddy, String s) {
      GuiChatWindow w = getChatWindow(buddy, true, true);
      String msg = s.trim().replaceAll("\\\\n", "\n").replaceAll("\r", "");

      boolean right = true;
      if (msg.startsWith("/")) {
        right = list_of_commands.out_command(buddy, msg, w, false);
      } else if (msg.startsWith("[Delayed] /")) {
        right = list_of_commands.out_command(buddy, msg.substring(10), w, true);
      }

      if (right) {
        ChatWindow
            .update_window(MessageType.RECEIVE_NORMAL, w, msg, null, "", !buddy.isFullyConnected());
      }

      if (Config.getAlert_on_message() == 1) {
        if (!w.isFocused() && !(Buddy.isInBlackList(((Buddy) buddy).getAddress()))) {
          if (alert != null && !alert.isFinished()) {
            alert.kill();
          }
          alert = new GuiAlert("New Message: " + buddy.toString());
          alert.start();
        }
      }

    }

    @Override
    public void onBuddyRemoved(Buddy buddy) {
      MutableTreeNode node = nodeMap.remove(buddy.getAddress());
      if (node != null) // remove entry in the gui
      {
        ((DefaultTreeModel) jt.getModel()).removeNodeFromParent(node);
      }
      if (getChatWindow(buddy, false, false) != null) {
        windowMap.remove(buddy.getAddress()).dispose();
      }
    }

    @Override
    public void onNewBuddy(Buddy buddy) {
      MutableTreeNode node = nodeMap.get(buddy.getAddress());
      if (node != null) {
        node.removeFromParent();
      }
      nodeMap.put(buddy.getAddress(), node = new DefaultMutableTreeNode(buddy));

      if (buddy.getAddress().equals(Config.getUs())) {
        ((DefaultTreeModel) jt.getModel()).insertNodeInto(node, buddyNode, 0);
      } else {
        ((DefaultTreeModel) jt.getModel())
            .insertNodeInto(node, buddyNode, buddyNode.getChildCount());
      }

      if (buddyNode.getChildCount() == 1) {
        jt.expandRow(0);
      }
    }
  }

}
