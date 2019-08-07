package jmri.jmrit.logixng.string.implementation;

import jmri.InstanceManager;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.FemaleSocket;
import jmri.jmrit.logixng.MaleSocketTestBase;
import jmri.jmrit.logixng.StringActionBean;
import jmri.jmrit.logixng.StringActionManager;
import jmri.jmrit.logixng.string.actions.AbstractStringAction;
import jmri.jmrit.logixng.string.actions.StringActionMemory;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test ExpressionTimer
 * 
 * @author Daniel Bergqvist 2018
 */
public class DefaultMaleStringActionSocketTest extends MaleSocketTestBase {

    @Override
    protected String getNewSystemName() {
        return InstanceManager.getDefault(StringActionManager.class)
                .getNewSystemName();
    }
    
    // The minimal setup for log4J
    @Before
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.resetInstanceManager();
        JUnitUtil.initInternalSensorManager();
        JUnitUtil.initInternalTurnoutManager();
        
        StringActionBean actionA = new StringActionMemory("IQSA321");
        Assert.assertNotNull("exists", actionA);
        StringActionBean actionB = new MyAnalogAction("IQSA322");
        Assert.assertNotNull("exists", actionA);
        
        maleSocketA =
                InstanceManager.getDefault(StringActionManager.class)
                        .registerAction(actionA);
        Assert.assertNotNull("exists", maleSocketA);
        
        maleSocketB =
                InstanceManager.getDefault(StringActionManager.class)
                        .registerAction(actionB);
        Assert.assertNotNull("exists", maleSocketA);
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }
    
    
    /**
     * This action is different from MyAnalogAction and is used to test the
 male socket.
     */
    private class MyAnalogAction extends AbstractStringAction {
        
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
        public void setValue(String value) {
            throw new UnsupportedOperationException("Not supported.");
        }
        
    }
    
}
