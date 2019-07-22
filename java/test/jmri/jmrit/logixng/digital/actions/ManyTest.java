package jmri.jmrit.logixng.digital.actions;

import jmri.InstanceManager;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.DigitalAction;
import jmri.jmrit.logixng.DigitalActionBean;
import jmri.jmrit.logixng.DigitalActionManager;
import jmri.jmrit.logixng.MaleDigitalActionSocket;
import jmri.jmrit.logixng.MaleSocket;
import jmri.jmrit.logixng.SocketAlreadyConnectedException;
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
public class ManyTest extends AbstractDigitalActionTestBase {

    @Test
    public void testCtor() {
        Assert.assertNotNull("exists", new Many("IQA55:10:DA321"));
    }
    
    @Test
    @Override
    public void testSupportsEnableExecution() throws SocketAlreadyConnectedException {
        DigitalAction da = new Many("IQA55:10:DA321");
        
        // By default, doesn't support enable execution
        Assert.assertFalse("supportsEnableExecution() returns correct value",
                da.supportsEnableExecution());
        
        // Support enable execution if hard lock
        da.setLock(Base.Lock.HARD_WITH_CHILDREN_LOCK);
        Assert.assertTrue("supportsEnableExecution() returns correct value",
                da.supportsEnableExecution());
        
        // Support enable execution if hard lock
        da.setLock(Base.Lock.HARD_LOCK);
        Assert.assertTrue("supportsEnableExecution() returns correct value",
                da.supportsEnableExecution());
        
        // Doesn't support enable execution if any of the children doesn't
        // support it.
        DigitalActionBean da2 = new Many("IQA55:10:DA322");
        MaleSocket socket = InstanceManager.getDefault(DigitalActionManager.class)
                .registerAction(da2);
        da.getChild(0).connect(socket);
        Assert.assertFalse("supportsEnableExecution() returns correct value",
                ((MaleDigitalActionSocket)socket).supportsEnableExecution());
        Assert.assertFalse("supportsEnableExecution() returns correct value",
                da.supportsEnableExecution());
        
        // But support enable execution if all children supports enabled execution
        socket.setLock(Base.Lock.HARD_LOCK);
        Assert.assertTrue("supportsEnableExecution() returns correct value",
                ((MaleDigitalActionSocket)socket).supportsEnableExecution());
        Assert.assertTrue("supportsEnableExecution() returns correct value",
                da.supportsEnableExecution());
    }
    
    // The minimal setup for log4J
    @Before
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.resetInstanceManager();
        JUnitUtil.initInternalSensorManager();
        JUnitUtil.initInternalTurnoutManager();
        _base = new Many("IQA55:10:DA321");
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }
    
}
