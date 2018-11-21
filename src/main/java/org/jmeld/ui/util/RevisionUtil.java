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

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.jmeld.diff.JMDelta;
import org.jmeld.settings.EditorSettings;
import org.jmeld.settings.JMeldSettings;

/**
 * A utility class for making UI "decisions" based on the JMRevision/Delta/Chunk model.
 * (currently, only JMDelta are supported but that may simply be due to unidentified use cases)
 * 
 * @author jmeld-legacy
 * @author Rick Wellman
 *
 */
public class RevisionUtil {
    
    // A "cache" for the darker colors to prevent recompute time
    private static Map<Color, Color> DARKER_MAP = new HashMap<Color, Color>();

    // A "cache" for the opaque colors to prevent recompute time
    private static Map<Color, Color> OPAQUE_MAP = new HashMap<Color, Color>();

    private static EditorSettings getSettings() {
        return JMeldSettings.getInstance().getEditor();
    }
    
    public static Color getColor(JMDelta delta) {
        
        if (delta.isDelete()) {
            return getSettings().getDeletedColor();
        }

        if (delta.isChange()) {
            return getSettings().getChangedColor();
        }

        return getSettings().getAddedColor();
    }

    public static Color getDarkerColor(JMDelta delta) {

        final Color c = getColor(delta);

        // If found in the cache, then return the cached value. If not, compute/cache/return.
        Color result = DARKER_MAP.get(c);
        if (result == null) {
//            result = c.darker().darker(); // .darker().darker().darker();
//            result = new Color(result.getRed(), result.getGreen(), result.getBlue(), result.getAlpha() + 128);
            result = new Color(8, 201, 136, 32); // <<< is this even being used? it does not look like it!
            DARKER_MAP.put(c, result);
        }

        return result;
    }

    /** 
     * Now that we are allowing/defaulting colors using alpha channels (translucency),
     * some components look better/require the opaque version of the color.
     * For example:  RevisionBar
     * 
     * @param delta
     * @return
     */
    public static Color getOpaqueColor(JMDelta delta) {
        final Color c = getColor(delta);

        // If found in the cache, then return the cached value. If not, compute/cache/return.
        Color result = OPAQUE_MAP.get(c);
        if (result == null) {
            result = new Color(c.getRed(), c.getGreen(), c.getBlue(), 160);
            OPAQUE_MAP.put(c, result);
        }

        return result;        
    }
    
}
