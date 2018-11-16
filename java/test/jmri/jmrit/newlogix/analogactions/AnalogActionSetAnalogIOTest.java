package jmri.jmrit.newlogix.analogactions;

import jmri.jmrit.newlogix.AnalogAction;
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
public class AnalogActionSetAnalogIOTest {

    @Test
    public void testCtor() {
        new AnalogActionSetAnalogIO("IQA55:A321");
    }
    
    @Test
    public void testToString() {
        AnalogAction analogAction = new AnalogActionSetAnalogIO("IQA55:A321");
        Assert.assertTrue("String matches", "Set analog none".equals(analogAction.toString()));
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
