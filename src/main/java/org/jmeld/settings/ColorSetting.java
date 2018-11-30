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
package org.jmeld.settings;

import java.awt.Color;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.jmeld.util.conf.AbstractConfigurationElement;

/**
 * 
 * @author jmeld-legacy
 * @author Rick Wellman
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public class ColorSetting extends AbstractConfigurationElement {
    
    @XmlAttribute
    private int a = -1;

    @XmlAttribute
    private int b = -1;

    @XmlAttribute
    private int g = -1;

    @XmlAttribute
    private int r = -1;

    private Color color;

    public ColorSetting() {
    }

    public ColorSetting(Color color) {
        this.r = color.getRed();
        this.g = color.getGreen();
        this.b = color.getBlue();
        this.a = color.getAlpha();

        this.color = color;
    }

    public Color getColor() {
        if (r == -1 || g == -1 || b == -1 || a == -1) {
            return null;
        }

        if (color == null) {
            color = new Color(r, g, b, a);
        }

        return color;
    }

}
