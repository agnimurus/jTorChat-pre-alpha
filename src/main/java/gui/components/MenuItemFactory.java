package gui.components;

import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;

public class MenuItemFactory implements FactoryInterface<JMenuItem> {

  /**
   * Create A JMenuItem from title
   *
   *  Horizontal Text is center-positioned and center-aligned
   * @param descriptor title of menu item
   */
  @Override
  public JMenuItem create(String descriptor) {
    JMenuItem menuItem = new JMenuItem(descriptor);
    menuItem.setHorizontalAlignment(SwingConstants.CENTER);
    menuItem.setHorizontalTextPosition(SwingConstants.CENTER);
    return menuItem;
  }

  /**
   * Create A JMenuItem from title and action listener
   *
   *  Horizontal Text is center-positioned and center-aligned
   * @param descriptor title of menu item
   */
  public JMenuItem create(String descriptor, ActionListener onAction) {
    JMenuItem menuItem = this.create(descriptor);
    menuItem.addActionListener(onAction);
    return menuItem;
  }
}
