package jmri.jmrit.newlogix.actions.configurexml;

import jmri.jmrit.newlogix.actions.configurexml.ActionDoIfXml;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test ActionDoIfXml
 * 
 * @author Daniel Bergqvist 2018
 */
public class ActionDoIfXmlTest {

    @Test
    public void testCtor() {
        new ActionDoIfXml();
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