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
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComponent;

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
public class RevisionBar extends JComponent implements HierarchyListener {
    
    private FilePanel filePanel;

    private BufferDiffPanel diffPanel;
  
    /** If true, this represents the "original/left" document. Otherwise, it represents the "revised/right" document. */
    private boolean original;

    public RevisionBar(BufferDiffPanel diffPanel, FilePanel filePanel, boolean original) {

        this.diffPanel = diffPanel;
        this.filePanel = filePanel;
        this.original = original;

        this.setBorder(BorderFactory.createLineBorder( // TODO ... this border does not seem to be getting painted?
            ColorUtil.darker(
            ColorUtil.darker( Colors.getPanelBackground())) ));

        this.addMouseListener(getMouseListener());
        
        this.filePanel.getScrollPane().getVerticalScrollBar().addHierarchyListener(this);
    }

    /**
     * Respond to mouse clicks
     * 
     * @return
     */
    private MouseListener getMouseListener() {
        return new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                System.out.print(".me");

                final JMRevision revision = diffPanel.getCurrentRevision();
                if (revision == null) {
                    return;
                }

                final Rectangle r = getDrawableRectangle();
                if (r == null || r.height <= 0) {
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

                // If the files are very large the resolution of one pixel 
                // contains a lot of lines of the document.
                // Check if there is a chunk in the revision between those lines 
                // and if there is position on that chunk.
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
                System.out.println("click to line: " + line);
            } // end mouseclicked
        };
    } // end mouselistener

    /**
     * Calculate the rectangle that can be used to draw the diffs. 
     * It is essentially the size of the scrollbar minus its buttons.
     */
    private Rectangle getDrawableRectangle() {

        JComponent sb = filePanel.getScrollPane().getVerticalScrollBar();
        boolean useScrollBar = sb.isVisible();
        if (!useScrollBar) sb = filePanel.getEditor();
        
        final Rectangle r = sb.getBounds();
        r.x = 0;
        r.y = 0;

        if (useScrollBar) {
            for (Component c : sb.getComponents()) {
                if (c instanceof AbstractButton) {
                    r.y += c.getHeight();
                    r.height -= (2 * c.getHeight());
                    break;
                }
            }            
        } else {
            r.height = sb.getHeight();            
        }

        return r;
    }

    public void paintComponent(Graphics g) {
        final Graphics2D g2 = (Graphics2D) g;
        
        final Rectangle clipBounds = g2.getClipBounds();
        
        final Rectangle r = getDrawableRectangle();
        r.x = clipBounds.x;
        r.width = clipBounds.width;

        // Paint the background (in white)
        g2.setColor(Color.white);
        g2.fill(r);
        
        final JMRevision revision = diffPanel.getCurrentRevision();
        if (revision == null) 
        {} else {
            final int numberOfLines = getNumberOfLines(revision);
            if ( numberOfLines > 0 ) {
                
                // Paint each delta
                // System.out.println("painting revisions: " + revision.getDeltas().size());
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
            }
        }

        // Finally, Paint a "border"
        g2.setColor(Color.BLACK);
        g2.drawRect(r.x, r.y, r.width-1, r.height-1);

        g2.dispose();
        return;
    }

    private int getNumberOfLines(JMRevision revision) {
        return original ? revision.getOrgSize() : revision.getRevSize();
    }

    @Override
    public void hierarchyChanged(HierarchyEvent e) {
        //We don't really need the scrollbar, we just need to know that this event occurred on it
        //final JScrollBar sb = (JScrollBar) e.getSource();
        this.revalidate();
        this.repaint();
    }

}
