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
package org.jmeld.ui.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.jmeld.settings.JMeldSettings;
import org.jmeld.ui.FilePanel;
import org.jmeld.ui.util.ColorUtil;
import org.jmeld.ui.util.Colors;

/**
 * Line Number Component that decorates a FilePanel.
 * 
 * TODO Investigate better looking/performing alternatives
 * 
 * @author jmeld-legacy
 * @author Rick Wellman
 *
 */
@SuppressWarnings("serial")
public class LineNumberBorder extends EmptyBorder {
    
    private FilePanel filePanel;
    private Color background;
    private Color lineColor;
    private Color textColor = new Color(0x3C3C3C);
    private Font font;
    private int fontWidth;
    private int fontHeight;
    protected boolean enableBlame = true;

    private static int MARGIN = 4;
    
    public LineNumberBorder(FilePanel filePanel) {
        super(0, 40 + MARGIN, 0, 0);

        this.filePanel = filePanel;

        init();
    }

    public void enableBlame(boolean enableBlame) {
        this.enableBlame = enableBlame;
    }

    private void init() {

        // TODO This approach bases colors on current L&F; I disagree with this approach. 
        // The document pane(s) should default to a fixed styling; if the user wants his
        // own styling, it can be customized in settings.
        boolean oldschool = false;
        if (oldschool) {
            final Color baseColor = Colors.getPanelBackground();
            lineColor = ColorUtil.darker(baseColor);
            background = ColorUtil.brighter(baseColor);            
        } else {
            lineColor = Color.black; // ColorUtil.darker(baseColor);
            background = new Color(0xCDCDCD); // ColorUtil.brighter(baseColor);            
        }
        
        // TODO commit this; uses same font as document settings; worst case default to system "monospaced" at 10pt
        // font = new Font("Monospaced", Font.PLAIN, 10);
        final JMeldSettings settings = JMeldSettings.getInstance();
        font = settings.getEditor().isCustomFontEnabled() ? settings.getEditor().getFont() : null;
        if (font != null) {
            // I originally tried the following but didn't like the way it looked, even when aligned on the baseline
            // Reverting back to using the same size as the text area(s) - which is what Eclipse does if that helps
            // font = font.deriveFont(.8f * font.getSize());
        } else {
            // worst case default to system "monospaced" at 10pt
            font = new Font("Monospaced", Font.PLAIN, 10);            
        }

        final FontMetrics fm = filePanel.getEditor().getFontMetrics(font);
        fontWidth = fm.stringWidth("0");
        fontHeight = fm.getHeight();
    }

    // TODO this should be part of an interface to show its relationship to JMHighlighter
    public void paintBefore(Graphics g) {
        Rectangle clip = g.getClipBounds();
        g.setColor(background);
        g.fillRect(0, clip.y, left - MARGIN, clip.y + clip.height);
    }

    // TODO this should be part of an interface to show its relationship to JMHighlighter
    public void paintAfter(Graphics g, int startOffset, int endOffset) {
        final Graphics2D g2 = (Graphics2D) g.create();
        final Rectangle clip = g2.getClipBounds();
        final JTextArea textArea = filePanel.getEditor();

        try {
            final int startLine = textArea.getLineOfOffset(startOffset);
            final int endLine = textArea.getLineOfOffset(endOffset);
            
            final Rectangle r1 = textArea.modelToView(startOffset);
            final int lineHeight = r1.height;
            int heightCorrection = (lineHeight - fontHeight) / 2; // TODO this can be final once the baseline is figured out
            heightCorrection += 5; // TODO this is new and hacked; figure out a better solution for correcting the baseline
            // ^^^ more testing necessary using alternate font/sizes (though it may be best not to give the user the choice)
            // If someone absolutely wants that... they can code it themselves and contribute to the cause.

            g2.setColor(lineColor);
            g2.drawLine(left - MARGIN, clip.y, left - MARGIN, clip.y + clip.height);

            // Draw Text
            g2.setFont(font);
            g2.setColor(textColor); // (Color.black);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int y = r1.y;
            for (int line = startLine; line <= endLine; line++) {
                y += lineHeight;
                
                final String nn = Integer.toString(line + 1);
                g2.drawString(nn, left - (fontWidth * nn.length()) - 1 - MARGIN, y - heightCorrection);
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            g2.dispose();
        }
        
    }
    
}
