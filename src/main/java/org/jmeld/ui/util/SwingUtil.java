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

import org.jmeld.ui.action.MeldAction;

import javax.swing.*;

/**
 * 
 * @author jmeld-legacy 
 * @author Rick Wellman
 *
 */
public class SwingUtil {
    
    private SwingUtil() {
    }

    public static void installKey(JComponent component, String key, MeldAction action) {
        
        final KeyStroke stroke = KeyStroke.getKeyStroke(key);

        final InputMap inputMap = component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        if (inputMap.get(stroke) != action.getName()) {
            inputMap.put(stroke, action.getName());
        }

        final ActionMap actionMap = component.getActionMap();
        if (actionMap.get(action.getName()) != action) {
            actionMap.put(action.getName(), action);
        }
        
    }

    public static void deInstallKey(JComponent component, String key, MeldAction action) {

        final KeyStroke stroke = KeyStroke.getKeyStroke(key);

        final InputMap inputMap = component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.remove(stroke);

        // Do not deinstall the action because I don't know how many other
        // inputmap residents will call the action.
        // ActionMap actionMap;
    }
    
}
