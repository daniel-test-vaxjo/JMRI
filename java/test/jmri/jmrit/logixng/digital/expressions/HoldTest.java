package jmri.jmrit.logixng.digital.expressions;

import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import jmri.jmrit.logixng.DigitalExpression;

/**
 * Test Hold
 * 
 * @author Daniel Bergqvist 2018
 */
public class HoldTest {

    @Test
    public void testCtor() {
        DigitalExpression t = new Hold("IQA55:E321");
        Assert.assertNotNull("exists",t);
    }
    
    @Test
    public void testDescription() {
        DigitalExpression e1 = new Hold("IQA55:E321");
        Assert.assertTrue("Hold".equals(e1.getShortDescription()));
        Assert.assertTrue("Hold while E1. Trigger on E2".equals(e1.getLongDescription()));
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