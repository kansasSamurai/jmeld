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
package org.jmeld.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollBar;

import org.jmeld.diff.JMChunk;
import org.jmeld.diff.JMDelta;
import org.jmeld.diff.JMRevision;
import org.jmeld.ui.util.ColorUtil;
import org.jmeld.ui.util.Colors;
import org.jmeld.ui.util.RevisionUtil;

/**
 * This custom JComponent is the visual "diff summary" displayed on the side of each BufferDiffPanel.
 * 
 * @author jmeld-legacy
 * @author Rick Wellman
 *
 */
@SuppressWarnings("serial")
public class RevisionBar extends JComponent {
    
    private FilePanel filePanel;

    private BufferDiffPanel diffPanel;
  
    /** If true, this represents the "original/left" document. Otherwise, it represents the "revised/right" document. */
    private boolean original;

    public RevisionBar(BufferDiffPanel diffPanel, FilePanel filePanel, boolean original) {

        this.diffPanel = diffPanel;
        this.filePanel = filePanel;
        this.original = original;

        setBorder(BorderFactory.createLineBorder(
            ColorUtil.darker(
            ColorUtil.darker(
                    Colors.getPanelBackground()))   
                ));

        addMouseListener(getMouseListener());
    }

    private MouseListener getMouseListener() {
        return new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {

                final Rectangle r = getDrawableRectangle();
                if (r == null) {
                    return;
                } else if (r.height <= 0) {
                    return;
                }

                final JMRevision revision = diffPanel.getCurrentRevision();
                if (revision == null) {
                    return;
                }

                final int y = me.getY() - r.y;
                final int numberOfLines = getNumberOfLines(revision);

                int line = (y * numberOfLines) / r.height;
                if (line > numberOfLines) {
                    line = numberOfLines;
                }
                if (line < 0) {
                    line = 0;
                }

                // If the files are very large the resolution of one pixel contains a lot of
                // lines of the document.
                // Check if there is a chunk in the revision between those lines and if there is
                // position on that chunk.
                final int lineBefore = ((y - 3) * numberOfLines) / r.height;
                final int lineAfter = ((y + 3) * numberOfLines) / r.height;
                for (JMDelta delta : revision.getDeltas()) {
                    final JMChunk original = delta.getOriginal();

                    // The chunk starts within the bounds of the line-resolution.
                    if (original.getAnchor() > lineBefore && original.getAnchor() < lineAfter) {
                        diffPanel.doGotoDelta(delta);
                        return;
                    }
                }

                diffPanel.doGotoLine(line);
            } // end mouseclicked
        };
    } // end mouselistener

    /**
     * Calculate the rectangle that can be used to draw the diffs. It is essentially
     * the size of the scrollbar minus its buttons.
     */
    private Rectangle getDrawableRectangle() {

        final JScrollBar sb = filePanel.getScrollPane().getVerticalScrollBar();
        final Rectangle r = sb.getBounds();
        r.x = 0;
        r.y = 0;

        for (Component c : sb.getComponents()) {
            if (c instanceof AbstractButton) {
                r.y += c.getHeight();
                r.height -= (2 * c.getHeight());
                break;
            }
        }

        return r;
    }

    public void paintComponent(Graphics g) {
        final Graphics2D g2 = (Graphics2D) g;
        
        final Rectangle clipBounds = g.getClipBounds();
        final Rectangle r = getDrawableRectangle();
        r.x = clipBounds.x;
        r.width = clipBounds.width;

        // Paint the background (in white)
        g2.setColor(Color.white);
        g2.fill(r);

        // If there are no revisions, no need to paint anything else
        final JMRevision revision = diffPanel.getCurrentRevision();
        if (revision == null) {
            return;
        }

        // If there are "no lines", no need to paint anything else
        final int numberOfLines = getNumberOfLines(revision);
        if (numberOfLines <= 0) {
            return;
        }

        // Finally, paint each delta
        for (JMDelta delta : revision.getDeltas()) {

            final JMChunk chunk = original ? delta.getOriginal() : delta.getRevised();

            // Calculate geometry
            final int y = r.y + (r.height * chunk.getAnchor()) / numberOfLines;
            final int heightCalc = (r.height * chunk.getSize()) / numberOfLines;
            final int height = heightCalc <= 0 ? 1 : heightCalc;
            
            // Set the color corresponding to the delta type then paint the delta
            g2.setColor(RevisionUtil.getOpaqueColor(delta));
            g2.fillRect(0, y, r.width, height);
            
        }
        
        g2.dispose();
    }

    private int getNumberOfLines(JMRevision revision) {
        return original ? revision.getOrgSize() : revision.getRevSize();
    }

}
