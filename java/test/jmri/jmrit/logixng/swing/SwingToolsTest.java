package jmri.jmrit.logixng.swing;

// import java.awt.GraphicsEnvironment;
import jmri.jmrit.logixng.DigitalAction;
import jmri.jmrit.logixng.digital.actions.ActionTurnout;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/**
 * Test SwingToolsTest
 * 
 * @author Daniel Bergqvist 2018
 */
public class SwingToolsTest {

    @Test
    public void testCtor() {
//        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        
        DigitalAction action = new ActionTurnout("IQ1:DA1");
        Class actionClass = ActionTurnout.class;
        
        Assert.assertTrue("Class name is correct",
                "jmri.jmrit.logixng.digital.actions.configureswing.ActionTurnoutSwing"
                        .equals(SwingTools.adapterNameForObject(action)));
        
        Assert.assertTrue("Class name is correct",
                "jmri.jmrit.logixng.digital.actions.configureswing.ActionTurnoutSwing"
                        .equals(SwingTools.adapterNameForClass(actionClass)));
        
        Assert.assertTrue("Class is correct",
                "jmri.jmrit.logixng.digital.actions.configureswing.ActionTurnoutSwing"
                        .equals(SwingTools.getSwingConfiguratorForObject(action).getClass().getName()));
        
        Assert.assertTrue("Class is correct",
                "jmri.jmrit.logixng.digital.actions.configureswing.ActionTurnoutSwing"
                        .equals(SwingTools.getSwingConfiguratorForClass(actionClass).getClass().getName()));
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
