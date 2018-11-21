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

import javax.swing.JPanel;

/**
 * An extension of JPanel to serve as base class for...
 * 
 * @author jmeld-legacy 
 * @author Rick Wellman
 *
 */
@SuppressWarnings("serial")
abstract public class AbstractBarDialog extends JPanel {
    
  // Instance variables:
  private JMeldPanel meldPanel;

  public AbstractBarDialog(JMeldPanel meldPanel) {
    this.meldPanel = meldPanel;

    init();
  }

  final public void activate() {
    _activate();
  }

  final public void deactivate() {
    _deactivate();
  }
  
  abstract protected void init();

  abstract protected void _activate();

  abstract protected void _deactivate();

  protected JMeldPanel getMeldPanel() {
    return meldPanel;
  }

}
