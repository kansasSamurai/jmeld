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

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jmeld.JMeld;
import org.jmeld.settings.JMeldSettings;
import org.jmeld.util.ObjectUtil;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jidesoft.plaf.LookAndFeelFactory;

/**
 * 
 * @author jmeld-legacy
 * @author Rick Wellman committed
 *
 */
public class LookAndFeelManager {

    // Class variables:
    private static LookAndFeelManager instance;

    private LookAndFeelManager() {
        init();
    }

    public static LookAndFeelManager getInstance() {
        if (instance == null) {
            instance = new LookAndFeelManager();
        }
        return instance;
    }

    private void init() {
        try {
            PlasticLookAndFeel.setTabStyle(PlasticLookAndFeel.TAB_STYLE_METAL_VALUE);
            System.setProperty(PlasticLookAndFeel.DEFAULT_THEME_KEY, "MySkyBluer");
            UIManager.installLookAndFeel("JGoodies Plastic 3D", "com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void install() {
        String lookAndFeelName;
        String lookAndFeelClassName;
        Component root;

        try {
            lookAndFeelClassName = getDefaultLookAndFeelClassName();

            // Try the preferred look and feel:
            lookAndFeelName = JMeldSettings.getInstance().getEditor().getLookAndFeelName();
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (ObjectUtil.equals(info.getName(), lookAndFeelName)) {
                    lookAndFeelClassName = info.getClassName();
                    break;
                }
            }

            UIManager.setLookAndFeel(lookAndFeelClassName);
            LookAndFeelFactory.installJideExtension();

            root = SwingUtilities.getRoot(JMeld.getJMeldPanel());
            if (root != null) {
                SwingUtilities.updateComponentTreeUI(root);
            }
        } catch (Exception e) {
        }
    }

    public String getInstalledLookAndFeelName() {
        LookAndFeel lf;

        lf = UIManager.getLookAndFeel();

        // WATCH OUT:
        // The lookandfeel can be registered in the UIManager with a different
        // name than the lookAndFeel.getName() (Is this a bug?)

        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if (ObjectUtil.equals(info.getClassName(), lf.getClass().getName())) {
                return info.getName();
            }
        }

        // This should never happen!
        return lf.getName();
    }

    private String getDefaultLookAndFeelClassName() {
        if (System.getProperty("java.version").startsWith("1.7")) {
            return UIManager.getSystemLookAndFeelClassName();
        }

        return Plastic3DLookAndFeel.class.getName();
    }

    public List<String> getInstalledLookAndFeels() {
        List<String> result;

        result = new ArrayList<String>();
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            result.add(info.getName());
        }

        return result;
    }
}
