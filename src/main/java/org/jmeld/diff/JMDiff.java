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

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jmeld.JMeldException;
import org.jmeld.ui.text.AbstractBufferDocument;
import org.jmeld.util.Ignore;
import org.jmeld.util.StopWatch;
import org.jmeld.util.file.CompareUtil;

/**
 * This is the class that actually performs/starts the "diff" process.
 * Currently, it only invokes the EclipseDiff instance.
 * 
 * A brief analysis of this class shows that this is/could/should be a singleton.
 * However, it currently uses a shared/static buffer so I am guessing that it is
 * possible for this to be mis-used since several classes instantiate new
 * instances of this JMDiff class (use IDE 'references' feature).
 * 
 * So... I'm not currently sure if it IS causing issues, but it looks like
 * it is susceptible if used in the wrong way.
 * 
 * @author jmeld-legacy
 * @author Rick Wellman committed
 *
 */
public class JMDiff {

    public static int BUFFER_SIZE = 100000;

    // Class variables:

    // Allocate a charBuffer once for performance. 
    // The charbuffer is used to store a 'line' WITHOUT the ignored characters.
    static final private CharBuffer inputLine = CharBuffer.allocate(BUFFER_SIZE);
    static final private CharBuffer outputLine = CharBuffer.allocate(BUFFER_SIZE);

    // Instance variables:
    private List<JMDiffAlgorithmIF> algorithms;

    public JMDiff() {

        this.algorithms = new ArrayList<JMDiffAlgorithmIF>();
        // Timing/Memory (msec/Mb):
        //                                             Myers  Eclipse GNU Hunt
        //  ================================================================================
        //  2 Totally different files  (116448 lines)  31317  1510    340 195
        //  2 Totally different files  (232896 lines)  170673 212     788 354
        //  2 Medium different files  (1778583 lines)  41     55      140 24679
        //  2 Medium different files (10673406 lines)  216    922     632 >300000
        //  2 Equal files             (1778583 lines)  32     55      133 24632
        //  2 Equal files            (10673406 lines)  121    227     581 >60000
    
        // MyersDiff is the fastest but can be very slow when 2 files are very different.
        MyersDiff myersDiff = new MyersDiff();
        myersDiff.checkMaxTime(true);
        //algorithms.add(myersDiff);
    
        // EclipseDiff looks like Myersdiff but is slower.
        // It performs much better if the files are totally different
        algorithms.add(new EclipseDiff());
    
        // HuntDiff (from netbeans) is very, very slow
        //algorithms.add(new HuntDiff());
    
    }

    public JMRevision diff(List<String> a, List<String> b, Ignore ignore) throws JMeldException {
        if (a == null) {
            a = Collections.emptyList();
        }
        if (b == null) {
            b = Collections.emptyList();
        }
        return diff(a.toArray(), b.toArray(), ignore);
    }

    public JMRevision diff(Object[] a, Object[] b, Ignore ignore) throws JMeldException {
        
        Object[] org = a;
        if (org == null) {
            org = new Object[] {};
        }

        Object[] rev = b;
        if (rev == null) {
            rev = new Object[] {};
        }

        final boolean filtered = org instanceof AbstractBufferDocument.Line[] && rev instanceof AbstractBufferDocument.Line[];
        final StopWatch sp = new StopWatch(); {
            sp.start();
            if (filtered) {
                org = filter(ignore, org);
                rev = filter(ignore, rev);
            }
        } long filteredTime = sp.getElapsedTime();

        // This is setup to iterate over the list but in production code there is only one item in the list (EclipseDiff)
        for (JMDiffAlgorithmIF algorithm : algorithms) {
            try {
                final JMRevision revision = algorithm.diff(org, rev);                
                revision.setIgnore(ignore);
                revision.update(a, b);
                // revision.filter();
                
                if (filtered) {
                    this.adjustRevision(revision, a, (JMString[]) org, b, (JMString[]) rev);
                }

                if (a.length > 1000) {
                    System.out.println("diff took " + sp.getElapsedTime() + " msec. [filter=" + filteredTime + " msec]["
                            + algorithm.getClass() + "]");
                }

                return revision;
            } catch (JMeldException ex) {
                if (ex.getCause() instanceof MaxTimeExceededException) {
                    System.out.println("Time exceeded for " + algorithm.getClass() + ": try next algorithm");
                } else {
                    throw ex;
                }
            }
        }

        return null;
    }

    /**
     * 
     * @param revision
     * @param orgArray
     * @param orgArrayFiltered
     * @param revArray
     * @param revArrayFiltered
     */
    private void adjustRevision(
        JMRevision revision, 
        Object[] orgArray, JMString[] orgArrayFiltered, 
        Object[] revArray, JMString[] revArrayFiltered) {

        for (JMDelta delta : revision.getDeltas()) {
            JMChunk chunk = delta.getOriginal();
            
            // System.out.print(" original=" + chunk);
            int index = chunk.getAnchor();

            int anchor = (index < orgArrayFiltered.length)
                    ? orgArrayFiltered[index].lineNumber
                    : orgArray.length ;

            int size = chunk.getSize();
            if (size > 0) {
                index += chunk.getSize() - 1;
                if (index < orgArrayFiltered.length) {
                    size = orgArrayFiltered[index].lineNumber - anchor + 1;
                }
                /*
                 * index += chunk.getSize(); if (index < orgArrayFiltered.length) { size =
                 * orgArrayFiltered[index].lineNumber - anchor; }
                 */
            }
            chunk.setAnchor(anchor);
            chunk.setSize(size);
            // System.out.println(" => " + chunk);

            chunk = delta.getRevised();
            // System.out.print(" revised=" + chunk);
            index = chunk.getAnchor();
            if (index < revArrayFiltered.length) {
                // System.out.print(" [index=" + index + ", text="
                // + revArrayFiltered[index].s + "]");
                anchor = revArrayFiltered[index].lineNumber;
            } else {
                anchor = revArray.length;
            }
            size = chunk.getSize();
            if (size > 0) {
                index += chunk.getSize() - 1;
                if (index < revArrayFiltered.length) {
                    size = revArrayFiltered[index].lineNumber - anchor + 1;
                }
                /*
                 * index += chunk.getSize(); if (index < revArrayFiltered.length) { size =
                 * revArrayFiltered[index].lineNumber - anchor; }
                 */
            }
            chunk.setAnchor(anchor);
            chunk.setSize(size);
            // System.out.println(" => " + chunk);
        }
    }

    private JMString[] filter(Ignore ignore, Object[] array) {
        
        synchronized (inputLine) {
            // System.out.println("> start");
            final List<JMString> result = new ArrayList<JMString>(array.length);
            int lineNumber;

            lineNumber = -1;
            for (Object o : array) {
                lineNumber++;

                inputLine.clear();
                inputLine.put(o.toString());
                CompareUtil.removeIgnoredChars(inputLine, ignore, outputLine);
                if (outputLine.remaining() == 0) {
                    continue;
                }

                result.add(new JMString(outputLine.toString(), lineNumber));
            }
            
            return result.toArray(new JMString[result.size()]);            
        }

    }

    /**
     * Though a minor/trivial class, there is no reason this needs to be an inner class.
     * 
     * @author rwellman
     *
     */
    class JMString {
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

}
