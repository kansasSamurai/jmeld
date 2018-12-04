/* 
   JWeld - A diff and merge API plus GUI - Originally forked from JMeld
   Copyright (C) 2018  Rick Wellman - GNU LGPL   
   -----
      
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
 * An interface to allow custom compare algorithms to extend the raw comparison algorithm.
 * 
 * The intent is that an implementation of this interface will be called only if the 
 * base algorithm determines an inequality (to emphasize... NOT equal).  The implementor
 * can then scrutinize the objects compared and provide further rules for determining
 * if the two objects should be considered different.  For example, a file format
 * may define one or more "fields" that represent an "amount".  An implementation
 * of this interface could choose to ignore differences in the amount that are
 * under or over an arbitrary threshold.
 * 
 * @author Rick Wellman
 *
 */
public interface CompareAware {

    public boolean compare(Object o1, Object o2);
    
}
