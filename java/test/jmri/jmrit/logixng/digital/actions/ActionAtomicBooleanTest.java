package jmri.jmrit.logixng.digital.actions;

import java.util.concurrent.atomic.AtomicBoolean;
import jmri.InstanceManager;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.ConditionalNG;
import jmri.jmrit.logixng.ConditionalNG_Manager;
import jmri.jmrit.logixng.DigitalActionManager;
import jmri.jmrit.logixng.LogixNG;
import jmri.jmrit.logixng.LogixNG_Manager;
import jmri.jmrit.logixng.MaleSocket;
import jmri.jmrit.logixng.SocketAlreadyConnectedException;
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
public class ActionAtomicBooleanTest extends AbstractDigitalActionTestBase {

    private LogixNG logixNG;
    private ConditionalNG conditionalNG;
    private AtomicBoolean atomicBoolean;
    private ActionAtomicBoolean actionAtomicBoolean;
    
    
    @Override
    public ConditionalNG getConditionalNG() {
        return conditionalNG;
    }
    
    @Override
    public LogixNG getLogixNG() {
        return logixNG;
    }
    
    @Override
    public String getExpectedPrintedTree() {
        return String.format("Set the atomic boolean to true%n");
    }
    
    @Override
    public String getExpectedPrintedTreeFromRoot() {
        return String.format(
                "LogixNG: A logixNG%n" +
                "   ConditionalNG: A conditionalNG%n" +
                "      ! %n" +
                "         Set the atomic boolean to true%n");
    }
    
    @Test
    public void testCtor() {
        Assert.assertTrue("object exists", _base != null);
        
        ActionAtomicBoolean action2;
        Assert.assertNotNull("atomicBoolean is not null", atomicBoolean);
        atomicBoolean.set(true);
        
        action2 = new ActionAtomicBoolean("IQDA321", null);
        Assert.assertNotNull("object exists", action2);
        Assert.assertNull("Username matches", action2.getUserName());
        Assert.assertEquals("String matches", "Set the atomic boolean to false", action2.getLongDescription());
        
        action2 = new ActionAtomicBoolean("IQDA321", "My atomicBoolean");
        Assert.assertNotNull("object exists", action2);
        Assert.assertEquals("Username matches", "My atomicBoolean", action2.getUserName());
        Assert.assertEquals("String matches", "Set the atomic boolean to false", action2.getLongDescription());
        
        action2 = new ActionAtomicBoolean("IQDA321", null);
        action2.setAtomicBoolean(atomicBoolean);
        Assert.assertTrue("atomic boolean is correct", atomicBoolean == action2.getAtomicBoolean());
        Assert.assertNotNull("object exists", action2);
        Assert.assertNull("Username matches", action2.getUserName());
        Assert.assertEquals("String matches", "Set the atomic boolean to false", action2.getLongDescription());
        
        AtomicBoolean ab = new AtomicBoolean();
        action2 = new ActionAtomicBoolean("IQDA321", "My atomicBoolean");
        action2.setAtomicBoolean(ab);
        Assert.assertTrue("atomic boolean is correct", ab == action2.getAtomicBoolean());
        Assert.assertNotNull("object exists", action2);
        Assert.assertEquals("Username matches", "My atomicBoolean", action2.getUserName());
        Assert.assertEquals("String matches", "Set the atomic boolean to false", action2.getLongDescription());
        
        // Test template
        action2 = (ActionAtomicBoolean)_base.getNewObjectBasedOnTemplate();
        Assert.assertNotNull("object exists", action2);
        Assert.assertNull("Username is null", action2.getUserName());
//        Assert.assertTrue("Username matches", "My atomicBoolean".equals(expression2.getUserName()));
        Assert.assertEquals("String matches", "Set the atomic boolean to false", action2.getLongDescription());
        
        boolean thrown = false;
        try {
            // Illegal system name
            new ActionAtomicBoolean("IQA55:12:XY11", null);
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        Assert.assertTrue("Expected exception thrown", thrown);
        
        thrown = false;
        try {
            // Illegal system name
            new ActionAtomicBoolean("IQA55:12:XY11", "A name");
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        Assert.assertTrue("Expected exception thrown", thrown);
        
        // Test setup(). This method doesn't do anything, but execute it for coverage.
        _base.setup();
    }
    
    @Test
    public void testAction() throws SocketAlreadyConnectedException {
        // Set new value to true
        actionAtomicBoolean.setNewValue(true);
        Assert.assertTrue("new value is true", actionAtomicBoolean.getNewValue());
        // The action is not yet executed so the atomic boolean should be false
        Assert.assertFalse("atomicBoolean is false",atomicBoolean.get());
        // Execute the conditional
        conditionalNG.execute();
        // The action should now be executed so the atomic boolean should be true
        Assert.assertTrue("atomicBoolean is true",atomicBoolean.get());
        
        // Set new value to false
        actionAtomicBoolean.setNewValue(false);
        Assert.assertFalse("new value is false", actionAtomicBoolean.getNewValue());
        // Execute the conditional
        conditionalNG.execute();
        // The action should now be executed so the atomic boolean should be true
        Assert.assertFalse("atomicBoolean is false",atomicBoolean.get());
    }
    
    @Test
    public void testCategory() {
        Assert.assertTrue("Category matches", Category.OTHER == _base.getCategory());
    }
    
    @Test
    public void testIsExternal() {
        Assert.assertTrue("is external", _base.isExternal());
    }
    
    @Test
    public void testShortDescription() {
        Assert.assertEquals("String matches", "Set the atomic boolean", _base.getShortDescription());
    }
    
    @Test
    public void testLongDescription() {
        Assert.assertEquals("String matches", "Set the atomic boolean to true", _base.getLongDescription());
    }
    
    @Test
    public void testChild() {
        Assert.assertTrue("Num children is zero", 0 == _base.getChildCount());
        boolean hasThrown = false;
        try {
            _base.getChild(0);
        } catch (UnsupportedOperationException ex) {
            hasThrown = true;
            Assert.assertTrue("Error message is correct", "Not supported.".equals(ex.getMessage()));
        }
        Assert.assertTrue("Exception is thrown", hasThrown);
    }
    
    // The minimal setup for log4J
    @Before
    public void setUp() throws SocketAlreadyConnectedException {
        JUnitUtil.setUp();
        JUnitUtil.resetInstanceManager();
        JUnitUtil.initInternalSensorManager();
        JUnitUtil.initInternalTurnoutManager();
        
        atomicBoolean = new AtomicBoolean(false);
        logixNG = InstanceManager.getDefault(LogixNG_Manager.class).createLogixNG("A logixNG");
        conditionalNG = InstanceManager.getDefault(ConditionalNG_Manager.class)
                .createConditionalNG("A conditionalNG");  // NOI18N
        logixNG.addConditionalNG(conditionalNG);
        conditionalNG.setRunOnGUIDelayed(false);
        conditionalNG.setEnabled(true);
        actionAtomicBoolean = new ActionAtomicBoolean("IQDA321", null, atomicBoolean, true);
        MaleSocket socket =
                InstanceManager.getDefault(DigitalActionManager.class).registerAction(actionAtomicBoolean);
        conditionalNG.getChild(0).connect(socket);
        
        _base = actionAtomicBoolean;
        _baseMaleSocket = socket;
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }
    
}