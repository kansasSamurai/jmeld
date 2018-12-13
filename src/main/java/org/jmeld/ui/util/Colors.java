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

package org.jmeld.ui.util;

import javax.swing.*;
import java.awt.*;

/**
 * 
 * @author jmeld-legacy
 * @author Rick Wellman
 *
 */
public class Colors {
    
    public static final Color ADDED = new Color(180, 255, 180);
    public static final Color CHANGED = Color.orange; // new Color(160, 200, 255);
    public static final Color CHANGED_LIGHTER = getChangedLighterColor(CHANGED);  // <<< this color does not appear to affect the UI; superceded by JMHighlightPainter.initConfiguration()?
    public static final Color DELETED = new Color(255, 160, 180);
    public static final Color DND_SELECTED_NEW = new Color(13, 143, 13);
    public static final Color DND_SELECTED_USED = new Color(238, 214, 128);

    private static final Colors singleton = new Colors();
    private Color selectionColor;
    private Color panelBackground;
    private Color darkLookAndFeelColor;
    private Color tableRowHighLighterColor;
    
    // private constructor for singleton pattern
    private Colors() {}
    
    /**
     * This should probably be better named as getChangedHIGHLIGHTColor().
     * 
     * The old algorithm attempted to change the actual color...
     * The new algorithm uses a change in the alpha channel now that we are using translucent colors.
     * 
     * @param changedColor
     * @return
     */    
    public static Color getChangedLighterColor(Color changedColor) {
        Color c = changedColor;

        boolean newalgorithm = true;        
        if (newalgorithm) {
            
        } else {
            // Color c = changedColor;
            c = ColorUtil.brighter(c);
            c = ColorUtil.brighter(c);
            c = ColorUtil.lighter(c);
            c = ColorUtil.lighter(c);            
        }

        return c;
    }

    /**
     * Get a highlighter that will match the current l&f.
     */
    public static Color getTableRowHighLighterColor() {
        if (singleton.tableRowHighLighterColor == null) {
            Color color = getSelectionColor();
            color = ColorUtil.setSaturation(color, 0.05f);
            color = ColorUtil.setBrightness(color, 1.00f);
            singleton.tableRowHighLighterColor = color;
        }

        return singleton.tableRowHighLighterColor;
    }

    public static Color getDarkLookAndFeelColor() {
        if (singleton.darkLookAndFeelColor == null) {
            Color color = getSelectionColor();
            color = ColorUtil.setBrightness(color, 0.40f);
            singleton.darkLookAndFeelColor = color;
        }

        return singleton.darkLookAndFeelColor;
    }

    @SuppressWarnings("rawtypes")
    public static Color getSelectionColor() {
        if (singleton.selectionColor == null) {
            // DO NOT USE UIManager to get colors because it is not lookandfeel independent! 
            // (Learned it the hard way with Nimbus L&F)
            singleton.selectionColor = new JList().getSelectionBackground();
        }
        return singleton.selectionColor;
    }

    public static Color getPanelBackground() {
        if (singleton.panelBackground == null) {
            // DO NOT USE UIManager to get colors because it is not lookandfeel independent! 
            // (Learned it the hard way with Nimbus L&F)
            singleton.panelBackground = new JPanel().getBackground();
        }
        return singleton.panelBackground;
    }
    
}
