package org.jmeld.diff;

import java.math.BigDecimal;

/**
 * 
 * @author Rick Wellman
 *
 */
public class CompareCSF extends BaseCompare implements CompareAware {

    private final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    
    @Override
    public boolean compare(Object o1, Object o2) {
        // Implement logic here
        return true;
    }
    
}
