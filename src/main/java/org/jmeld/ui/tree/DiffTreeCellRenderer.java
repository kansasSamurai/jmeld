/*
   JMeld is a visual diff and merge tool.
   
   Copyright (C) 2018  Rick Wellman - GNU LGPL   
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
package org.jmeld.ui.tree;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.jmeld.diff.JMChunk;
import org.jmeld.diff.JMDelta;
import org.jmeld.diff.JMRevision;
import org.jmeld.ui.util.RevisionUtil;

/**
 * Created by alberto on 16/11/14.
 * 
 * @author alberto
 * 
 */
@SuppressWarnings("serial")
class DiffTreeCellRenderer extends DefaultTreeCellRenderer {

    @SuppressWarnings("unused")
    private static final Color COLOR_DELTA  = new Color(209, 70, 237); // TODO not sure what this was originally intended for... marked for deletion
    private static final Color COLOR_CHUNK  = new Color(217, 154, 13);
    private static final Color COLOR_CHANGE = new Color(237, 38, 139);
    private static final Color COLOR_REVISION = new Color(39, 86, 189);
    
    private static final Icon ICON_REVISION = new TreeColorIcon(COLOR_REVISION);
    private static final Icon ICON_CHANGE = new TreeColorIcon(COLOR_CHANGE);
    private static final Icon ICON_CHUNK = new TreeColorIcon(COLOR_CHUNK);

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        final Object userObject = node.getUserObject();
        if (userObject instanceof JMRevision) {
            final JMRevision revision = (JMRevision) userObject;
            final int numChanges = revision.getDeltas().size();
            
            setIcon(ICON_REVISION);
            setText(String.format("%d changes, %s", numChanges, revision.getIgnore()));
            
        } else if (userObject instanceof JMChange) {            
            final JMChange change = (JMChange) userObject;
            
            setIcon(ICON_CHANGE);
            setText(change.toString());
            
        } else if (userObject instanceof JMDelta) {
            final JMDelta delta = (JMDelta) userObject;
            
            setIcon(new TreeColorIcon(RevisionUtil.getColor(delta)));
            setText(delta.getType().toString());
            
        } else if (value instanceof JMChunkNode) {
            final JMChunkNode chunkNode = (JMChunkNode) value;
            final JMChunk chunk = chunkNode.getChunk();
            
            setIcon(ICON_CHUNK);
            setText(String.format("%d, %d: \"%s\"", chunk.getAnchor(), chunk.getSize(), chunkNode.getString()));
        
        }
        
        return this;
    }
    
}
