package jmri.jmrit.logixng.digital.actions;

import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test ActionTurnout
 * 
 * @author Daniel Bergqvist 2018
 */
public class ShutdownComputerTest extends AbstractDigitalActionTestBase {

    @Test
    public void testCtor() {
        Assert.assertNotNull("exists", new ShutdownComputer("IQA55:10:DA321", null, 0));
    }
    
    // The minimal setup for log4J
    @Before
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.resetInstanceManager();
        JUnitUtil.initInternalSensorManager();
        JUnitUtil.initInternalTurnoutManager();
        _base = new ShutdownComputer("IQA55:10:DA321", null, 0);
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }
    
}
