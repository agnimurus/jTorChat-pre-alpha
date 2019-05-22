package gui.log;

import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class LogTextPane extends JTextPane {

  /**
   * Adds Log Legend to Text Pane (kind of like a style guide)
   */
  void addLogLegend() {
    StyledDocument document = (StyledDocument) this.getDocument();

    // Second parameter of style calls is the parent style.
    // Custom styles set this to `null`

    Style timestampStyle = document.addStyle("Time Stamp", null);
    StyleConstants.setForeground(timestampStyle, Color.gray.darker());

    Style theirNameStyle = document.addStyle("Err", null);
    StyleConstants.setForeground(theirNameStyle, Color.red.darker());

    Style classC = document.addStyle("Class-c", null);
    StyleConstants.setForeground(classC, Color.green.darker());

    Style classT = document.addStyle("Class-t", null);
    StyleConstants.setForeground(classT, Color.green.darker().darker().darker());
  }
}
