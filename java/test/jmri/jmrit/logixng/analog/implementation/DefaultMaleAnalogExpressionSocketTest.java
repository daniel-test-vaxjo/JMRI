package jmri.jmrit.logixng.analog.implementation;

import jmri.jmrit.logixng.analog.implementation.DefaultMaleAnalogExpressionSocket;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import jmri.jmrit.logixng.analog.expressions.GetAnalogIO;
import jmri.jmrit.logixng.AnalogExpression;

/**
 * Test ExpressionTimer
 * 
 * @author Daniel Bergqvist 2018
 */
public class DefaultMaleAnalogExpressionSocketTest {

    @Test
    public void testCtor() {
        AnalogExpression expression = new GetAnalogIO("IQA55:E321");
        new DefaultMaleAnalogExpressionSocket(expression);
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
