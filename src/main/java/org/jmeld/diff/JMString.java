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

package org.jmeld.diff;

/**
 * A utility class for JMDiff
 * 
 * This class was previously a private class to JMDiff but is now exposed
 * to support use cases such as Groovy scripts.
 * 
 * @author Rick Wellman
 *
 */
public class JMString {
    
    String s;
    int lineNumber;

    public JMString() {}
    
    public JMString(String astring, int lineno) {
        this.s = astring;
        this.lineNumber = lineno;
        // System.out.println(" " + this);
    }
    
    @Override
    public int hashCode() {
        return s.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return s.equals(((JMString) o).s);
    }

    @Override
    public String toString() {
        return "[" + lineNumber + "] " + s;
    }
    
}
