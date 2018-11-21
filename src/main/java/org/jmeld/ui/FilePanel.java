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

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.PlainDocument;

import org.jmeld.diff.JMChunk;
import org.jmeld.diff.JMDelta;
import org.jmeld.diff.JMRevision;
import org.jmeld.settings.JMeldSettings;
import org.jmeld.ui.search.SearchCommand;
import org.jmeld.ui.search.SearchHit;
import org.jmeld.ui.search.SearchHits;
import org.jmeld.ui.swing.DiffLabel;
import org.jmeld.ui.swing.JMHighlightPainter;
import org.jmeld.ui.swing.JMHighlighter;
import org.jmeld.ui.swing.LeftScrollPaneLayout;
import org.jmeld.ui.swing.LineNumberBorder;
import org.jmeld.ui.text.BufferDocumentChangeListenerIF;
import org.jmeld.ui.text.BufferDocumentIF;
import org.jmeld.ui.text.JMDocumentEvent;
import org.jmeld.ui.util.FontUtil;
import org.jmeld.ui.util.ImageUtil;
import org.jmeld.util.StringUtil;
import org.jmeld.util.conf.ConfigurationListenerIF;

/**
 * Though this object is not a Component/JComponent, it does effectively serve as the container
 * for a single file's Diff JPanel.
 * 
 * @author jmeld-legacy
 * @author Rick Wellman
 * 
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class FilePanel implements BufferDocumentChangeListenerIF, ConfigurationListenerIF {
    
    private static final int MAXSIZE_CHANGE_DIFF = 1000;

    private int position;
    private Timer timer;
    private String name;
    private boolean selected;
    
    private JTextArea editor;
    private DiffLabel fileLabel;
    private BufferDiffPanel diffPanel;
    private BufferDocumentIF bufferDocument;
    private JComboBox fileBox;
    private JButton saveButton;
    private SearchHits searchHits;
    private FilePanelBar filePanelBar;
    private JScrollPane scrollPane;

    // Flyweight(s)
    private final HighlightOriginal highlightOriginal = new HighlightOriginal(null);
    private final HighlightRevised highlightRevised = new HighlightRevised(null);
    
    public FilePanel(BufferDiffPanel diffPanel, String name, int position) {
        this.diffPanel = diffPanel;
        this.name = name;
        this.position = position;

        init();
    }

    private void init() {

        editor = new JTextArea(); // TODO replace with RSyntaxTextArea
        editor.setDragEnabled(true);
        editor.setHighlighter(new JMHighlighter());

        editor.addFocusListener(getFocusListener());
        editor.addCaretListener(getCaretListener());
        
        final DefaultContextMenu contextMenu = new DefaultContextMenu();
        contextMenu.add(editor);

        scrollPane = new JScrollPane(editor);
        scrollPane.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE); // (JViewport.SIMPLE_SCROLL_MODE);
        if (BufferDocumentIF.ORIGINAL.equals(name)) {

            // Dirty trick to have the scrollbar on the other side!
            LeftScrollPaneLayout layout = new LeftScrollPaneLayout();
            scrollPane.setLayout(layout);
            layout.syncWithScrollPane(scrollPane);

            // Normally the leftside is not painted as a scrollbar that is NOT freestanding.
            scrollPane.getVerticalScrollBar().putClientProperty("JScrollBar.isFreeStanding", Boolean.TRUE);
            
        }

        fileBox = new JComboBox();
        fileBox.addActionListener(getFileBoxAction());

        fileLabel = new DiffLabel();

        saveButton = new JButton();
        saveButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        saveButton.setContentAreaFilled(false);

        final ImageIcon icon = ImageUtil.getSmallImageIcon("stock_save");
        saveButton.setIcon(icon);
        saveButton.setDisabledIcon(ImageUtil.createTransparentIcon(icon));
        saveButton.addActionListener(getSaveButtonAction());

        timer = new Timer(100, refresh());
        timer.setRepeats(false);

        initConfiguration();
        getConfiguration().addConfigurationListener(this);
    }

    FilePanelBar getFilePanelBar() {
        if (filePanelBar == null) {
            filePanelBar = new FilePanelBar(this);
        }

        return filePanelBar;
    }

    JComboBox getFileBox() {
        return fileBox;
    }

    DiffLabel getFileLabel() {
        return fileLabel;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public JTextArea getEditor() {
        return editor;
    }

    public BufferDocumentIF getBufferDocument() {
        return bufferDocument;
    }

    JButton getSaveButton() {
        return saveButton;
    }

    public void setBufferDocument(BufferDocumentIF bd) {
        Document previousDocument;
        Document document;
        String fileName;

        try {
            if (bufferDocument != null) {
                bufferDocument.removeChangeListener(this);

                previousDocument = bufferDocument.getDocument();
                if (previousDocument != null) {
                    previousDocument.removeUndoableEditListener(diffPanel
                            .getUndoHandler());
                }
            }

            bufferDocument = bd;

            document = bufferDocument.getDocument();
            if (document != null) {
                editor.setDocument(document);
                editor.setTabSize(getConfiguration().getEditor().getTabSize());
                bufferDocument.addChangeListener(this);
                document.addUndoableEditListener(diffPanel.getUndoHandler());
            }

            fileName = bufferDocument.getName();
            fileBox.addItem(fileName);
            fileBox.setSelectedItem(fileName);
            fileLabel.setText(fileName);

            checkActions();
            initConfiguration();
        } catch (Exception ex) {
            ex.printStackTrace();

            JOptionPane.showMessageDialog(diffPanel, "Could not read file: "
                    + bufferDocument.getName()
                    + "\n" + ex.getMessage(),
                    "Error opening file", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    void updateFileLabel(String name1, String name2) {
        fileLabel.setText(name1, name2);
    }

    void doStopSearch() {
        searchHits = null;
        reDisplay();
    }

    private void checkSearch() {
        doSearch();
    }

    SearchHits doSearch() {
        int numberOfLines;
        BufferDocumentIF doc;
        String text;
        int index, fromIndex;
        boolean regularExpression;
        String searchText;
        SearchHit searchHit;
        int offset;
        // int length;
        SearchCommand searchCommand;

        searchCommand = diffPanel.getSearchCommand();
        if (searchCommand == null) {
            return null;
        }

        searchText = searchCommand.getSearchText();
        regularExpression = searchCommand.isRegularExpression();

        doc = getBufferDocument();
        numberOfLines = doc.getNumberOfLines();

        searchHits = new SearchHits();

        if (!StringUtil.isEmpty(searchText)) {
            for (int line = 0; line < numberOfLines; line++) {
                text = doc.getLineText(line);
                if (!regularExpression) {
                    fromIndex = 0;
                    while ((index = text.indexOf(searchText, fromIndex)) != -1) {
                        offset = bufferDocument.getOffsetForLine(line);
                        if (offset < 0) {
                            continue;
                        }

                        searchHit = new SearchHit(line, offset + index, searchText.length());
                        searchHits.add(searchHit);

                        fromIndex = index + searchHit.getSize() + 1;
                    }
                }
            }
        }

        reDisplay();

        return getSearchHits();
    }

    void setShowLineNumbers(boolean showLineNumbers) {
        
        String propertyName = "JMeld.originalBorder";
        Border originalBorder = (Border) editor.getClientProperty(propertyName);

        if (showLineNumbers) {
            if (originalBorder == null) {
                originalBorder = editor.getBorder();
                editor.setBorder(new LineNumberBorder(this));
                editor.putClientProperty(propertyName, originalBorder);
            }
        } else {
            if (originalBorder != null) {
                editor.setBorder(originalBorder);
                editor.putClientProperty(propertyName, null);
            }
        }
    }

    public SearchHits getSearchHits() {
        return searchHits;
    }

    public void reDisplay() {
        getHighlighter().setDoNotRepaint(true);    
            removeHighlights();
            paintSearchHighlights();
            paintRevisionHighlights();
        getHighlighter().setDoNotRepaint(false);
        
        getHighlighter().repaint();
    }

    private void paintSearchHighlights() {
        if (searchHits != null) {
            for (SearchHit sh : searchHits.getSearchHits()) {
                setHighlight(JMHighlighter.LAYER2, sh.getFromOffset(),
                        sh.getToOffset(),
                        searchHits.isCurrent(sh)
                                ? JMHighlightPainter.CURRENT_SEARCH: JMHighlightPainter.SEARCH);
            }
        }
    }

    private void paintRevisionHighlights() {

        if (bufferDocument == null) {
            return;
        }

        JMRevision revision = diffPanel.getCurrentRevision();
        if (revision == null) {
            return;
        }

        for (JMDelta delta : revision.getDeltas()) {
            if (BufferDocumentIF.ORIGINAL.equals(name)) {
                highlightOriginal.setDelta(delta).highlight(); // converted to flyweight pattern
            } else if (BufferDocumentIF.REVISED.equals(name)) {
                highlightRevised.setDelta(delta).highlight(); // converted to flyweight pattern
            }
        }
        
    }

    /**
     * 
     * @author Rick Wellman
     *
     */
    abstract class AbstractHighlight {
        
        protected JMDelta delta;

        /**
         * Create a new instance; the 'delta' parameter can now be null
         * since we now treat this as a flyweight object by creating the
         * setDelta() method.
         * 
         * @param delta
         */
        public AbstractHighlight(JMDelta delta) {
            this.delta = delta;
        }
        
        public AbstractHighlight setDelta(JMDelta d) {
            this.delta = d;
            return this;
        }

        protected void highlight() {

            final int fromOffset = bufferDocument.getOffsetForLine(getPrimaryChunk().getAnchor());
            if (fromOffset < 0) {
                return;
            }

            int toOffset = bufferDocument.getOffsetForLine(getPrimaryChunk().getAnchor() + getPrimaryChunk().getSize());
            if (toOffset < 0) {
                return;
            }

            boolean isEndAndIsLastNewLine = isEndAndIsLastNewLine(toOffset);

            JMHighlightPainter highlight = null;
            if (delta.isChange()) {
                if (    delta.getOriginal().getSize() < MAXSIZE_CHANGE_DIFF
                     && delta.getRevised().getSize() < MAXSIZE_CHANGE_DIFF) {
                    
                    final JMRevision changeRev = delta.getChangeRevision();
                    if (changeRev != null) {
                        for (JMDelta changeDelta : changeRev.getDeltas()) {
                            final JMChunk changeOriginal = getPrimaryChunk(changeDelta);
                            if (changeOriginal.getSize() <= 0) {
                                continue;
                            }

                            final int fromOffset2 = fromOffset + changeOriginal.getAnchor();
                            final int toOffset2 = fromOffset2 + changeOriginal.getSize();

                            setHighlight(JMHighlighter.LAYER1, fromOffset2, toOffset2, JMHighlightPainter.CHANGED_LIGHTER);
                        }
                    }
                }

                highlight = isEndAndIsLastNewLine ? JMHighlightPainter.CHANGED_NEWLINE : JMHighlightPainter.CHANGED;
            } else {
                if (isEmptyLine()) {
                    toOffset = fromOffset + 1;
                }
                if (delta.isAdd()) {
                    highlight = getAddedHighlightPainter(isOriginal(), isEndAndIsLastNewLine);
                } else if (delta.isDelete()) {
                    highlight = getDeleteHighlightPainter(!isOriginal(), isEndAndIsLastNewLine);
                }
            }
            setHighlight(fromOffset, toOffset, highlight);
        }

        /**
         * If last change reach the end and has a newline as final char, final line is
         * virtual. Document has no line for it, since newline stars that line but it
         * contains nothing yet.
         *
         * @param toOffset last offset of change
         *
         * @see org.jmeld.ui.swing.JMHighlightPainter#ADDED_NEWLINE
         * @see org.jmeld.ui.swing.JMHighlightPainter#CHANGED_NEWLINE
         * @see org.jmeld.ui.swing.JMHighlightPainter#DELETED_NEWLINE
         * @return
         */
        private boolean isEndAndIsLastNewLine(int toOffset) {
            boolean isEndAndIsLastNewLine = false;
            try {
                PlainDocument document = bufferDocument.getDocument();
                int endOffset = toOffset - 1;
                boolean changeReachEnd = endOffset == document.getLength();
                boolean lastCharIsNewLine = "\n".equals(document.getText(endOffset, 1));
                isEndAndIsLastNewLine = changeReachEnd && lastCharIsNewLine;
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
            return isEndAndIsLastNewLine;
        }

        private JMChunk getPrimaryChunk() {
            return getPrimaryChunk(delta);
        }

        private boolean isOriginal() {
            return delta.getOriginal() == getPrimaryChunk();
        }

        private JMHighlightPainter getAddedHighlightPainter(boolean line, boolean isLastNewLine) {
            return line
                    ? JMHighlightPainter.ADDED_LINE
                    : isLastNewLine
                        ? JMHighlightPainter.ADDED_NEWLINE
                        : JMHighlightPainter.ADDED;
        }

        private JMHighlightPainter getDeleteHighlightPainter(boolean line, boolean isLastNewLine) {
            return line
                    ? JMHighlightPainter.DELETED_LINE
                    : isLastNewLine
                        ? JMHighlightPainter.DELETED_NEWLINE
                        : JMHighlightPainter.DELETED;
        }

        protected abstract JMChunk getPrimaryChunk(JMDelta changeDelta);

        public abstract boolean isEmptyLine();
    }

    /**
     * Converted to flyweight
     * 
     * @author Rick Wellman
     *
     */
    class HighlightOriginal extends AbstractHighlight {

        public HighlightOriginal(JMDelta delta) {
            super(delta);
        }

        public boolean isEmptyLine() {
            return delta.isAdd();
        }

        protected JMChunk getPrimaryChunk(JMDelta changeDelta) {
            return changeDelta.getOriginal();
        }
    }

    /**
     * Converted to flyweight
     * 
     * @author Rick Wellman
     *
     */
    class HighlightRevised extends AbstractHighlight {

        public HighlightRevised(JMDelta delta) {
            super(delta);
        }

        public boolean isEmptyLine() {
            return delta.isDelete();
        }

        protected JMChunk getPrimaryChunk(JMDelta changeDelta) {
            return changeDelta.getRevised();
        }
    }

    private JMHighlighter getHighlighter() {
        return (JMHighlighter) editor.getHighlighter();
    }

    private void removeHighlights() {
        JMHighlighter jmhl;

        jmhl = getHighlighter();
        jmhl.removeHighlights(JMHighlighter.LAYER0);
        jmhl.removeHighlights(JMHighlighter.LAYER1);
        jmhl.removeHighlights(JMHighlighter.LAYER2);
    }

    private void setHighlight(int offset, int size,
                              Highlighter.HighlightPainter highlight) {
        setHighlight(JMHighlighter.LAYER0, offset, size, highlight);
    }

    private void setHighlight(Integer layer, int offset, int size,
                              Highlighter.HighlightPainter highlight) {
        try {
            getHighlighter().addHighlight(layer, offset, size, highlight);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }

    public ActionListener getSaveButtonAction() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    bufferDocument.write();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(SwingUtilities.getRoot(editor),
                            "Could not save file: " + bufferDocument.getName() + "\n"
                                    + ex.getMessage(), "Error saving file",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
    }

    public ActionListener getFileBoxAction() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                //System.out.println("fileBox: " + fileBox.getSelectedItem());
            }
        };
    }

    public void documentChanged(JMDocumentEvent de) {
        if (de.getStartLine() == -1 && de.getDocumentEvent() == null) {
            // Refresh the diff of whole document.
            timer.restart();
        } else {
            // Try to update the revision instead of doing a full diff.
            if (!diffPanel.revisionChanged(de)) {
                timer.restart();
            }
        }

        checkSearch();
        checkActions();
    }

    private void checkActions() {
        if (saveButton.isEnabled() != isDocumentChanged()) {
            saveButton.setEnabled(isDocumentChanged());
        }

        diffPanel.checkActions();
    }

    boolean isDocumentChanged() {
        return bufferDocument != null ? bufferDocument.isChanged() : false;
    }

    private ActionListener refresh() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                diffPanel.diff();
            }
        };
    }

    public FocusListener getFocusListener() {
        return new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent fe) {
                diffPanel.setSelectedPanel(FilePanel.this);
            }
        };
    }

    public CaretListener getCaretListener() {
        return new CaretListener() {
            public void caretUpdate(CaretEvent fe) {
                updateFilePanelBar();
            }
        };
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        updateFilePanelBar();
        checkSearch();
    }

    private void updateFilePanelBar() {
        if (filePanelBar != null) {
            filePanelBar.update();
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void configurationChanged() {
        initConfiguration();
    }

    private void initConfiguration() {

        final JMeldSettings settings = getConfiguration();

        setShowLineNumbers(settings.getEditor().getShowLineNumbers());

        Font font = settings.getEditor().isCustomFontEnabled() ? settings.getEditor().getFont() : null;
        font = font != null ? font : FontUtil.defaultTextAreaFont;
        editor.setFont(font);

        FontMetrics fm = editor.getFontMetrics(font);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(fm.getHeight());

        getEditor().setTabSize(settings.getEditor().getTabSize());

        boolean readonly = false;
        if (position == BufferDiffPanel.LEFT) {
            readonly = settings.getEditor().getLeftsideReadonly();
        } else if (position == BufferDiffPanel.RIGHT) {
            readonly = settings.getEditor().getRightsideReadonly();
        }

        if (bufferDocument != null && bufferDocument.isReadonly()) {
            readonly = true;
        }

        editor.setEditable(!readonly);
    }

    private JMeldSettings getConfiguration() {
        return JMeldSettings.getInstance();
    }

    public String getSelectedText() {
        return editor.getSelectedText();
    }
    
}
