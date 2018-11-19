/*
   JMeld is a visual diff and merge tool.
   Copyright (C) 2007  Kees Kuip
   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2.1 of the License, or (at your option) any later version.
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.
   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 51 Franklin Street, Fifth Floor,
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
 * 
 * @author jmeld-legacy
 *
 */
public class RevisionUtil {
    
    // A "cache" for the darker colors to prevent recompute time
    private static Map<Color, Color> DARKER_MAP = new HashMap<Color, Color>();

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

    static private EditorSettings getSettings() {
        return JMeldSettings.getInstance().getEditor();
    }
    
}
