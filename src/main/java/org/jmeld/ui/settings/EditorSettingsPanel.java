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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jmeld.JMeld;
import org.jmeld.settings.EditorSettings;
import org.jmeld.settings.JMeldSettings;
import org.jmeld.ui.util.EmptyIcon;
import org.jmeld.ui.util.FontUtil;
import org.jmeld.ui.util.LookAndFeelManager;
import org.jmeld.ui.util.TypeTokenizerManager;
import org.jmeld.util.CharsetDetector;
import org.jmeld.util.Ignore;
import org.jmeld.util.conf.ConfigurationListenerIF;

import com.l2fprod.common.swing.JFontChooser;

/**
 * EditorPreferencePanel.java
 *
 * Created on January 10, 2007, 6:31 PM
 *
 * @author kees
 * @author Rick Wellman
 */
public class EditorSettingsPanel extends EditorSettingsForm implements ConfigurationListenerIF {
    
    private static final long serialVersionUID = 1L;

    private static JDialog colorDialog;

    private static JColorChooser colorChooser;

    private boolean originalAntialias;

    public EditorSettingsPanel() {
        originalAntialias = getEditorSettings().isAntialiasEnabled();

        initConfiguration();
        init();

        JMeldSettings.getInstance().addConfigurationListener(this);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void init() {
        // ignore:
        ignoreWhitespaceAtBeginCheckBox.addActionListener(getIgnoreWhitespaceAtBeginAction());
        ignoreWhitespaceInBetweenCheckBox.addActionListener(getIgnoreWhitespaceInBetweenAction());
        ignoreWhitespaceAtEndCheckBox.addActionListener(getIgnoreWhitespaceAtEndAction());
        ignoreEOLCheckBox.addActionListener(getIgnoreEOLAction());
        ignoreBlankLinesCheckBox.addActionListener(getIgnoreBlankLinesAction());
        ignoreCaseCheckBox.addActionListener(getIgnoreCaseAction());

        // Miscellaneous:
        leftsideReadonlyCheckBox.addActionListener(getLeftsideReadonlyAction());
        rightsideReadonlyCheckBox.addActionListener(getRightsideReadonlyAction());
        antialiasCheckBox.addActionListener(getAntialiasAction());
        tabSizeSpinner.addChangeListener(getTabSizeChangeListener());
        showLineNumbersCheckBox.addActionListener(getShowLineNumbersAction());
        lookAndFeelComboBox.setModel(getLookAndFeelModel());
        lookAndFeelComboBox.setSelectedItem(LookAndFeelManager.getInstance().getInstalledLookAndFeelName());
        lookAndFeelComboBox.addActionListener(getLookAndFeelAction());

        // Colors:
        colorAddedButton.addActionListener(getColorAddedAction());
        colorDeletedButton.addActionListener(getColorDeletedAction());
        colorChangedButton.addActionListener(getColorChangedAction());
        restoreOriginalColorsButton.addActionListener(getRestoreOriginalColorsAction());

        // Font:
        defaultFontRadioButton.addActionListener(getDefaultFontAction());
        customFontRadioButton.addActionListener(getCustomFontAction());
        fontChooserButton.addActionListener(getFontChooserAction());

        // File encoding:
        defaultEncodingRadioButton.setText(
                defaultEncodingRadioButton.getText() + " (" + CharsetDetector.getInstance().getDefaultCharset() + ")");

        defaultEncodingRadioButton.addActionListener(getDefaultEncodingAction());
        detectEncodingRadioButton.addActionListener(getDetectEncodingAction());
        specificEncodingRadioButton.addActionListener(getSpecificEncodingAction());
        specificEncodingComboBox .setModel(new DefaultComboBoxModel(CharsetDetector.getInstance().getCharsetNameList().toArray()));
        specificEncodingComboBox.setSelectedItem(getEditorSettings().getSpecificFileEncodingName());
        specificEncodingComboBox.addActionListener(getSpecificEncodingNameAction());

        // Toolbar appearance:
        toolbarButtonIconComboBox.setModel(getToolbarButtonIconModel());
        toolbarButtonIconComboBox.setSelectedItem(getEditorSettings().getToolbarButtonIcon());
        toolbarButtonIconComboBox.addActionListener(getToolbarButtonIconAction());
        toolbarButtonTextEnabledCheckBox.addActionListener(getToolbarButtonTextEnabledAction());
        showLevensteinCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                getEditorSettings().setShowLevenstheinEditor(showLevensteinCheckBox.isSelected());
            }
        });
        showTreeChunksCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                getEditorSettings().setShowTreeChunks(showTreeChunksCheckBox.isSelected());
            }
        });
        showTreeRawCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                getEditorSettings().setShowTreeRaw(showTreeRawCheckBox.isSelected());
            }
        });

        // Tokenizer Type
        typeTokenizerComboBox.setModel(getTypeTokenizerModel());
        typeTokenizerComboBox.setSelectedItem(getEditorSettings().getTypeTokenizerName());
        typeTokenizerComboBox.addActionListener(getTypeTokenizerAction());

    }

    private ChangeListener getTabSizeChangeListener() {
        return new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                getEditorSettings().setTabSize((Integer) tabSizeSpinner.getValue());
            }
        };
    }

    private ActionListener getColorAddedAction() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                final Color color = chooseColor(getEditorSettings().getAddedColor());
                if (color != null) {
                    getEditorSettings().setAddedColor(color);
                }
            }
        };
    }

    private ActionListener getColorDeletedAction() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                final Color color = chooseColor(getEditorSettings().getDeletedColor());
                if (color != null) {
                    getEditorSettings().setDeletedColor(color);
                }
            }
        };
    }

    private ActionListener getColorChangedAction() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                final Color color = chooseColor(getEditorSettings().getChangedColor());
                if (color != null) {
                    getEditorSettings().setChangedColor(color);
                }
            }
        };
    }

    private ActionListener getShowLineNumbersAction() {
        return new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getEditorSettings().setShowLineNumbers(showLineNumbersCheckBox.isSelected());
            }
        };
    }

    private ActionListener getIgnoreWhitespaceAtBeginAction() {
        return new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getEditorSettings().setIgnoreWhitespaceAtBegin(ignoreWhitespaceAtBeginCheckBox.isSelected());
            }
        };
    }

    private ActionListener getIgnoreWhitespaceInBetweenAction() {
        return new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getEditorSettings().setIgnoreWhitespaceInBetween(ignoreWhitespaceInBetweenCheckBox.isSelected());
            }
        };
    }

    private ActionListener getIgnoreWhitespaceAtEndAction() {
        return new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getEditorSettings().setIgnoreWhitespaceAtEnd(ignoreWhitespaceAtEndCheckBox.isSelected());
            }
        };
    }

    private ActionListener getIgnoreEOLAction() {
        return new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { // TODO ... shouldn't this be in a
                                                                          // SwingUtilities.invokeLater()?
                getEditorSettings().setIgnoreEOL(ignoreEOLCheckBox.isSelected());
            }
        };
    }

    private ActionListener getIgnoreBlankLinesAction() {
        return new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getEditorSettings().setIgnoreBlankLines(ignoreBlankLinesCheckBox.isSelected());
            }
        };
    }

    private ActionListener getIgnoreCaseAction() {
        return new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getEditorSettings().setIgnoreCase(ignoreCaseCheckBox.isSelected());
            }
        };
    }

    private ActionListener getLeftsideReadonlyAction() {
        return new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getEditorSettings().setLeftsideReadonly(leftsideReadonlyCheckBox.isSelected());
            }
        };
    }

    private ActionListener getRightsideReadonlyAction() {
        return new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getEditorSettings().setRightsideReadonly(rightsideReadonlyCheckBox.isSelected());
            }
        };
    }

    private ActionListener getAntialiasAction() {
        return new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getEditorSettings().enableAntialias(antialiasCheckBox.isSelected());
            }
        };
    }

    private ActionListener getRestoreOriginalColorsAction() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                getEditorSettings().restoreColors();
            }
        };
    }

    private ActionListener getDefaultEncodingAction() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                getEditorSettings().setDefaultFileEncodingEnabled(true);
                getEditorSettings().setDetectFileEncodingEnabled(false);
                getEditorSettings().setSpecificFileEncodingEnabled(false);
            }
        };
    }

    private ActionListener getDetectEncodingAction() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                getEditorSettings().setDefaultFileEncodingEnabled(false);
                getEditorSettings().setDetectFileEncodingEnabled(true);
                getEditorSettings().setSpecificFileEncodingEnabled(false);
            }
        };
    }

    private ActionListener getSpecificEncodingAction() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                getEditorSettings().setDefaultFileEncodingEnabled(false);
                getEditorSettings().setDetectFileEncodingEnabled(false);
                getEditorSettings().setSpecificFileEncodingEnabled(true);
            }
        };
    }

    private ActionListener getSpecificEncodingNameAction() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                getEditorSettings().setSpecificFileEncodingName((String) specificEncodingComboBox.getSelectedItem());
            }
        };
    }

    private ActionListener getLookAndFeelAction() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                getEditorSettings().setLookAndFeelName((String) lookAndFeelComboBox.getSelectedItem());
                LookAndFeelManager.getInstance().install();
            }
        };
    }

    private ActionListener getTypeTokenizerAction() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                getEditorSettings().setTypeTokenizerName((String) typeTokenizerComboBox.getSelectedItem());
            }
        };
    }

    private ActionListener getToolbarButtonIconAction() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                getEditorSettings().setToolbarButtonIcon(
                        (EditorSettings.ToolbarButtonIcon) toolbarButtonIconComboBox.getSelectedItem());
                JMeld.getJMeldPanel().addToolBar();
            }
        };
    }

    private ActionListener getToolbarButtonTextEnabledAction() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                getEditorSettings().setToolbarButtonTextEnabled(toolbarButtonTextEnabledCheckBox.isSelected());
                JMeld.getJMeldPanel().addToolBar();
            }
        };
    }

    private ActionListener getDefaultFontAction() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                getEditorSettings().enableCustomFont(!defaultFontRadioButton.isSelected());
            }
        };
    }

    private ActionListener getCustomFontAction() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                getEditorSettings().enableCustomFont(customFontRadioButton.isSelected());
            }
        };
    }

    private ActionListener getFontChooserAction() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                final Font font = chooseFont(getEditorFont());
                if (font != null) {
                    getEditorSettings().setFont(font);
                }
            }
        };
    }

    private Color chooseColor(Color initialColor) {
        // Do not instantiate ColorChooser multiple times because it contains a memory leak.
        if (colorDialog == null) {
            colorChooser = new JColorChooser(initialColor);
            colorDialog = JColorChooser.createDialog(null, "Choose color", true, colorChooser, null, null);
        }

        colorChooser.setColor(initialColor);
        colorDialog.setVisible(true);

        return colorChooser.getColor();
    }

    private Font chooseFont(Font initialFont) {
        final JFontChooser fontChooser = new JFontChooser();
        fontChooser.setSelectedFont(initialFont);
        
        final Font chosen = fontChooser.showFontDialog(this, "Select Font..."); // fontChooser.setVisible(true);
        return chosen; // fontChooser.getSelectedFont();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private ComboBoxModel getLookAndFeelModel() {
        return new DefaultComboBoxModel(LookAndFeelManager.getInstance().getInstalledLookAndFeels().toArray());
    }

    @SuppressWarnings("rawtypes")
    private ComboBoxModel getTypeTokenizerModel() {
        return new DefaultComboBoxModel<>(TypeTokenizerManager.getInstance().getTypeTokenizers());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private ComboBoxModel getToolbarButtonIconModel() {
        return new DefaultComboBoxModel(getEditorSettings().getToolbarButtonIcons());
    }

    public void configurationChanged() {
        initConfiguration();
    }

    private void initConfiguration() {
        EditorSettings settings;
        Font font;
        Ignore ignore;

        settings = getEditorSettings();
        ignore = settings.getIgnore();
        colorAddedButton.setIcon(new EmptyIcon(settings.getAddedColor(), 20, 20));
        colorAddedButton.setText("");
        colorDeletedButton.setIcon(new EmptyIcon(settings.getDeletedColor(), 20, 20));
        colorDeletedButton.setText("");
        colorChangedButton.setIcon(new EmptyIcon(settings.getChangedColor(), 20, 20));
        colorChangedButton.setText("");
        showLineNumbersCheckBox.setSelected(settings.getShowLineNumbers());
        ignoreWhitespaceAtBeginCheckBox.setSelected(ignore.ignoreWhitespaceAtBegin);
        ignoreWhitespaceInBetweenCheckBox.setSelected(ignore.ignoreWhitespaceInBetween);
        ignoreWhitespaceAtEndCheckBox.setSelected(ignore.ignoreWhitespaceAtEnd);
        ignoreEOLCheckBox.setSelected(ignore.ignoreEOL);
        ignoreBlankLinesCheckBox.setSelected(ignore.ignoreBlankLines);
        ignoreCaseCheckBox.setSelected(ignore.ignoreCase);
        leftsideReadonlyCheckBox.setSelected(settings.getLeftsideReadonly());
        rightsideReadonlyCheckBox.setSelected(settings.getRightsideReadonly());
        showLevensteinCheckBox.setSelected(settings.isShowLevenstheinEditor());
        showTreeChunksCheckBox.setSelected(settings.isShowTreeChunks());
        showTreeRawCheckBox.setSelected(settings.isShowTreeRaw());
        antialiasCheckBox.setSelected(settings.isAntialiasEnabled());
        if (originalAntialias != settings.isAntialiasEnabled()) {
            antialiasCheckBox.setText("antialias on (NEED A RESTART)");
        } else {
            antialiasCheckBox.setText("antialias on");
        }
        tabSizeSpinner.setValue(settings.getTabSize());
        font = getEditorFont();
        fontChooserButton.setFont(font);
        fontChooserButton.setText(font.getName() + " (" + font.getSize() + ")");
        defaultFontRadioButton.setSelected(!settings.isCustomFontEnabled());
        customFontRadioButton.setSelected(settings.isCustomFontEnabled());

        defaultEncodingRadioButton.setSelected(settings.getDefaultFileEncodingEnabled());
        detectEncodingRadioButton.setSelected(settings.getDetectFileEncodingEnabled());
        specificEncodingRadioButton.setSelected(settings.getSpecificFileEncodingEnabled());

        toolbarButtonIconComboBox.setSelectedItem(getEditorSettings().getToolbarButtonIcon());
        toolbarButtonTextEnabledCheckBox.setSelected(getEditorSettings().isToolbarButtonTextEnabled());

        revalidate();
    }

    private EditorSettings getEditorSettings() {
        return JMeldSettings.getInstance().getEditor();
    }

    private Font getEditorFont() {
        Font font = getEditorSettings().getFont();
        font = ( font == null ) ? FontUtil.defaultTextAreaFont : font;

        return font;
    }
    
}
