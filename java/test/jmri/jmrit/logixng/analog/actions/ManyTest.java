package jmri.jmrit.logixng.analog.actions;

import jmri.InstanceManager;
import jmri.NamedBean;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.ConditionalNG;
import jmri.jmrit.logixng.ConditionalNG_Manager;
import jmri.jmrit.logixng.AnalogAction;
import jmri.jmrit.logixng.AnalogActionBean;
import jmri.jmrit.logixng.AnalogActionManager;
import jmri.jmrit.logixng.AnalogExpressionBean;
import jmri.jmrit.logixng.AnalogExpressionManager;
import jmri.jmrit.logixng.DigitalActionManager;
import jmri.jmrit.logixng.LogixNG;
import jmri.jmrit.logixng.LogixNG_Manager;
import jmri.jmrit.logixng.MaleAnalogActionSocket;
import jmri.jmrit.logixng.MaleSocket;
import jmri.jmrit.logixng.SocketAlreadyConnectedException;
import jmri.jmrit.logixng.digital.actions.DoAnalogAction;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Many
 * 
 * @author Daniel Bergqvist 2018
 */
public class ManyTest extends AbstractAnalogActionTestBase {

    LogixNG logixNG;
    ConditionalNG conditionalNG;
    
    @Override
    public ConditionalNG getConditionalNG() {
        return conditionalNG;
    }
    
    @Override
    public LogixNG getLogixNG() {
        return logixNG;
    }
    
    @Override
    public MaleSocket getConnectableChild() {
        Many action = new Many("IQAA999", null);
        MaleSocket maleSocket =
                InstanceManager.getDefault(AnalogActionManager.class).registerAction(action);
        return maleSocket;
    }
    
    @Override
    public String getExpectedPrintedTree() {
        return String.format(
                "Many%n" +
                "   !~ A1%n" +
                "      Socket not connected%n");
    }
    
    @Override
    public String getExpectedPrintedTreeFromRoot() {
        return String.format(
                "LogixNG: A new logix for test%n" +
                "   ConditionalNG: A conditionalNG%n" +
                "      ! %n" +
                "         Read analog E and set analog A%n" +
                "            ?~ E%n" +
                "               Socket not connected%n" +
                "            !~ A%n" +
                "               Many%n" +
                "                  !~ A1%n" +
                "                     Socket not connected%n");
    }
    
    @Override
    public NamedBean createNewBean(String systemName) {
        return new Many(systemName, null);
    }
    
    @Test
    public void testCtor() {
        Assert.assertNotNull("exists", new Many("IQAA321", null));
    }
    
    @Test
    public void testGetChild() throws SocketAlreadyConnectedException {
        Many action2 = new Many("IQAA321", null);
        
        for (int i=0; i < 3; i++) {
            Assert.assertTrue("getChildCount() returns "+i, i+1 == action2.getChildCount());

            Assert.assertNotNull("getChild(0) returns a non null value",
                    action2.getChild(0));

            boolean hasThrown = false;
            try {
                action2.getChild(i+1);
            } catch (IndexOutOfBoundsException ex) {
                hasThrown = true;
                String msg = String.format("Index: %d, Size: %d", i+1, i+1);
                Assert.assertEquals("Error message is correct", msg, ex.getMessage());
            }
            Assert.assertTrue("Exception is thrown", hasThrown);
            
            // Connect a new child expression
            AnalogActionMemory expr = new AnalogActionMemory("IQAA"+i, null);
            MaleSocket maleSocket =
                    InstanceManager.getDefault(AnalogActionManager.class).registerAction(expr);
            action2.getChild(i).connect(maleSocket);
        }
    }
    
    @Test
    public void testCategory() {
        Assert.assertTrue("Category matches", Category.COMMON == _base.getCategory());
    }
    
    @Test
    public void testIsExternal() {
        Assert.assertFalse("is external", _base.isExternal());
    }
    
    // The minimal setup for log4J
    @Before
    public void setUp() throws SocketAlreadyConnectedException {
        JUnitUtil.setUp();
        JUnitUtil.resetInstanceManager();
        JUnitUtil.initInternalSensorManager();
        JUnitUtil.initInternalTurnoutManager();
        
        _category = Category.COMMON;
        _isExternal = false;
        
        logixNG = InstanceManager.getDefault(LogixNG_Manager.class).createLogixNG("A new logix for test");  // NOI18N
        conditionalNG = InstanceManager.getDefault(ConditionalNG_Manager.class)
                .createConditionalNG("A conditionalNG");  // NOI18N
        conditionalNG.setEnabled(true);
        conditionalNG.setRunOnGUIDelayed(false);
        logixNG.addConditionalNG(conditionalNG);
        
        DoAnalogAction doAnalogAction = new DoAnalogAction("IQDA321", null);
        MaleSocket maleSocketDoAnalogAction =
                InstanceManager.getDefault(DigitalActionManager.class)
                        .registerAction(doAnalogAction);
        conditionalNG.getChild(0).connect(maleSocketDoAnalogAction);
        
        Many action = new Many("IQAA321", null);
        MaleSocket maleSocket =
                InstanceManager.getDefault(AnalogActionManager.class).registerAction(action);
        doAnalogAction.getChild(1).connect(maleSocket);
        _base = action;
        _baseMaleSocket = maleSocket;
        
        logixNG.setParentForAllChildren();
        logixNG.setEnabled(true);
        logixNG.activateLogixNG();
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }
    
}