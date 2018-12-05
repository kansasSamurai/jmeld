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
   
    private static CompareAware compareObject;
    
    /**
     * No-args Constructor
     */
    public EclipseDiff() {
    }

    /**
     * Allow injection of a CompareAware implementation.
     * 
     * @param co
     */
    public void setCompareObject(CompareAware co) {
        compareObject = co;
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
            else {
                if (compareObject != null)  {
                    return compareObject.compare(o1, o2); 
                } else {
                    int format = 2;
                    switch (format) {
                    case 1: return new CompareCSF().compare(o1, o2); 
                    case 2: return new CompareCRISP().compare(o1, o2);
                    }                                    
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
    
}
