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
