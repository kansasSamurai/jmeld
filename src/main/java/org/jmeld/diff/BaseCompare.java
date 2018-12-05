package org.jmeld.diff;

import java.math.BigDecimal;

/**
 * 
 * @author rwellman
 *
 */
abstract public class BaseCompare {

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
