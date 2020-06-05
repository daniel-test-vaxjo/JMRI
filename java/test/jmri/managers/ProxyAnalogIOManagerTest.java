package jmri.managers;

import java.beans.PropertyChangeListener;
import jmri.AnalogIO;
import jmri.InstanceManager;
import jmri.JmriException;
import jmri.Light;
import jmri.LightManager;
import jmri.implementation.AbstractNamedBean;
import jmri.jmrix.internal.InternalAnalogIOManager;
import jmri.jmrix.internal.InternalSystemConnectionMemo;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import jmri.AnalogIOManager;

/**
 * Test the ProxyAnalogIOManager.
 *
 * @author  Bob Jacobsen 2003, 2006, 2008
 * @author  Daniel Bergqvist Copyright (C) 2020
 */
public class ProxyAnalogIOManagerTest {

    public String getSystemName(int i) {
        return "JV" + i;
    }

    protected AnalogIOManager l = null;     // holds objects under test

    static protected boolean listenerResult = false;

    protected class Listen implements PropertyChangeListener {

        @Override
        public void propertyChange(java.beans.PropertyChangeEvent e) {
            listenerResult = true;
        }
    }

    private AnalogIO newAnalogIO(String sysName, String userName) {
        return new MyAnalogIO(sysName, userName);
    }
    
    @Test
    public void testDispose() {
        l.dispose();  // all we're really doing here is making sure the method exists
    }

    @Test
    public void testAnalogIOPutGet() {
        // create
        AnalogIO t = newAnalogIO(getSystemName(getNumToTest1()), "mine");
        l.register(t);
        // check
        Assert.assertTrue("real object returned ", t != null);
        Assert.assertTrue("user name correct ", t == l.getByUserName("mine"));
        Assert.assertTrue("system name correct ", t == l.getBySystemName(getSystemName(getNumToTest1())));
    }

    @Test
    public void testSingleObject() {
        // test that you always get the same representation
        AnalogIO t1 = newAnalogIO(getSystemName(getNumToTest1()), "mine");
        l.register(t1);
        Assert.assertTrue("t1 real object returned ", t1 != null);
        Assert.assertTrue("same by user ", t1 == l.getByUserName("mine"));
        Assert.assertTrue("same by system ", t1 == l.getBySystemName(getSystemName(getNumToTest1())));
    }

    @Test
    public void testMisses() {
        // try to get nonexistant lights
        Assert.assertTrue(null == l.getByUserName("foo"));
        Assert.assertTrue(null == l.getBySystemName("bar"));
    }

    @Test
    public void testRename() {
        // get light
        AnalogIO t1 = newAnalogIO(getSystemName(getNumToTest1()), "before");
        Assert.assertNotNull("t1 real object ", t1);
        l.register(t1);
        t1.setUserName("after");
        AnalogIO t2 = l.getByUserName("after");
        Assert.assertEquals("same object", t1, t2);
        Assert.assertEquals("no old object", null, l.getByUserName("before"));
    }

    @Test
    public void testInstanceManagerIntegration() {
        jmri.util.JUnitUtil.resetInstanceManager();
        Assert.assertNotNull(InstanceManager.getDefault(AnalogIOManager.class));

        Assert.assertTrue(InstanceManager.getDefault(AnalogIOManager.class) instanceof ProxyAnalogIOManager);

        Assert.assertNotNull(InstanceManager.getDefault(AnalogIOManager.class));
        AnalogIO b = newAnalogIO("IV1", null);
        InstanceManager.getDefault(AnalogIOManager.class).register(b);
        Assert.assertNotNull(InstanceManager.getDefault(AnalogIOManager.class).getBySystemName("IV1"));

        InternalAnalogIOManager m = new InternalAnalogIOManager(new InternalSystemConnectionMemo("J", "Juliet"));
        InstanceManager.setAnalogIOManager(m);

        b = newAnalogIO("IV2", null);
        InstanceManager.getDefault(AnalogIOManager.class).register(b);
        Assert.assertNotNull(InstanceManager.getDefault(AnalogIOManager.class).getBySystemName("IV1"));
        b = newAnalogIO("IV3", null);
        InstanceManager.getDefault(AnalogIOManager.class).register(b);
        Assert.assertNotNull(InstanceManager.getDefault(AnalogIOManager.class).getBySystemName("IV1"));
    }

    @Test
    public void testLights() {
        // Test that the AnalogIOManager registers variable lights but not
        // lights that are not variable.
        
        Light light = new MyLight("JL1");
        InstanceManager.getDefault(LightManager.class).register(light);
        AnalogIO analogIO = InstanceManager.getDefault(AnalogIOManager.class).getBySystemName("JL1");
        Assert.assertNull("light does not exists in AnalogIOManager", analogIO);
        
        // Check that we can deregister light without problem
        InstanceManager.getDefault(LightManager.class).deregister(light);
        analogIO = InstanceManager.getDefault(AnalogIOManager.class).getBySystemName("JL1");
        Assert.assertNull("light does not exists in AnalogIOManager", analogIO);
        
        Light variableLight = new MyVariableLight("JL2");
        InstanceManager.getDefault(LightManager.class).register(variableLight);
        analogIO = InstanceManager.getDefault(AnalogIOManager.class).getBySystemName("JL2");
        Assert.assertNotNull("variable light exists in AnalogIOManager", analogIO);
        
        // Check that we can deregister light and that it get deregstered from AnalogIOManager as well
        InstanceManager.getDefault(LightManager.class).deregister(variableLight);
        analogIO = InstanceManager.getDefault(AnalogIOManager.class).getBySystemName("JL2");
        Assert.assertNull("light does not exists in AnalogIOManager", analogIO);
    }

    @Test
    public void testLights_UserName() {
        // Test that the AnalogIOManager registers variable lights but not
        // lights that are not variable.
        
        Light light = new MyLight("JL1");
        InstanceManager.getDefault(LightManager.class).register(light);
        AnalogIO analogIO = InstanceManager.getDefault(AnalogIOManager.class).getByUserName("A light");
        Assert.assertNull("light does not exists in AnalogIOManager", analogIO);
        
        // Check that we can deregister light without problem
        InstanceManager.getDefault(LightManager.class).deregister(light);
        analogIO = InstanceManager.getDefault(AnalogIOManager.class).getByUserName("A light");
        Assert.assertNull("light does not exists in AnalogIOManager", analogIO);
        
        Light variableLight = new MyVariableLight("JL2", "A variable light");
        InstanceManager.getDefault(LightManager.class).register(variableLight);
        analogIO = InstanceManager.getDefault(AnalogIOManager.class).getByUserName("A variable light");
        Assert.assertNotNull("variable light exists in AnalogIOManager", analogIO);
        
        // Check that we can deregister light and that it get deregstered from AnalogIOManager as well
        InstanceManager.getDefault(LightManager.class).deregister(variableLight);
        analogIO = InstanceManager.getDefault(AnalogIOManager.class).getByUserName("A variable light");
        Assert.assertNull("light does not exists in AnalogIOManager", analogIO);
    }

    /**
     * Number of light to test. Made a separate method so it can be overridden
     * in subclasses that do or don't support various numbers.
     * 
     * @return the number to test
     */
    protected int getNumToTest1() {
        return 9;
    }

    protected int getNumToTest2() {
        return 7;
    }

    @Before
    public void setUp() {
        JUnitUtil.setUp();
        // create and register the manager object
        l = new InternalAnalogIOManager(new InternalSystemConnectionMemo("J", "Juliet"));
        jmri.InstanceManager.setAnalogIOManager(l);
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }

    
    private class MyAnalogIO extends AbstractNamedBean implements AnalogIO {

        double _value = 0.0;
        
        public MyAnalogIO(String sys, String userName) {
            super(sys, userName);
        }
        
        @Override
        public void setState(int s) throws JmriException {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public int getState() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public String getBeanType() {
            return "AnalogIO";
        }

        @Override
        public void setCommandedAnalogValue(double value) throws JmriException {
            _value = value;
        }

        @Override
        public double getCommandedAnalogValue() {
            return _value;
        }

        @Override
        public double getMin() {
            return Float.MIN_VALUE;
        }

        @Override
        public double getMax() {
            return Float.MAX_VALUE;
        }

        @Override
        public double getResolution() {
            return 0.1;
        }

        @Override
        public AbsoluteOrRelative getAbsoluteOrRelative() {
            return AbsoluteOrRelative.ABSOLUTE;
        }
    
    }
    
    
    private class MyLight extends jmri.implementation.AbstractLight {
        
        public MyLight(String systemName) {
            super(systemName);
        }
        
    }
    
    
    private class MyVariableLight extends jmri.implementation.AbstractVariableLight {

        public MyVariableLight(String systemName) {
            super(systemName);
        }

        public MyVariableLight(String systemName, String userName) {
            super(systemName, userName);
        }

        @Override
        protected void sendIntensity(double intensity) {
            // No need to implement this method for this test
            throw new UnsupportedOperationException("Not supported");
        }

        @Override
        protected void sendOnOffCommand(int newState) {
            // No need to implement this method for this test
            throw new UnsupportedOperationException("Not supported");
        }

        @Override
        protected int getNumberOfSteps() {
            // No need to implement this method for this test
            throw new UnsupportedOperationException("Not supported");
        }
        
    }
    
    private final static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProxyAnalogIOManagerTest.class);
    
}
