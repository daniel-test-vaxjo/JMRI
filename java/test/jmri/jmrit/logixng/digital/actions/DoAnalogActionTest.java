package jmri.jmrit.logixng.digital.actions;

import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test ActionMany
 * 
 * @author Daniel Bergqvist 2018
 */
public class DoAnalogActionTest extends AbstractDigitalActionTestBase {

    @Test
    public void testCtor() {
        Assert.assertNotNull("exists", new DoAnalogAction("IQA55:10:DA321"));
    }
    
    // The minimal setup for log4J
    @Before
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.resetInstanceManager();
        JUnitUtil.initInternalSensorManager();
        JUnitUtil.initInternalTurnoutManager();
        JUnitUtil.initLogixNGManager();
        JUnitUtil.initDigitalExpressionManager();
        JUnitUtil.initDigitalActionManager();
        JUnitUtil.initAnalogExpressionManager();
        JUnitUtil.initAnalogActionManager();
        _base = new DoAnalogAction("IQA55:10:DA321");
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }
    
}
