package jmri.jmrit.newlogix;

import jmri.NewLogixCategory;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test ExpressionTimer
 * 
 * @author Daniel Bergqvist 2018
 */
public class ExpressionTimerTest {

    @Test
    public void testCtor() {
        new ExpressionTimer();
    }
    
    @Test
    public void testGetCategory() {
        Assert.assertTrue(NewLogixCategory.COMMON.equals(new ExpressionTimer().getCategory()));
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