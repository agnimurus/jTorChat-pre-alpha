package gui;

import core.Config;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import lombok.NonNull;
import util.LogWriter;

@SuppressWarnings("serial")
public class GuiLog extends JFrame {

  // public static long lastclear = System.currentTimeMillis() / 1000;


  private static GuiLog guiLog;

  private static final Object LOCK = new Object();//Used for thread syncronization

  static {
    Gui.setLookAndFeel("Nimbus");
    //FIXME determine if this is a bug. I think it is. --> guiLog = new GuiLog();
//		guiLog.setVisible(true);
  }

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
  private JTextPane logTextPane;

  public GuiLog() {
    initComponents();
    setSize(755, 402);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    logTextPane.setEditable(false);
    initDocument();
  }

  /**
   * Returns GuiLog instance, or throws a NullPointer exception if it's null
   *
   * @return private static instance of GuiLog
   * @throws NullPointerException if guiLog is null
   */
  public static GuiLog getGuiLog() throws NullPointerException {
    if (guiLog == null) {
      throw new NullPointerException("GuiLog is Null");
    }
    return guiLog;
  }

  /**
   * Sets internal guiLog to the passed log.
   *
   * @param guiLog : The new log to reference
   */
  public static void setGuiLog(GuiLog guiLog) {
    GuiLog.guiLog = guiLog;
  }

  /**
   * Initialized the private GuiLog guiLog and returns it.
   *
   * @return private static GuiLog guiLog
   */
  public static GuiLog initLogInstance() {
    guiLog = new GuiLog();
    return guiLog;
  }

  /**
   * Append {@code text} to the {@code logTextPane} using {@code style}.
   * {@code style} is the string representation of the format passed to the logTextPane's default-styled document
   *    (  {@code document.getStyle(style)}  )
   */
  public static void append(String text, @NonNull String style) {
    synchronized (LOCK) {
      DefaultStyledDocument defaultStyledDocument = (DefaultStyledDocument) guiLog.logTextPane
          .getDocument();
      try {
        int offset = defaultStyledDocument.getLength();
        AttributeSet attributes = defaultStyledDocument.getStyle(style);

        defaultStyledDocument.insertString(offset, text, attributes);
        trimText();
        guiLog.logTextPane.setCaretPosition(
            defaultStyledDocument.getLength()); // TODO: Determine if this is necessary
      } catch (BadLocationException ble) {
        ble.printStackTrace();
      }
    }
  }

  /**
   * Append {@code text} to the {@code logTextPane} without a special style.
   * @param text
   */
  public static void append(String text) {
    append(text, null); // Append text without a style
  }

  public static void updateErr(String errorString) {
    String style = "Err"; // To maintain easy readability
    append(errorString, style);
  }

  public static void updateOut(String update) {
    append(update);
  }

  /**
   * Trims the text shown in {@code logTextPane}
   */
  private static void trimText() {
    // TODO: replace hard coded values with values from a config

    if (Config.getFulllog() == 0) {
      if (guiLog.logTextPane.getDocument().getLength() > 10000) {
        guiLog.logTextPane.setCaretPosition(0);
        int i = guiLog.logTextPane.getText().indexOf("\n");
        try {
          guiLog.logTextPane.getDocument().remove(0, i + 1);
          // trimText(); // Fixed by moving to try block then commenting out 10-04-2018: FIXME: possible recursive bug causing an infinitely broken call if exception is caught
        } catch (BadLocationException e) {
          e.printStackTrace();
        }
      }
    }
  } // See TODO

  private void initDocument() {
    DefaultStyledDocument defaultStyledDocument = (DefaultStyledDocument) logTextPane.getDocument();

    Style timestampStyle = defaultStyledDocument.addStyle("Time Stamp", null);
    StyleConstants.setForeground(timestampStyle, Color.gray.darker());
    Style theirNameStyle = defaultStyledDocument.addStyle("Err", null);
    StyleConstants.setForeground(theirNameStyle, Color.red.darker());

    Style classC = defaultStyledDocument.addStyle("Class-c", null);
    StyleConstants.setForeground(classC, Color.green.darker());

    Style classT = defaultStyledDocument.addStyle("Class-t", null);
    StyleConstants.setForeground(classT, Color.green.darker().darker().darker());
  }

  private void save() {
    LogWriter.logWrite(guiLog.logTextPane.getText());
  }

  private void clear() {
    guiLog.logTextPane.setCaretPosition(0);
    guiLog.logTextPane.setDocument(new DefaultStyledDocument());
    initDocument();
    System.gc(); // ask the jvm to collect garbage
  }

  private void close() {
    guiLog.setVisible(false);
  }

  private void initComponents() {
    // TODO: replace hard coded values with config

    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    // Generated using JFormDesigner Evaluation license - dfddfd dfdfdf
    logActionsMenuBar = new JMenuBar();
    saveMenuItem = new JMenuItem();
    clearMenuItem = new JMenuItem();
    closeMenuItem = new JMenuItem();
    containerScrollPane = new JScrollPane();
    logTextPane = new JTextPane();

    //======== this ========
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout(8, 8));

    //======== logActionsMenuBar ========
    {

      //---- saveMenuItem ----
      saveMenuItem.setText("Save");
      saveMenuItem.setHorizontalTextPosition(SwingConstants.CENTER);
      saveMenuItem.setHorizontalAlignment(SwingConstants.CENTER);
      saveMenuItem.addActionListener(actionEvent -> save());
      logActionsMenuBar.add(saveMenuItem);

      //---- clearMenuItem ----
      clearMenuItem.setText("Clear");
      clearMenuItem.setHorizontalTextPosition(SwingConstants.CENTER);
      clearMenuItem.setHorizontalAlignment(SwingConstants.CENTER);
      clearMenuItem.addActionListener(actionEvent -> clear());
      logActionsMenuBar.add(clearMenuItem);

      //---- closeMenuItem ----
      closeMenuItem.setText("Close");
      closeMenuItem.setHorizontalTextPosition(SwingConstants.CENTER);
      closeMenuItem.setHorizontalAlignment(SwingConstants.CENTER);
      closeMenuItem.addActionListener(actionEvent -> close());
      logActionsMenuBar.add(closeMenuItem);
    }
    setJMenuBar(logActionsMenuBar);

    //======== containerScrollPane ========
    containerScrollPane.setViewportView(logTextPane);

    contentPane.add(containerScrollPane, BorderLayout.CENTER);
    pack();
    setLocationRelativeTo(getOwner());
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }
  // JFormDesigner - End of variables declaration  //GEN-END:variables


}


