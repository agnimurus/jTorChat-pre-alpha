package gui.log;

import gui.components.MenuItemFactory;
import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.text.DefaultStyledDocument;
import util.LogWriter;

public class LogView extends JFrame {

  /**
   * Singleton Instance
   */
  private static LogView ourInstance = new LogView();

  /**
   * Menu bar for the GuiLog Window. Contains "Save", "Clear", and "Close" actions.
   */
  private JMenuBar logActionsMenuBar;
  /**
   * Saves the Log
   */
  private JMenuItem saveMenuItem;
  /**
   * Clears the log
   */
  private JMenuItem clearMenuItem;
  /**
   * Closes the Log
   */
  private JMenuItem closeMenuItem;
  /**
   * Container for logTextPane
   */
  private JScrollPane containerScrollPane;
  /**
   * Displays the Log in a JTextPane
   */
  private LogTextPane logTextPane;

  /**
   * The content pane for this display
   */
  private Container contentPane;

  private MenuItemFactory menuItemFactory = new MenuItemFactory();

  public static LogView getInstance() {
    return ourInstance;
  }

  private LogView() {
    initComponents();
    initWindow();
  }

  /**
   * Initializes the following: - Menu Bar - Menu Items - Scroll Pane - Log Text Pane
   */
  private void initComponents() {
    logTextPane = new LogTextPane(); // Avoid possibility of race-condition by initing before menu
    logTextPane.setEditable(false); // Logs are read-only
    initMenu();
    initViewPort();
  }

  /**
   * Initializes the actual window to display - Disposes on Close - Positioned relative to parent
   */
  private void initWindow() {
    // TODO Read dimensions from config file
    int windowWidth = 750, windowHeight = 400;
    this.setSize(windowWidth, windowHeight);
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.setJMenuBar(logActionsMenuBar);
    this.pack();
    this.setLocationRelativeTo(this.getOwner());
    this.setVisible(true);
  }

  /**
   * Inits menu bar with Save, Close, and Clear Actions
   */
  private void initMenu() {
    logActionsMenuBar = new JMenuBar();

    // TODO load values from config (with internationalization)
    // TODO log writing logic is copied from eventual. Refactor
    closeMenuItem = menuItemFactory
        .create("Close", actionEvent -> logTextPane.setVisible(false));
    clearMenuItem = menuItemFactory
        .create("Clear", actionEvent -> {
          logTextPane.setCaretPosition(0); // Position caret at beginning
          logTextPane.setDocument(new DefaultStyledDocument()); // clear document
          logTextPane.addLogLegend(); // for user's convenience
        });
    saveMenuItem = menuItemFactory
        .create("Close", actionEvent -> LogWriter.logWrite(logTextPane.getText()));

    logActionsMenuBar.add(saveMenuItem);
    logActionsMenuBar.add(closeMenuItem);
    logActionsMenuBar.add(clearMenuItem);
  }

  /**
   * Initializes the container view for logTextPane
   */
  private void initViewPort() {
    containerScrollPane = new JScrollPane();
    containerScrollPane.setViewportView(logTextPane);
    initContentPane();
  }

  /**
   * Adds `containerScrollPane` to this window's content pane (with an 8x8px border)
   */
  private void initContentPane() {
    contentPane = this.getContentPane();
    // TODO read values from config
    int horizGap = 8, vertGap = 8;
    contentPane.setLayout(new BorderLayout(horizGap, vertGap));
    contentPane.add(containerScrollPane, BorderLayout.CENTER);
  }

}
