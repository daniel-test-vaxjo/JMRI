package jmri.jmrit.logixng;

import jmri.jmrit.logixng.analog.expressions.*;
import jmri.AnalogIO;
import jmri.JmriException;
import jmri.jmrit.logixng.implementation.AbstractBase;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test AbstractAnalogExpression
 * 
 * @author Daniel Bergqvist 2018
 */
public abstract class AbstractBaseTestBase {

    protected Base _base;
    
    @Test
    public void testLock() {
        _base.setLock(Base.Lock.NONE);
        Assert.assertTrue("Enum matches", Base.Lock.NONE == _base.getLock());
        Assert.assertTrue("String matches", "No lock".equals(_base.getLock().toString()));
        Assert.assertTrue("isChangeableByUser", _base.getLock().isChangeableByUser());
        _base.setLock(Base.Lock.USER_LOCK);
        Assert.assertTrue("Enum matches", Base.Lock.USER_LOCK == _base.getLock());
        Assert.assertTrue("String matches", "User lock".equals(_base.getLock().toString()));
        Assert.assertTrue("isChangeableByUser", _base.getLock().isChangeableByUser());
        _base.setLock(Base.Lock.HARD_LOCK);
        Assert.assertTrue("Enum matches", Base.Lock.HARD_LOCK == _base.getLock());
        Assert.assertTrue("String matches", "Hard lock".equals(_base.getLock().toString()));
        Assert.assertFalse("isChangeableByUser", _base.getLock().isChangeableByUser());
        _base.setLock(Base.Lock.TEMPLATE_LOCK);
        Assert.assertTrue("Enum matches", Base.Lock.TEMPLATE_LOCK == _base.getLock());
        Assert.assertTrue("String matches", "Template lock".equals(_base.getLock().toString()));
        Assert.assertFalse("isChangeableByUser", _base.getLock().isChangeableByUser());
    }
    
    @Test
    public void testConstants() {
        Assert.assertTrue("String matches", "ChildCount".equals(Base.PROPERTY_CHILD_COUNT));
        Assert.assertTrue("String matches", "SocketConnected".equals(Base.PROPERTY_SOCKET_CONNECTED));
        Assert.assertTrue("integer matches", 0x02 == Base.SOCKET_CONNECTED);
        Assert.assertTrue("integer matches", 0x04 == Base.SOCKET_DISCONNECTED);
    }
    
    @Test
    public void testNames() {
        Assert.assertNotNull("system name not null", _base.getSystemName());
        Assert.assertFalse("system name is not empty string", _base.getSystemName().isEmpty());
        
        _base.setUserName("One user name");
        Assert.assertTrue("User name matches", "One user name".equals(_base.getUserName()));
        _base.setUserName("Another user name");
        Assert.assertTrue("User name matches", "Another user name".equals(_base.getUserName()));
        _base.setUserName(null);
        Assert.assertNull("User name matches", _base.getUserName());
        _base.setUserName("One user name");
        Assert.assertTrue("User name matches", "One user name".equals(_base.getUserName()));
    }
    
    @Test
    public void testParent() {
        MyBase a = new MyBase();
        _base.setParent(null);
        Assert.assertNull("Parent matches", _base.getParent());
        _base.setParent(a);
        Assert.assertTrue("Parent matches", a == _base.getParent());
        _base.setParent(null);
        Assert.assertNull("Parent matches", _base.getParent());
    }
    
    
    private class MyBase extends AbstractBase {

        private MyBase() {
            super("IQ1");
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
        public void setState(int s) throws JmriException {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public int getState() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public String getBeanType() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public String getShortDescription() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public String getLongDescription() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Base getNewObjectBasedOnTemplate(String sys) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Base getParent() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setParent(Base parent) {
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
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean isExternal() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Lock getLock() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setLock(Lock lock) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setup() {
            throw new UnsupportedOperationException("Not supported.");
        }
        
    }
    
}
