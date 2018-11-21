/*
   
   JWeld - A diff and merge API plus GUI - Originally forked from JMeld
   Copyright (C) 2018  Rick Wellman - GNU LGPL
   
   This library is free software and has been modified according to the permissions 
   granted below; this version of the library continues to be distributed under the terms of the
   GNU Lesser General Public License version 2.1 as published by the Free Software Foundation
   and may, therefore, be redistributed or further modified under the same terms as the original.
   
   -----
   JMeld is a visual diff and merge tool.
   Copyright (C) 2007  Kees Kuip - GNU LGPL
   
   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2.1 of the License, or (at your option) any later version.
   
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
   
   See the GNU Lesser General Public License for more details.
   
   You should have received a copy of the GNU Lesser General 
   Public License along with this library; if not, write to:
   Free Software Foundation, Inc.
   51 Franklin Street, Fifth Floor
   Boston, MA  02110-1301  USA
   
 */
package org.jmeld.ui;

import org.jmeld.ui.util.ImageUtil;
import org.jmeld.ui.util.ToolBarBuilder;

import javax.swing.*;

/**
 * A type of "status bar" for the main FilePanel (a.k.a. FileViewer)
 * <p>
 * Includes such information as:<ul>
 * <li>Line x of y</li>
 * <li>Column</li>
 * <li>Visual indicator that current file panel has the focus</li>
 * 
 * @author jmeld-legacy
 * @author Rick Wellman
 *
 */
@SuppressWarnings("serial")
public class FilePanelBar extends JPanel {
    
    /** A reference to the FilePanel being statused/decorated */
    private FilePanel filePanel;
    private JLabel selected;
    private JLabel lineNumber;
    private JLabel columnNumber;
    private ImageIcon iconSelected;
    private ImageIcon iconNotSelected;

  public FilePanelBar(FilePanel filePanel) {
    this.filePanel = filePanel;

    init();
  }

  private void init() {
      
    selected = new JLabel();
    lineNumber = new JLabel();
    columnNumber = new JLabel();

    final ToolBarBuilder builder = new ToolBarBuilder(this);
    builder.addComponent(selected);
    builder.addSpring();
    builder.addComponent(lineNumber);
    builder.addSeparator();
    builder.addComponent(columnNumber);

    iconSelected = ImageUtil.getImageIcon("panel-selected");
    iconNotSelected = ImageUtil.createTransparentIcon(iconSelected);

    update();
  }

  public void update() {
      
    Icon icon;
    JTextArea editor;
    int caretPosition;
    String text;
    int line;
    int column;

    icon = filePanel.isSelected() ? iconSelected : iconNotSelected;
    if (selected.getIcon() != icon)
    {
      selected.setIcon(icon);
    }

    editor = filePanel.getEditor();
    caretPosition = editor.getCaretPosition();
    try {
      line = editor.getLineOfOffset(caretPosition);
    } catch (Exception ex) {
      line = -1;
    }

    try {
      column = caretPosition - editor.getLineStartOffset(line);
    } catch (Exception ex) {
      column = -1;
    }

    text = String.format("Line: %05d/%05d", line + 1, editor.getLineCount());
    lineNumber.setText(text);

    text = String.format("Column: %03d", column);
    columnNumber.setText(text);
  }
  
}
