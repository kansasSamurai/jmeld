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
package org.jmeld;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import jiconfont.swing.IconFontSwing;
import jiconfont.icons.FontAwesome;

import org.jmeld.settings.JMeldSettings;
import org.jmeld.ui.JMeldPanel;
import org.jmeld.ui.util.ImageUtil;
import org.jmeld.ui.util.LookAndFeelManager;
import org.jmeld.util.prefs.WindowPreference;

/**
 * 
 * @author jmeld-legacy
 * @author Rick Wellman
 *
 */
public class JMeld implements Runnable {
    
    private List<String> fileNameList;

    // Singleton
    private static JMeldPanel jmeldPanel;

    public JMeld(String[] args) {
        //TODO: parse options (showTree show levenstein
        fileNameList = new ArrayList<String>();
        for (String arg : args) {
            fileNameList.add(arg);
        }
    }

    public static JMeldPanel getJMeldPanel() {
        return jmeldPanel;
    }

    /**
     * Runs on the EDT when invoked via SwingUtilities.invokeLater() < (as it should be)
     */
    public void run() {

        // This must be called before showing the JFrame (so just do it before *everything*)
        LookAndFeelManager.getInstance().install();

        jmeldPanel = new JMeldPanel();

        final JFrame frame = new JFrame("JMeld");
        frame.add(jmeldPanel);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setIconImage(ImageUtil.getImageIcon("jmeld-small").getImage());
        new WindowPreference(frame.getTitle(), frame);
        frame.addWindowListener(jmeldPanel.getWindowListener());
        frame.setVisible(true);
        frame.toFront();

        jmeldPanel.openComparison(fileNameList);
    }

    public static void main(String[] args) {
        
        final JMeldSettings settings = JMeldSettings.getInstance();        
        settings.getEditor().setShowLineNumbers(true);
        if (settings.getEditor().isAntialiasEnabled()) {
            System.setProperty("swing.aatext", "true");
        }
        // TODO Bug: if setDrawCurves is true, F7/F8 do not work correctly
        // Actually, it seems to manifest itself even when false...
        // the JSplitPane appears to get focus when hitting F7/F8 instead of the DiffScrollComponent
        // settings.setDrawCurves(false); // original setting is:  true
        // settings.setCurveType(1);
        
        IconFontSwing.register(FontAwesome.getIconFont());

        SwingUtilities.invokeLater(new JMeld(args));
    }
    
}
