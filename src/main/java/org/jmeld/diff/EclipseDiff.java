/*
   JMeld is a visual diff and merge tool.
   Copyright (C) 2007  Kees Kuip
   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2.1 of the License, or (at your option) any later version.
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.
   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 51 Franklin Street, Fifth Floor,
   Boston, MA  02110-1301  USA
 */
package org.jmeld.diff;

import java.math.BigDecimal;

import org.eclipse.compare.rangedifferencer.IRangeComparator;
import org.eclipse.compare.rangedifferencer.RangeDifference;
import org.eclipse.compare.rangedifferencer.RangeDifferencer;
import org.jmeld.JMeldException;

/**
 * 
 * @author jmeld-legacy
 * @author Rick Wellman
 *
 */
public class EclipseDiff extends AbstractJMDiffAlgorithm {
   
    /**
     * No-args Constructor
     */
    public EclipseDiff() {
    }

    @Override
    public JMRevision diff(Object[] orig, Object[] rev) throws JMeldException {

        final RangeDifference[] differences = 
            RangeDifferencer.findDifferences(
                new RangeComparator(orig),
                new RangeComparator(rev)   );

        return buildRevision(differences, orig, rev);
    }

    /**
     * Converts results of Eclipse' RangeDifferencer into JMeld data model.
     * 
     * Specifically, creates top level JMRevision object and, for each Eclipse RangeDifference object,
     * convert it to a JMDelta/JmChunk object and add it to the JMRevision.
     * 
     * @param differences
     * @param orig
     * @param rev
     * @return
     */
    private JMRevision buildRevision(RangeDifference[] differences, Object[] orig, Object[] rev) {

        if (orig == null) {
            throw new IllegalArgumentException("original sequence is null");
        }

        if (rev == null) {
            throw new IllegalArgumentException("revised sequence is null");
        }

        final JMRevision result = new JMRevision(orig, rev);
        for (RangeDifference rd : differences) {
            result.add(new JMDelta( result,
                    new JMChunk(rd.leftStart(), rd.leftLength()),
                    new JMChunk(rd.rightStart(), rd.rightLength())   ));
        }

        return result;
    }

    private class RangeComparator implements IRangeComparator {

        private Object[] objectArray;

        RangeComparator(Object[] objectArray) {
            this.objectArray = objectArray;
        }

        @Override
        public int getRangeCount() {
            return objectArray.length;
        }

        @Override
        public boolean rangesEqual(int thisIndex, IRangeComparator other, int otherIndex) {

            final Object o1 = objectArray[thisIndex];
            final Object o2 = ((RangeComparator) other).objectArray[otherIndex];

            if (o1 == o2) {
                // Would this ever happen in our use case?
                return true;
            }

            // The check for o1 == null is sufficient, right?... because...
            // if o1 is null and o2 is null, then the method would have returned in the previous check.
            // Therefore, if o1 is null at this point, then o2 MUST be not null.
            if (o1 == null && o2 != null) {
                return false;
            }

            // Same logic goes here... if o2 is null at this point, then o1 MUST be not null.
            // Therefore, we COULD combine these two statements to be:
            // if (o1 == null || o2 == null) ...
            if (o1 != null && o2 == null) {
                return false;
            }
            
            if (o1.equals(o2)) { return true; }
// ======================= TSS Customization ==========================
            else if (this.compareCSTM(o1, o2)) { return true; }

            return false;
        }

// ======================= TSS Customization ==========================
        private boolean compareCSTM(Object o1, Object o2) {

            // These are called first
            if (o1 instanceof JMDiff.JMString && o2 instanceof JMDiff.JMString) {
                final String left = ((JMDiff.JMString)o1).s;
                final String right = ((JMDiff.JMString)o2).s;

                if ( (left.length() < segmentindex) || (right.length() < segmentindex) ) {
                    return left.equals(right);
                }
                
                String segment = left.substring(0,4);
                switch (segment) {
                case "HEAD":
                case "CUST":
                case "ACCT":
                case "CSTM":
                case "GENE":
                case "DEPO":
                case "FUND":
                case "LOCK":
                case "INFO":
                case "AMT2":
                case "SAMT":
                    if ( right.startsWith(segment) ) 
                        return compareStatementStrings(segment, left, right); // true;
                    else 
                        return false;
                }
                
                return left.equals(right);
            }
         
            // These are called next... reason pending
            if (o1 instanceof String && o2 instanceof String) {
                final String left = (String)o1;
                final String right = (String)o2;
                return left.equals(right); // this.compareStatementStrings(left, right);
            }
            
            return false;
        }
        
        private boolean compareStatementStrings(String segment, String left, String right) {

            if (true) { // Level 1 // used to do this but then re-designed: leftSegment.equals(rightSegment)
                
                final String[] larray = left.split("~");
                final String[] rarray = right.split("~");
                
                switch (segment) {
                case "HEAD":
                    for (int i=0; i < larray.length; i++) {
                        switch (i) {
                        case 0: // HEAD
                        case 1: // 
                        case 5: //
                            if ( ! larray[i].equals(rarray[i]) ) return false;
                            break;
                        default:
                            // do nothing; i.e. ignore differences in these fields
                        }
                    }
                    return true; // break; < unnecessary
                case "CUST":
                case "ACCT":
                    for (int i=0; i < larray.length; i++) {
                        switch (i) {
                        case 0: // segment id
                        case 1: // 
                        case 2: //
                        case 3: //
                        case 4: //
                            if ( ! larray[i].equals(rarray[i]) ) return false;
                            break;
                        default:
                            // do nothing; i.e. ignore differences in these fields
                        }
                    }
                    return true; // break; < unnecessary
                case "CSTM":
                    counterCSTM++;
                    for (int i=0; i < larray.length; i++) {
                        if (i < valueindex) {
                            if ( ! larray[i].equals(rarray[i]) ) return false;
                        } else {
                            left = larray[i].replace("+", "").replace("-", "").replace(",", "");
                            right = rarray[i].replace("+", "").replace("-", "").replace(",", "");
                            final BigDecimal lvalue = new BigDecimal(left).multiply(ONE_HUNDRED);
                            final BigDecimal rvalue = new BigDecimal(right).multiply(ONE_HUNDRED);
                            final int delta =  Math.abs( lvalue.subtract(rvalue).intValue() );
                            if      ( delta == 1 ) deltaCSTM[0] += 1; // { deltaCSTM[0] += 1; return true; } 
                            else if ( delta < 11 ) deltaCSTM[1] += 1;
                            else if ( delta < 101 ) deltaCSTM[2] += 1;
                            else if ( delta < 1001 ) deltaCSTM[3] += 1;
                            else                      deltaCSTM[4] += 1;
                            return true; // for now... all deltas of value are to be ignored
                        }
                    }
                    break;
                case "SAMT":
                case "AMT2":
                    counterSAMT++;
                    return true; // break; < unnecessary
                case "GENE":
                case "DEPO":
                case "FUND":
                case "LOCK":
                case "INFO":
                    for (int i=0; i < larray.length; i++) {
                        if (i < 3) {
                            if ( ! larray[i].equals(rarray[i]) ) return false;
                        } else {
                            // If we get this far, the "prefix" is the same; ignore the rest by returning true.
                            return true; 
                        }
                    }
                    break;
                default:
                    return left.equals(right);
                }

            }
            
            return false;
        }

        @Override
        public boolean skipRangeComparison(int length, int maxLength, IRangeComparator other) {
            // Q: When does this get called? i.e. sequence diagram
            return false;
        }

    } // end class RangeComparator

 // ======================= TSS Customization ==========================
    private int counterCSTM; private int[] deltaCSTM = { 0, 0, 0, 0, 0 };
    private int counterSAMT;
    private int valueindex = 4; // just here so that I can change at debug time if necessary
    private int segmentindex = 4;
    private final BigDecimal ONE_HUNDRED = new BigDecimal("100");

}
