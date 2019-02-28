package jmri.jmrit.logixng.expressions;

import jmri.jmrit.logixng.expressions.ExpressionHold;
import jmri.jmrit.logixng.Expression;
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
        new ExpressionHold("IQA55:E321");
    }
    
    @Test
    public void testShortDescription() {
        Expression e1 = new ExpressionHold("IQA55:E321");
        Assert.assertTrue("Hold while E1. Trigger on E2".equals(e1.getShortDescription()));
    }
    
    // The minimal setup for log4J
    @Before
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.resetProfileManager();
        JUnitUtil.resetInstanceManager();
        JUnitUtil.initInternalSensorManager();
        JUnitUtil.initInternalTurnoutManager();
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }
    
}