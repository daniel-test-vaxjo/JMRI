package jmri.jmrit.logixng.digital.implementation;

import jmri.jmrit.logixng.digital.implementation.DefaultMaleDigitalExpressionSocket;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import jmri.jmrit.logixng.digital.expressions.And;
import jmri.jmrit.logixng.DigitalExpression;

/**
 * Test ExpressionTimer
 * 
 * @author Daniel Bergqvist 2018
 */
public class DefaultMaleDigitalExpressionSocketTest {

    @Test
    public void testCtor() {
        DigitalExpression expression = new And("IQA55:E321");
        new DefaultMaleDigitalExpressionSocket(expression);
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