package jmri.jmrit.logixng.implementation;

import java.util.*;

import jmri.*;
import jmri.jmrit.logixng.*;
import jmri.jmrit.logixng.digital.actions.AbstractDigitalAction;
import jmri.jmrit.logixng.digital.actions.IfThenElse;
import jmri.util.JUnitAppender;
import jmri.util.JUnitUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test DefaultConditionalNG
 * 
 * @author Daniel Bergqvist 2020
 */
public class DefaultConditionalNGTest {

    @Test
    public void testCtor() {
        DefaultConditionalNG conditionalNG = new DefaultConditionalNG("IQC123", null);
        Assert.assertNotNull("exists", conditionalNG);
        
        boolean hasThrown = false;
        try {
            // Bad system name
            new DefaultConditionalNG("IQCAbc", null);
        } catch (IllegalArgumentException e) {
            hasThrown = true;
            Assert.assertEquals("Error message is correct", "system name is not valid", e.getMessage());
        }
        Assert.assertTrue("Exception thrown", hasThrown);
    }
    
    @Test
    public void testEnableExecution() throws SocketAlreadyConnectedException {
        MyDigitalAction action = new MyDigitalAction("IQDA1", null);
        DefaultConditionalNG conditionalNG = new DefaultConditionalNG("IQC123", null);
        
        MaleSocket socket = InstanceManager.getDefault(DigitalActionManager.class)
                .registerAction(action);
        
        Assert.assertFalse("Enable execution is not supported when no child is connected",
                conditionalNG.supportsEnableExecution());
        
        conditionalNG.getChild(0).connect(socket);
        
        Assert.assertFalse("Enable execution is not supported",
                conditionalNG.supportsEnableExecution());
        
        // Test with an action that implements DigitalActionWithEnableExecution
        DigitalActionBean actionSupportExecution = new IfThenElse("IQDA2", null);
        socket = InstanceManager.getDefault(DigitalActionManager.class)
                .registerAction(actionSupportExecution);
        conditionalNG.getChild(0).disconnect();
        conditionalNG.getChild(0).connect(socket);
        Assert.assertTrue("Enable execution is supported",
                conditionalNG.supportsEnableExecution());
    }
    
    @Test
    public void testIsExecutionEnabled() throws SocketAlreadyConnectedException {
        DefaultConditionalNG conditionalNG = new DefaultConditionalNG("IQC123", null);
        
        boolean hasThrown = false;
        try {
            conditionalNG.isExecutionEnabled();
        } catch (UnsupportedOperationException e) {
            hasThrown = true;
            Assert.assertEquals("Error message is correct", "This conditionalNG does not supports the method isExecutionEnabled()", e.getMessage());
        }
        Assert.assertTrue("Exception thrown", hasThrown);
        JUnitAppender.assertErrorMessage("This conditionalNG does not supports the method isExecutionEnabled()");
        
        
        // Test an action that doesn't support enable execution
        MyDigitalAction action = new MyDigitalAction("IQDA1", null);
        MaleSocket socket = InstanceManager.getDefault(DigitalActionManager.class)
                .registerAction(action);
        conditionalNG.getChild(0).connect(socket);
        
        hasThrown = false;
        try {
            conditionalNG.isExecutionEnabled();
        } catch (UnsupportedOperationException e) {
            hasThrown = true;
            Assert.assertEquals("Error message is correct", "This conditionalNG does not supports the method isExecutionEnabled()", e.getMessage());
        }
        Assert.assertTrue("Exception thrown", hasThrown);
        JUnitAppender.assertErrorMessage("This conditionalNG does not supports the method isExecutionEnabled()");
        
        
        // Test an action that support enable execution
        conditionalNG.getChild(0).disconnect();
        
        IfThenElse action2 = new IfThenElse("IQDA2", null);
        MaleSocket socket2 = InstanceManager.getDefault(DigitalActionManager.class)
                .registerAction(action2);
        conditionalNG.getChild(0).connect(socket2);
        Assert.assertTrue("execution is enabled", conditionalNG.isExecutionEnabled());
        
        // Turn off enable execution on the action
        action2.setEnableExecution(false);
        Assert.assertFalse("execution is not enabled", conditionalNG.isExecutionEnabled());
    }
    
    @Test
    public void testSetEnableExecution() throws SocketAlreadyConnectedException {
        DefaultConditionalNG conditionalNG = new DefaultConditionalNG("IQC123", null);
        
        boolean hasThrown = false;
        try {
            conditionalNG.setEnableExecution(true);
        } catch (UnsupportedOperationException e) {
            hasThrown = true;
            Assert.assertEquals("Error message is correct", "This conditionalNG does not supports the method setEnableExecution()", e.getMessage());
        }
        Assert.assertTrue("Exception thrown", hasThrown);
        JUnitAppender.assertErrorMessage("This conditionalNG does not supports the method setEnableExecution()");
        
        
        // Test an action that doesn't support enable execution
        MyDigitalAction action = new MyDigitalAction("IQDA1", null);
        MaleSocket socket = InstanceManager.getDefault(DigitalActionManager.class)
                .registerAction(action);
        conditionalNG.getChild(0).connect(socket);
        
        hasThrown = false;
        try {
            conditionalNG.setEnableExecution(true);
        } catch (UnsupportedOperationException e) {
            hasThrown = true;
            Assert.assertEquals("Error message is correct", "This conditionalNG does not supports the method setEnableExecution()", e.getMessage());
        }
        Assert.assertTrue("Exception thrown", hasThrown);
        JUnitAppender.assertErrorMessage("This conditionalNG does not supports the method setEnableExecution()");
        
        
        // Test an action that support enable execution
        conditionalNG.getChild(0).disconnect();
        
        IfThenElse action2 = new IfThenElse("IQDA2", null);
        MaleSocket socket2 = InstanceManager.getDefault(DigitalActionManager.class)
                .registerAction(action2);
        conditionalNG.getChild(0).connect(socket2);
        Assert.assertTrue("execution is enabled", conditionalNG.isExecutionEnabled());
        
        // Turn off enable execution on the action
        conditionalNG.setEnableExecution(false);
        Assert.assertFalse("execution is not enabled", conditionalNG.isExecutionEnabled());
        
        // Turn on enable execution on the action
        conditionalNG.setEnableExecution(true);
        Assert.assertTrue("execution is enabled", conditionalNG.isExecutionEnabled());
    }
    
    @Test
    public void testLock() {
        DefaultConditionalNG conditionalNG = new DefaultConditionalNG("IQC123", null);
        
        conditionalNG.setLock(Base.Lock.NONE);
        Assert.assertEquals("Lock is correct", Base.Lock.NONE, conditionalNG.getLock());
        
        conditionalNG.setLock(Base.Lock.USER_LOCK);
        Assert.assertEquals("Lock is correct", Base.Lock.USER_LOCK, conditionalNG.getLock());
        
        conditionalNG.setLock(Base.Lock.HARD_LOCK);
        Assert.assertEquals("Lock is correct", Base.Lock.HARD_LOCK, conditionalNG.getLock());
    }
    
    @Test
    public void testState() throws JmriException {
        DefaultConditionalNG conditionalNG = new DefaultConditionalNG("IQC123", null);
        conditionalNG.setState(NamedBean.INCONSISTENT);
        JUnitAppender.assertWarnMessage("Unexpected call to setState in DefaultConditionalNG.");
        
        Assert.assertEquals("State is correct", NamedBean.UNKNOWN, conditionalNG.getState());
        JUnitAppender.assertWarnMessage("Unexpected call to getState in DefaultConditionalNG.");
    }
    
    @Test
    public void testExecute() throws SocketAlreadyConnectedException, JmriException {
        DefaultConditionalNG conditionalNG = new DefaultConditionalNG("IQC123", null);
        conditionalNG.setRunOnGUIDelayed(false);
        MyDigitalAction action = new MyDigitalAction("IQDA1", null);
        MaleSocket socket = InstanceManager.getDefault(DigitalActionManager.class)
                .registerAction(action);
        conditionalNG.getChild(0).connect(socket);
        
        socket.setErrorHandlingType(MaleSocket.ErrorHandlingType.THROW);
        
        action.throwOnExecute = false;
        action.hasExecuted = false;
        conditionalNG.execute();
        Assert.assertTrue("Action is executed", action.hasExecuted);
        
//        action.throwOnExecute = true;
//        action.hasExecuted = false;
//        conditionalNG.setErrorHandlingType(MaleSocket.ErrorHandlingType.SHOW_DIALOG_BOX);
//        conditionalNG.execute();
//        JUnitAppender.assertErrorMessage("An exception has occured during execute: IQC123");
        
        action.throwOnExecute = true;
        action.hasExecuted = false;
        conditionalNG.setErrorHandlingType(MaleSocket.ErrorHandlingType.LOG_ERROR);
        conditionalNG.execute();
        JUnitAppender.assertErrorMessage("ConditionalNG IQC123 got an exception during execute: jmri.JmriException: An error has occured");
        
        action.throwOnExecute = true;
        action.hasExecuted = false;
        conditionalNG.setErrorHandlingType(MaleSocket.ErrorHandlingType.LOG_ERROR_ONCE);
        conditionalNG.execute();
        JUnitAppender.assertWarnMessage("ConditionalNG IQC123 got an exception during execute: jmri.JmriException: An error has occured");
        
        action.throwOnExecute = true;
        action.hasExecuted = false;
        conditionalNG.setErrorHandlingType(MaleSocket.ErrorHandlingType.THROW);
        conditionalNG.execute();
        JUnitAppender.assertErrorMessage("ConditionalNG IQC123 got an exception during execute: jmri.JmriException: An error has occured");
    }
    
    @Test
    public void testDescription() {
        DefaultConditionalNG conditionalNG = new DefaultConditionalNG("IQC123", null);
        Assert.assertEquals("Short description is correct", "ConditionalNG: IQC123", conditionalNG.getShortDescription());
        Assert.assertEquals("Long description is correct", "ConditionalNG: IQC123", conditionalNG.getLongDescription());
    }
    
    @Test
    public void testErrorHandlingType() {
        DefaultConditionalNG conditionalNG = new DefaultConditionalNG("IQC123", null);
        Assert.assertEquals("Error handling type is correct", MaleSocket.ErrorHandlingType.LOG_ERROR, conditionalNG.getErrorHandlingType());
        conditionalNG.setErrorHandlingType(MaleSocket.ErrorHandlingType.SHOW_DIALOG_BOX);
        Assert.assertEquals("Error handling type is correct", MaleSocket.ErrorHandlingType.SHOW_DIALOG_BOX, conditionalNG.getErrorHandlingType());
        conditionalNG.setErrorHandlingType(MaleSocket.ErrorHandlingType.LOG_ERROR);
        Assert.assertEquals("Error handling type is correct", MaleSocket.ErrorHandlingType.LOG_ERROR, conditionalNG.getErrorHandlingType());
        conditionalNG.setErrorHandlingType(MaleSocket.ErrorHandlingType.LOG_ERROR_ONCE);
        Assert.assertEquals("Error handling type is correct", MaleSocket.ErrorHandlingType.LOG_ERROR_ONCE, conditionalNG.getErrorHandlingType());
        conditionalNG.setErrorHandlingType(MaleSocket.ErrorHandlingType.THROW);
        Assert.assertEquals("Error handling type is correct", MaleSocket.ErrorHandlingType.THROW, conditionalNG.getErrorHandlingType());
    }
    
    // The minimal setup for log4J
    @Before
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.resetInstanceManager();
        JUnitUtil.resetProfileManager();
        JUnitUtil.initConfigureManager();
        JUnitUtil.initInternalSensorManager();
        JUnitUtil.initInternalTurnoutManager();
        JUnitUtil.initLogixNGManager();
        
        InstanceManager.getDefault(LogixNGPreferences.class).setLimitRootActions(false);
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }
    
    
    
    private static class MyDigitalAction extends AbstractDigitalAction {

        private boolean hasExecuted;
        private boolean throwOnExecute;
        
        public MyDigitalAction(String sys, String user) throws BadUserNameException, BadSystemNameException {
            super(sys, user);
        }

        @Override
        protected void registerListenersForThisClass() {
            throw new UnsupportedOperationException("Not supported");
        }

        @Override
        protected void unregisterListenersForThisClass() {
            throw new UnsupportedOperationException("Not supported");
        }

        @Override
        protected void disposeMe() {
            throw new UnsupportedOperationException("Not supported");
        }

        @Override
        public String getShortDescription(Locale locale) {
            throw new UnsupportedOperationException("Not supported");
        }

        @Override
        public String getLongDescription(Locale locale) {
            throw new UnsupportedOperationException("Not supported");
        }

        @Override
        public FemaleSocket getChild(int index) throws IllegalArgumentException, UnsupportedOperationException {
            throw new UnsupportedOperationException("Not supported");
        }

        @Override
        public int getChildCount() {
            throw new UnsupportedOperationException("Not supported");
        }

        @Override
        public Category getCategory() {
            throw new UnsupportedOperationException("Not supported");
        }

        @Override
        public boolean isExternal() {
            throw new UnsupportedOperationException("Not supported");
        }

        @Override
        public void setup() {
            throw new UnsupportedOperationException("Not supported");
        }

        @Override
        public void execute() throws JmriException {
            if (throwOnExecute) {
                throw new JmriException ("An error has occured");
            } else {
                hasExecuted = true;
            }
        }
        
    }
    
}
