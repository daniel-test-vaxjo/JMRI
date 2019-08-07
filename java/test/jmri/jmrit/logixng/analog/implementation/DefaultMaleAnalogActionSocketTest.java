package jmri.jmrit.logixng.analog.implementation;

import jmri.InstanceManager;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import jmri.jmrit.logixng.AnalogActionBean;
import jmri.jmrit.logixng.AnalogActionManager;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.FemaleSocket;
import jmri.jmrit.logixng.MaleSocketTestBase;
import jmri.jmrit.logixng.analog.actions.AbstractAnalogAction;
import jmri.jmrit.logixng.analog.actions.AnalogActionMemory;

/**
 * Test ExpressionTimer
 * 
 * @author Daniel Bergqvist 2018
 */
public class DefaultMaleAnalogActionSocketTest extends MaleSocketTestBase {

    @Override
    protected String getNewSystemName() {
        return InstanceManager.getDefault(AnalogActionManager.class)
                .getNewSystemName();
    }
    
    @Test
    public void testCtor() {
        AnalogActionBean action = new AnalogActionMemory("IQAA321");
        Assert.assertNotNull("object exists", new DefaultMaleAnalogActionSocket(action));
    }
    
    // The minimal setup for log4J
    @Before
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.resetInstanceManager();
        JUnitUtil.initInternalSensorManager();
        JUnitUtil.initInternalTurnoutManager();
        
        AnalogActionBean actionA = new AnalogActionMemory("IQAA321");
        Assert.assertNotNull("exists", actionA);
        AnalogActionBean actionB = new MyAnalogAction("IQAA322");
        Assert.assertNotNull("exists", actionA);
        
        maleSocketA =
                InstanceManager.getDefault(AnalogActionManager.class)
                        .registerAction(actionA);
        Assert.assertNotNull("exists", maleSocketA);
        
        maleSocketB =
                InstanceManager.getDefault(AnalogActionManager.class)
                        .registerAction(actionB);
        Assert.assertNotNull("exists", maleSocketA);
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }
    
    
    /**
     * This action is different from AnalogActionMemory and is used to test the
     * male socket.
     */
    private class MyAnalogAction extends AbstractAnalogAction {
        
        MyAnalogAction(String sysName) {
            super(sysName);
        }

        @Override
        protected void registerListenersForThisClass() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        protected void unregisterListenersForThisClass() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        protected void disposeMe() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public String getShortDescription() {
            return "My short description";
        }

        @Override
        public String getLongDescription() {
            return "My long description";
        }

        @Override
        public Base getNewObjectBasedOnTemplate(String sys) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public FemaleSocket getChild(int index) throws IllegalArgumentException, UnsupportedOperationException {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public int getChildCount() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Category getCategory() {
            return Category.COMMON;
        }

        @Override
        public boolean isExternal() {
            return false;
        }

        @Override
        public void setup() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setValue(double value) {
            throw new UnsupportedOperationException("Not supported.");
        }
        
    }
    
}
