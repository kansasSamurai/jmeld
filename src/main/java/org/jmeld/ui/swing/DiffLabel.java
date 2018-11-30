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

package org.jmeld.ui.swing;

import java.awt.Color;
import java.util.List;

import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.jmeld.diff.JMChunk;
import org.jmeld.diff.JMDelta;
import org.jmeld.diff.JMDiff;
import org.jmeld.diff.JMRevision;
import org.jmeld.util.Ignore;
import org.jmeld.util.Tokenizer;
import org.jmeld.util.TokenizerFactory;

/**
 * Use a JTextPane to emulate a JLabel with the following features:
 * Wrap the text for long file names/paths.
 * Display the filename only part in bold.
 * 
 * @author jmeld-legacy
 * @author Rick Wellman
 *
 */
public class DiffLabel extends JTextPane {
    
    private static final long serialVersionUID = 1L;

    public DiffLabel() {
        init();
    }

    public void init() {

        setEditable(false);
        setOpaque(false);
        // Bug in Nimbus L&F doesn't honour the opaqueness of a JLabel.
        // Setting a fully transparent color is a workaround:
        setBackground(new Color(0, 0, 0, 0));
        setBorder(null);

        StyledDocument doc = getStyledDocument();
        Style defaultStyle = getStyle(StyleContext.DEFAULT_STYLE);
        Style s = doc.addStyle("bold", defaultStyle);
        StyleConstants.setBold(s, true);
    }

    /**
     * Set the text on this label.
     *  
     * Some parts of the text will be displayed in bold-style. 
     * These parts are the differences between text and otherText.
     * 
     * TODO this seems like a lot of code to do something this simple;
     * also... this uses the JMDiff.diff() algorithm?  maybe it's necessary
     * but I'm thinking there is a simpler altnerative
     * 
     * @param plainText
     * @param boldText
     */
    public void setText(String plainText, String boldText) {
        JMRevision revision;
        JMChunk chunk;
        StyledDocument doc;

        try {
            final Tokenizer wt = TokenizerFactory.getInstance().getFileNameTokenizer();
            final List<String> textList = wt.getTokens(plainText);
            final List<String> otherTextList = wt.getTokens(boldText);

            final String[] styles = new String[textList.size()];

            if (otherTextList.size() != 0) {
                revision = new JMDiff().diff(textList, otherTextList, Ignore.NULL_IGNORE);

                for (JMDelta delta : revision.getDeltas()) {
                    chunk = delta.getOriginal();
                    for (int i = 0; i < chunk.getSize(); i++) {
                        styles[chunk.getAnchor() + i] = "bold";
                    }
                }
            }

            doc = getStyledDocument();
            doc.remove(0, doc.getLength());

            for (int i = 0; i < textList.size(); i++) {
                doc.insertString(
                        doc.getLength(), 
                        textList.get(i),
                        (styles[i] != null ? doc.getStyle(styles[i]) : null));
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();

            // Make the best out of this situation. (Should never happen)
            setText(plainText);
        }
    }
}
