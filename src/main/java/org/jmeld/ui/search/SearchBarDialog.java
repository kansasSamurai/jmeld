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
package org.jmeld.ui.search;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jmeld.ui.AbstractBarDialog;
import org.jmeld.ui.JMeldPanel;
import org.jmeld.ui.util.ImageUtil;
import org.jmeld.util.StringUtil;

/**
 * An extension of JPanel that implements the "Search" feature
 * 
 * @author jmeld-legacy
 * @author Rick Wellman
 *
 */
@SuppressWarnings({"serial", "unused"})
public class SearchBarDialog extends AbstractBarDialog {
    
  // class variables:
  private static final String CP_FOREGROUND = "JMeld.foreground";
  private static final String CP_BACKGROUND = "JMeld.background";

  // Instance variables:
  private Timer timer;
  private JLabel searchResult; // icon + "Phrase not found"
  private JTextField searchField;

  public SearchBarDialog(JMeldPanel meldPanel) {
    super(meldPanel);
  }

  protected void init() {
      
    JButton closeButton;
    JButton previousButton;
    JButton nextButton;

    setLayout(new FlowLayout(FlowLayout.LEADING));

    // Close the search dialog:
    closeButton = new JButton(ImageUtil.getImageIcon("jmeld_close"));
    closeButton.setRolloverIcon(ImageUtil.getImageIcon("jmeld_close-rollover"));
    closeButton.setPressedIcon(ImageUtil.getImageIcon("jmeld_close-pressed"));
    closeButton.addActionListener(getCloseAction());
    initButton(closeButton, true);

    // Incremental search:
    searchField = new JTextField(15);
    searchField.getDocument().addDocumentListener(getSearchAction());
    searchField.addKeyListener(getSearchKeyAction());

    // Find previous match:
    previousButton = new JButton("Previous", ImageUtil .getImageIcon("stock_data-previous"));
    previousButton.addActionListener(getPreviousAction());
    initButton(previousButton, false);

    // Find next match:
    nextButton = new JButton("Next", ImageUtil.getImageIcon("stock_data-next"));
    nextButton.addActionListener(getNextAction());
    initButton(nextButton, false);

    searchResult = new JLabel();

    add(closeButton);
    add(Box.createHorizontalStrut(5));
    add(new JLabel("Find:"));
    add(searchField);
    add(previousButton);
    add(nextButton);
    add(Box.createHorizontalStrut(10));
    add(searchResult);

    timer = new Timer(500, executeSearch());
    timer.setRepeats(false);
  }

  private void initButton(AbstractButton button, boolean setNullBorder) {
    button.setFocusable(false);
    button.setBorderPainted(false);    
    button.setBorder(setNullBorder ? null : new EmptyBorder(0, 5, 0, 5));
  }

  public SearchCommand getCommand() {
    return new SearchCommand(searchField.getText(), false);
  }

  public void setSearchText(String searchText) {
      
    if (StringUtil.isEmpty(searchText)) {
      return;
    }

    // You can start a search by selecting some text and then
    //   hit CTRL-F. But if you have selected more than x 
    //   characters you probably don't want to search for that.
    //   So I choose to ignore those texts.
    if (searchText.length() > 50) {
      return;
    }

    searchField.setText(searchText);
  }

  @Override
  public void _activate() {
    
    searchField.requestFocus();
    searchField.selectAll();

    if (!StringUtil.isEmpty(searchField.getText())) {
      timer.restart();
    }
    
  }

  @Override
  public void _deactivate() {
  }

  private DocumentListener getSearchAction() {
    return new DocumentListener() {
        
      public void changedUpdate(DocumentEvent e) {
        timer.restart();
      }

      public void insertUpdate(DocumentEvent e) {
        timer.restart();
      }

      public void removeUpdate(DocumentEvent e) {
        timer.restart();
      }
      
    };
  }

  private ActionListener executeSearch() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
          
        final String searchText = searchField.getText();
        final SearchHits searchHits = getMeldPanel().doSearch(null);
        
        final boolean notFound = (searchHits == null || searchHits.getSearchHits().size() == 0);
        if (notFound) {
            
          // I would love to set the background to red and foreground
          //   to white but the jdk won't let me set the background if
          //   GTK look&feel is chosen.
          if (searchField.getForeground() != Color.red) {
              
            // Remember the original colors:
            searchField.putClientProperty(CP_FOREGROUND, searchField.getForeground());

            // Set the new colors:
            searchField.setForeground(Color.red);
          }

          searchResult.setIcon(ImageUtil.getImageIcon("bullet-warning"));
          searchResult.setText("Phrase not found");
          
        } else {
            
          // Set the original colors:
          final Color color = (Color) searchField.getClientProperty(CP_FOREGROUND);
          if (color != null) {
            searchField.setForeground(color);
            searchField.putClientProperty(CP_FOREGROUND, null);
          }

          if (!StringUtil.isEmpty(searchResult.getText())) {
            searchResult.setIcon(null);
            searchResult.setText("");
          }
          
        }
      }
    };
  }

  private KeyListener getSearchKeyAction() {
    return new KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          getMeldPanel().doNextSearch(null);
        }
      }
    };
  }

  private ActionListener getCloseAction() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        getMeldPanel().doStopSearch(null);
      }
    };
  }

  private ActionListener getPreviousAction() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        getMeldPanel().doPreviousSearch(null);
      }
    };
  }

  private ActionListener getNextAction() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        getMeldPanel().doNextSearch(null);
      }
    };
  }
  
}
