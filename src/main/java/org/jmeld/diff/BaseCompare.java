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

import java.math.BigDecimal;

/**
 * 
 * @author Rick Wellman
 *
 */
abstract public class BaseCompare implements CompareAware {

    public BaseCompare() {
        EclipseDiff.setCompareObject(this);
    }
    
    /**
     * An almost useless default implementation of CompareAware;
     * you are expected to override this.  If you do not override
     * this method, your compare user interface will not show
     * any differences.  This behavior was chosen so that you 
     * would hopefully know right away that something is wrong
     * and it alerts you that you have not done something right.
     */
    @Override
    public boolean compare(Object o1, Object o2) {
        return true;
    }
    
    /**
     * Given two strings which represent numeric values, return their
     * difference as an absolute difference at a scale based on their 
     * most significant digits.
     * <p>
     * This algorithm accepts both integer and decimal values and treats
     * them equally, as follows (be sure to read clarification at the end):
     * <p>
     * <b>Integers</b> By example: <ul>
     * <li>Given the integers 10 and 11, this would return 1. </li>
     * <li>Given the integers 10 and 15, this would return 5. </li>
     * <li>Given the integers 15 and 10, this would also return 5. </li>
     * <li>Given the integers 23 and 8, this would return... </li>
     * </ul><p>
     * <b>Decimals</b> By example, <ul>
     * <li>Given the integers 10 and 11, this would return 1. </li>
     * <li>Given the integers 10 and 15, this would return 5. </li>
     * <li>Given the integers 15 and 10, this would also return 5. </li>
     * <li>Given the integers 23 and 8, this would return... </li>
     * </ul><p>
     * 
     * @param left
     * @param right
     * @return
     */
    protected int getAbsDiff(String left, String right) {
        
        BigDecimal lvalue = new BigDecimal(left);
        lvalue = lvalue.scaleByPowerOfTen(lvalue.scale()); //.multiply(power); // (ONE_HUNDRED);
        
        BigDecimal rvalue = new BigDecimal(right);
        rvalue = rvalue.scaleByPowerOfTen(rvalue.scale()); //.multiply(power); // (ONE_HUNDRED);
        
        return Math.abs( lvalue.subtract(rvalue).intValue() );
    }

}
