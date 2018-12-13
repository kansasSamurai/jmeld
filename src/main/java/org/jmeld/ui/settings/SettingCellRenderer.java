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

package org.jmeld.ui.settings;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;

import jiconfont.icons.FontAwesome;
import jiconfont.swing.IconFontSwing;

/**
 * ListCellRenderer for JList of Settings objects.
 * 
 * @author jmeld-legacy
 * @author Rick Wellman
 *
 */
@SuppressWarnings("serial")
class SettingCellRenderer extends DefaultListCellRenderer {
	
	private final Icon[] iconset = {
         IconFontSwing.buildIcon(FontAwesome.FILE_TEXT, 24, Color.black)
        ,IconFontSwing.buildIcon(FontAwesome.FILTER, 24, Color.black)
        ,IconFontSwing.buildIcon(FontAwesome.FOLDER_OPEN, 24, Color.black)
	};
	
	public SettingCellRenderer() {
		setOpaque(true);
		setBackground(Color.white);
		setForeground(Color.black);
		setHorizontalAlignment(JLabel.CENTER);
		setVerticalAlignment(JLabel.CENTER);
		setVerticalTextPosition(JLabel.BOTTOM);
		setHorizontalTextPosition(JLabel.CENTER);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setPreferredSize(new Dimension(70, 70));
	}

	@Override
	public Component getListCellRendererComponent( JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

		Settings settings = (Settings) value;
		setText(settings.getName());
		setIcon(iconset[settings.ordinal()]); // (ImageUtil.getImageIcon(value.getIconName()));
		setEnabled(list.isEnabled());
		setFont(list.getFont());

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(Color.white);
			setForeground(Color.black);
		}

		return this;
	}

}
