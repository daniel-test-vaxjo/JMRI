package jmri.jmrit.logixng.digital.implementation;

import jmri.jmrit.logixng.digital.implementation.DefaultMaleDigitalActionSocket;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import jmri.jmrit.logixng.digital.actions.Many;
import jmri.jmrit.logixng.DigitalActionBean;

/**
 * Test ExpressionTimer
 * 
 * @author Daniel Bergqvist 2018
 */
public class DefaultMaleDigitalActionSocketTest {

    @Test
    public void testCtor() {
        DigitalActionBean action = new Many("IQDA321");
        Assert.assertNotNull("exists", new DefaultMaleDigitalActionSocket(action));
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
