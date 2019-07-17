package jmri.jmrit.logixng.analog.implementation;

import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import jmri.jmrit.logixng.analog.actions.AnalogActionMemory;
import jmri.jmrit.logixng.AnalogActionBean;

/**
 * Test ExpressionTimer
 * 
 * @author Daniel Bergqvist 2018
 */
public class DefaultMaleAnalogActionSocketTest {

    @Test
    public void testCtor() {
        AnalogActionBean action = new AnalogActionMemory("IQA55:10:AA321");
        Assert.assertNotNull("object exists", new DefaultMaleAnalogActionSocket(action));
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
