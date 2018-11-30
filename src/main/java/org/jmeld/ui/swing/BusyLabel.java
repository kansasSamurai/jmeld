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
package org.jmeld.ui.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.Timer;

/**
 * Provides the "spinner" icon in the bottom right of the status bar
 * 
 * @author jmeld-legacy
 * @author Rick Wellman
 * 
 */
@SuppressWarnings("serial")
public class BusyLabel extends JLabel {
    
  // Instance variables:
  private Timer timer;
  private boolean busy;
  private static BusyIcon icon;

  public BusyLabel() {

      if (icon == null) {
          icon = new BusyIcon();
      }

      timer = new Timer(125, busy());
      timer.setRepeats(false);
  }

  public void start() {
    busy = true;
    this.setIcon(icon);
    timer.restart();
  }

  public void stop() {
      this.setIcon(null);
      busy = false;
  }

  /**
   * Creates the ActionListener for the Timer
   * 
   * @return
   */
  private ActionListener busy() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        if (busy) {
          icon.roll();
          repaint();
          timer.restart();
        } else {
          icon.stop();
          repaint();
        }
      }
    };
  }

  class BusyIcon implements Icon {
      
    private int startIndex;
    List<Color> colors;

    BusyIcon() {
      colors = new ArrayList<Color>();
      colors.add(new Color(178, 178, 178));
      colors.add(new Color(153, 153, 153));
      colors.add(new Color(128, 128, 128));
      colors.add(new Color(102, 102, 102));
      colors.add(new Color(51, 51, 51));
      colors.add(new Color(26, 26, 26));
      colors.add(new Color(0, 0, 0));
      colors.add(new Color(0, 0, 0));
    }

    void setIndex(int startIndex) {
      this.startIndex = startIndex;
    }

    public void stop() {
      startIndex = 0;
    }

    public void roll() {
      startIndex--;
      if (startIndex < 0) {
        startIndex = colors.size() - 1;
      }
    }

    @Override
    public int getIconWidth() {
      return 16;
    }

    @Override
    public int getIconHeight() {
      return 16;
    }

    @Override
    public void paintIcon(Component component, Graphics g, int x, int y) {
      
        final int[] xpos = {10, 12, 10,  6,  2, 0, 2, 6};
        final int[] ypos = { 2,  6, 10, 12, 10, 6, 2, 0};
        
        int tx = 0;
        int ty = 0;
        for (int i = 0; i < 8; i++) {

            final Color c = busy
                ? colors.get((i + startIndex) % 8)
                : colors.get(0)
                ;

            tx = xpos[i];
            ty = ypos[i];

            g.setColor(c);
            g.drawLine(x + tx + 0, y + ty + 1, x + tx + 0, y + ty + 2);
            g.drawLine(x + tx + 1, y + ty + 0, x + tx + 1, y + ty + 3);
            g.drawLine(x + tx + 2, y + ty + 0, x + tx + 2, y + ty + 3);
            g.drawLine(x + tx + 3, y + ty + 1, x + tx + 3, y + ty + 2);
        
        }
    } // end method paintIcon()
    
  } // end class BusyIcon
  
}
