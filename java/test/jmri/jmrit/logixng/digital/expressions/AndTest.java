package jmri.jmrit.logixng.digital.expressions;

import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test And
 * 
 * @author Daniel Bergqvist 2018
 */
public class AndTest {

    @Test
    public void testCtor() {
        new And("IQA55:E321");
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