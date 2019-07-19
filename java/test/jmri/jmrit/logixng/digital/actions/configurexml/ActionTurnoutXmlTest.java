package jmri.jmrit.logixng.digital.actions.configurexml;

import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test ActionTurnoutXml
 * 
 * @author Daniel Bergqvist 2019
 */
public class ActionTurnoutXmlTest {

    @Test
    public void testCtor() {
        ActionTurnoutXml b = new ActionTurnoutXml();
        Assert.assertNotNull("exists", b);
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
