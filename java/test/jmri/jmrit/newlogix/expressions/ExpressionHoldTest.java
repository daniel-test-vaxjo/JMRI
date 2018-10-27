package jmri.jmrit.newlogix.expressions;

import jmri.jmrit.newlogix.expressions.ExpressionHold;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test ExpressionHold
 * 
 * @author Daniel Bergqvist 2018
 */
public class ExpressionHoldTest {

    @Test
    public void testCtor() {
        new ExpressionHold("IQA55:E321", null, null, null);
    }
    
    // The minimal setup for log4J
    @Before
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.resetInstanceManager();
        JUnitUtil.initInternalSensorManager();
        JUnitUtil.initInternalTurnoutManager();
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }
    
}