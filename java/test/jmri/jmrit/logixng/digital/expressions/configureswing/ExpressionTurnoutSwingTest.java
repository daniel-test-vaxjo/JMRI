package jmri.jmrit.logixng.digital.expressions.configureswing;

import jmri.jmrit.logixng.digital.actions.configureswing.*;
import java.awt.GraphicsEnvironment;
import javax.swing.JPanel;
import jmri.util.JUnitUtil;
import jmri.jmrit.logixng.digital.expressions.ExpressionTurnout;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/**
 * Test ActionTurnout
 * 
 * @author Daniel Bergqvist 2018
 */
public class ExpressionTurnoutSwingTest {

    @Test
    public void testCtor() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        
        ExpressionTurnoutSwing t = new ExpressionTurnoutSwing();
        Assert.assertNotNull("exists",t);
    }
    
    @Test
    public void testPanel() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        
        ExpressionTurnoutSwing t = new ExpressionTurnoutSwing();
        JPanel panel = t.getConfigPanel();
        Assert.assertNotNull("exists",panel);
    }
    
    @Test
    public void testCreatePanel() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        
        Assert.assertTrue("panel is not null",
            null != new ExpressionTurnoutSwing().getConfigPanel());
        Assert.assertTrue("panel is not null",
            null != new ExpressionTurnoutSwing().getConfigPanel(new ExpressionTurnout("IQ1:DA1")));
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
