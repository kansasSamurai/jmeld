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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jmeld.JMeldException;
import org.jmeld.ui.text.BufferDocumentIF;
import org.jmeld.util.ObjectUtil;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * 
 * @author jmeld-legacy
 * @author Rick Wellman
 *
 */
public class SavePanelDialog {

    // Instance variables:
    private boolean ok;
    private JMeldPanel meldPanel;
    private JCheckBox[] checkBoxes;
    private List<BufferDocumentIF> documents;

    public SavePanelDialog(JMeldPanel meldPanel) {
        this.meldPanel = meldPanel;

        documents = new ArrayList<BufferDocumentIF>();
    }

    public void add(BufferDocumentIF document) {
        documents.add(document);
    }

    public void show() {
        JOptionPane pane;
        JDialog dialog;

        pane = new JOptionPane(getSavePanel(), JOptionPane.WARNING_MESSAGE);
        pane.setOptionType(JOptionPane.OK_CANCEL_OPTION);

        dialog = pane.createDialog(meldPanel, "Save files");
        dialog.setResizable(true);
        try {
            dialog.setVisible(true);

            if (ObjectUtil.equals(pane.getValue(), JOptionPane.OK_OPTION)) {
                ok = true;
            }
        } finally {
            // Don't allow memoryleaks!
            dialog.dispose();
        }
    }

    public boolean isOK() {
        return ok;
    }

    public void doSave() {
        BufferDocumentIF document;

        if (checkBoxes == null) {
            return;
        }

        for (int i = 0; i < checkBoxes.length; i++) {
            if (!checkBoxes[i].isSelected()) {
                continue;
            }

            document = documents.get(i);
            if (document == null) {
                continue;
            }

            try {
                document.write();
            } catch (JMeldException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(meldPanel, "Can't write file" + document.getName(),
                        "Problem writing file", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JComponent getSavePanel() {
        JPanel panel;
        String columns;
        String rows;
        FormLayout layout;
        CellConstraints cc;
        JLabel label;
        JCheckBox checkBox;
        BufferDocumentIF document;
        Font font;

        columns = "10px, fill:pref, 10px";
        rows = "10px, fill:pref, 5px, fill:pref, 10px,";
        for (int i = 0; i < documents.size(); i++) {
            rows += " fill:pref, ";
        }

        rows += " 10px";

        layout = new FormLayout(columns, rows);
        cc = new CellConstraints();

        panel = new JPanel(layout);
        label = new JLabel("Some files have been changed");
        font = label.getFont().deriveFont(Font.BOLD);
        label.setFont(font);
        label.setHorizontalAlignment(JLabel.LEFT);
        panel.add(label, cc.xy(2, 2));
        label = new JLabel("Which ones would you like to save?");
        label.setFont(font);
        label.setHorizontalAlignment(JLabel.LEFT);
        panel.add(label, cc.xy(2, 4));

        checkBoxes = new JCheckBox[documents.size()];
        for (int i = 0; i < documents.size(); i++) {
            document = documents.get(i);
            if (document == null) {
                continue;
            }

            checkBox = new JCheckBox(document.getName());
            checkBoxes[i] = checkBox;
            if (document.isChanged()) {
                checkBox.setSelected(false);
            } else {
                checkBox.setEnabled(false);
            }

            panel.add(checkBox, cc.xy(2, 6 + i));
        }

        return panel;
    }
}
