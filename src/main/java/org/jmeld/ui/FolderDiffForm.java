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

import java.awt.Component;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.jmeld.ui.swing.DiffLabel;
import org.jmeld.ui.swing.table.JMTreeTable;

/**
 * 
 * @author kees
 * @author Rick Wellman
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class FolderDiffForm extends AbstractContentPanel {

    /**
     * Creates new form FolderDiffForm
     */
    public FolderDiffForm() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    protected void initComponents() {

        folder1Label = new DiffLabel();
        folder2Label = new DiffLabel();
        jScrollPane1 = new JScrollPane();
        folderTreeTable = new JMTreeTable();
        onlyRightButton = new JToggleButton();
        leftRightChangedButton = new JToggleButton();
        onlyLeftButton = new JToggleButton();
        leftRightUnChangedButton = new JToggleButton();
        hierarchyComboBox = new JComboBox();
        expandAllButton = new JButton();
        collapseAllButton = new JButton();
        deleteLeftButton = new JButton();
        copyToLeftButton = new JButton();
        copyToRightButton = new JButton();
        deleteRightButton = new JButton();
        refreshButton = new JButton();
        compareButton = new JButton();

        folder1Label.setText("Left name of directory");

        folder2Label.setText("Right name of directory");

        jScrollPane1.setViewportView(folderTreeTable);

        onlyRightButton.setText("R");

        leftRightChangedButton.setText("LR");

        onlyLeftButton.setText("L");

        leftRightUnChangedButton.setText("Un");

        hierarchyComboBox.setModel(new DefaultComboBoxModel(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));

        expandAllButton.setText("Exp");

        collapseAllButton.setText("Col");

        deleteLeftButton.setText("DL");
        deleteLeftButton.setMargin(new java.awt.Insets(2, 2, 2, 2));

        copyToLeftButton.setText("CL");
        copyToLeftButton.setMargin(new java.awt.Insets(2, 2, 2, 2));

        copyToRightButton.setText("CR");
        copyToRightButton.setMargin(new java.awt.Insets(2, 2, 2, 2));

        deleteRightButton.setText("DR");
        deleteRightButton.setMargin(new java.awt.Insets(2, 2, 2, 2));

        refreshButton.setText("RS");
        refreshButton.setMargin(new java.awt.Insets(2, 2, 2, 2));

        compareButton.setText("CO");
        compareButton.setMargin(new java.awt.Insets(2, 2, 2, 2));

        GroupLayout layout = new GroupLayout(
                this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(
                GroupLayout.LEADING).add(
                layout.createSequentialGroup().addContainerGap().add(
                        layout.createParallelGroup(GroupLayout.LEADING)
                                .add(
                                        GroupLayout.TRAILING,
                                        layout.createSequentialGroup().add(
                                                layout.createParallelGroup(
                                                        GroupLayout.LEADING).add(folder1Label,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE).add(
                                                        layout.createSequentialGroup().add(expandAllButton)
                                                                .addPreferredGap(LayoutStyle.RELATED)
                                                                .add(collapseAllButton).addPreferredGap(
                                                                LayoutStyle.RELATED).add(
                                                                hierarchyComboBox,
                                                                GroupLayout.PREFERRED_SIZE,
                                                                GroupLayout.DEFAULT_SIZE,
                                                                GroupLayout.PREFERRED_SIZE)))
                                                .addPreferredGap(LayoutStyle.RELATED)
                                                .add(compareButton).addPreferredGap(
                                                LayoutStyle.RELATED).add(refreshButton)
                                                .add(59, 59, 59).add(deleteLeftButton).addPreferredGap(
                                                LayoutStyle.RELATED).add(
                                                copyToLeftButton).add(18, 18, 18).add(copyToRightButton)
                                                .addPreferredGap(LayoutStyle.RELATED)
                                                .add(deleteRightButton).addPreferredGap(
                                                LayoutStyle.RELATED, 199,
                                                Short.MAX_VALUE).add(
                                                layout.createParallelGroup(
                                                        GroupLayout.TRAILING).add(
                                                        layout.createSequentialGroup().add(onlyLeftButton)
                                                                .addPreferredGap(
                                                                        LayoutStyle.RELATED).add(
                                                                leftRightChangedButton).addPreferredGap(
                                                                LayoutStyle.RELATED).add(
                                                                onlyRightButton).addPreferredGap(
                                                                LayoutStyle.RELATED).add(
                                                                leftRightUnChangedButton)).add(folder2Label,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE))).add(
                                jScrollPane1, GroupLayout.DEFAULT_SIZE, 875,
                                Short.MAX_VALUE)).addContainerGap()));

        layout.linkSize(new Component[]{leftRightChangedButton,
                leftRightUnChangedButton, onlyLeftButton, onlyRightButton},
                GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(layout.createParallelGroup(
                GroupLayout.LEADING).add(
                layout.createSequentialGroup().addContainerGap().add(
                        layout.createParallelGroup(GroupLayout.LEADING)
                                .add(folder1Label, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE).add(folder2Label,
                                GroupLayout.PREFERRED_SIZE,
                                GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                        LayoutStyle.RELATED).add(
                        layout.createParallelGroup(GroupLayout.CENTER).add(
                                expandAllButton).add(collapseAllButton).add(hierarchyComboBox,
                                GroupLayout.PREFERRED_SIZE,
                                GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE).add(compareButton)
                                .add(refreshButton).add(deleteLeftButton).add(copyToLeftButton)
                                .add(copyToRightButton).add(deleteRightButton).add(onlyLeftButton)
                                .add(leftRightChangedButton).add(onlyRightButton).add(
                                leftRightUnChangedButton)).addPreferredGap(
                        LayoutStyle.RELATED).add(jScrollPane1,
                        GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
                        .addContainerGap()));
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected JButton collapseAllButton;
    protected JButton compareButton;
    protected JButton copyToLeftButton;
    protected JButton copyToRightButton;
    protected JButton deleteLeftButton;
    protected JButton deleteRightButton;
    protected JButton expandAllButton;
    protected DiffLabel folder1Label;
    protected DiffLabel folder2Label;
    protected JMTreeTable folderTreeTable;
    protected JComboBox hierarchyComboBox;
    protected JScrollPane jScrollPane1;
    protected JToggleButton leftRightChangedButton;
    protected JToggleButton leftRightUnChangedButton;
    protected JToggleButton onlyLeftButton;
    protected JToggleButton onlyRightButton;
    protected JButton refreshButton;
    // End of variables declaration//GEN-END:variables

}
