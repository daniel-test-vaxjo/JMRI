package jmri.jmrit.logixng.string.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.util.concurrent.atomic.AtomicBoolean;
import jmri.InstanceManager;
import jmri.Memory;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import jmri.MemoryManager;
import jmri.NamedBeanHandle;
import jmri.NamedBeanHandleManager;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.SocketAlreadyConnectedException;

/**
 * Test SetStringIO
 * 
 * @author Daniel Bergqvist 2018
 */
public class StringActionMemoryTest extends AbstractStringActionTestBase {

    protected Memory _memory;
    
    @Test
    public void testCtor() {
        Assert.assertTrue("object exists", _base != null);
        
        StringActionMemory action2;
        Assert.assertNotNull("memory is not null", _memory);
        _memory.setValue(10.2);
        
        action2 = new StringActionMemory("IQSA11");
        Assert.assertNotNull("object exists", action2);
        Assert.assertTrue("Username matches", null == action2.getUserName());
        Assert.assertTrue("String matches", "Set memory none".equals(action2.getLongDescription()));
        
        action2 = new StringActionMemory("IQSA11", "My memory");
        Assert.assertNotNull("object exists", action2);
        Assert.assertTrue("Username matches", "My memory".equals(action2.getUserName()));
        Assert.assertTrue("String matches", "Set memory none".equals(action2.getLongDescription()));
        
        action2 = new StringActionMemory("IQSA11");
        action2.setMemory(_memory);
        Assert.assertNotNull("object exists", action2);
        Assert.assertTrue("Username matches", null == action2.getUserName());
        Assert.assertTrue("String matches", "Set memory IM1".equals(action2.getLongDescription()));
        
        action2 = new StringActionMemory("IQSA11", "My memory");
        action2.setMemory(_memory);
        Assert.assertNotNull("object exists", action2);
        Assert.assertTrue("Username matches", "My memory".equals(action2.getUserName()));
        Assert.assertTrue("String matches", "Set memory IM1".equals(action2.getLongDescription()));
        
        // Test template
        action2 = (StringActionMemory)_base.getNewObjectBasedOnTemplate("IQSA12");
        Assert.assertNotNull("object exists", action2);
        Assert.assertNull("Username is null", action2.getUserName());
        Assert.assertTrue("String matches", "Set memory IM1".equals(action2.getLongDescription()));
        
        boolean thrown = false;
        try {
            // Illegal system name
            new StringActionMemory("IQA55:12:XY11");
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        Assert.assertTrue("Expected exception thrown", thrown);
        
        thrown = false;
        try {
            // Illegal system name
            new StringActionMemory("IQA55:12:XY11", "A name");
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        Assert.assertTrue("Expected exception thrown", thrown);
    }
    
    @Test
    public void testAction() throws SocketAlreadyConnectedException, SocketAlreadyConnectedException {
        StringActionMemory _action = (StringActionMemory)_base;
        _action.setValue("");
        Assert.assertEquals("Memory has correct value", "", _memory.getValue());
        _action.setValue("Test");
        Assert.assertEquals("Memory has correct value", "Test", _memory.getValue());
        _action.setMemory((Memory)null);
        _action.setValue("Other test");
        Assert.assertEquals("Memory has correct value", "Test", _memory.getValue());
    }
    
    @Test
    public void testMemory() {
        StringActionMemory _action = (StringActionMemory)_base;
        _action.setMemory((Memory)null);
        Assert.assertNull("Memory is null", _action.getMemory());
        ((StringActionMemory)_base).setMemory(_memory);
        Assert.assertTrue("Memory matches", _memory == _action.getMemory().getBean());
        
        _action.setMemory((NamedBeanHandle<Memory>)null);
        Assert.assertNull("Memory is null", _action.getMemory());
        Memory otherMemory = InstanceManager.getDefault(MemoryManager.class).provide("IM99");
        Assert.assertNotNull("memory is not null", otherMemory);
        NamedBeanHandle<Memory> memoryHandle = InstanceManager.getDefault(NamedBeanHandleManager.class)
                .getNamedBeanHandle(otherMemory.getDisplayName(), otherMemory);
        ((StringActionMemory)_base).setMemory(memoryHandle);
        Assert.assertTrue("Memory matches", memoryHandle == _action.getMemory());
        Assert.assertTrue("Memory matches", otherMemory == _action.getMemory().getBean());
        
        _action.setMemory((String)null);
        Assert.assertNull("Memory is null", _action.getMemory());
        _action.setMemory(memoryHandle.getName());
        Assert.assertTrue("Memory matches", memoryHandle == _action.getMemory());
    }
    
    @Test
    public void testVetoableChange() throws PropertyVetoException {
        // Get some other memory for later use
        Memory otherMemory = InstanceManager.getDefault(MemoryManager.class).provide("IM99");
        Assert.assertNotNull("Memory is not null", otherMemory);
        Assert.assertNotEquals("Memory is not equal", _memory, otherMemory);
        
        // Get the expression and set the memory
        StringActionMemory _action = (StringActionMemory)_base;
        _action.setMemory(_memory);
        Assert.assertEquals("Memory matches", _memory, _action.getMemory().getBean());
        
        // Test vetoableChange() for a string
        _action.vetoableChange(new PropertyChangeEvent(this, "CanDelete", "test", null));
        Assert.assertEquals("Memory matches", _memory, _action.getMemory().getBean());
        _action.vetoableChange(new PropertyChangeEvent(this, "DoDelete", "test", null));
        Assert.assertEquals("Memory matches", _memory, _action.getMemory().getBean());
        
        // Test vetoableChange() for another memory
        _action.vetoableChange(new PropertyChangeEvent(this, "CanDelete", otherMemory, null));
        Assert.assertEquals("Memory matches", _memory, _action.getMemory().getBean());
        _action.vetoableChange(new PropertyChangeEvent(this, "DoDelete", otherMemory, null));
        Assert.assertEquals("Memory matches", _memory, _action.getMemory().getBean());
        
        // Test vetoableChange() for its own memory
        boolean thrown = false;
        try {
            _action.vetoableChange(new PropertyChangeEvent(this, "CanDelete", _memory, null));
        } catch (PropertyVetoException ex) {
            thrown = true;
        }
        Assert.assertTrue("Expected exception thrown", thrown);
        
        Assert.assertEquals("Memory matches", _memory, _action.getMemory().getBean());
        _action.vetoableChange(new PropertyChangeEvent(this, "DoDelete", _memory, null));
        Assert.assertNull("Memory is null", _action.getMemory());
    }
    
    @Test
    public void testCategory() {
        Assert.assertTrue("Category matches", Category.ITEM == _base.getCategory());
    }
    
    @Test
    public void testIsExternal() {
        Assert.assertTrue("is external", _base.isExternal());
    }
    
    @Test
    public void testShortDescription() {
        Assert.assertTrue("String matches", "Set memory IM1".equals(_base.getShortDescription()));
    }
    
    @Test
    public void testLongDescription() {
        Assert.assertTrue("String matches", "Set memory IM1".equals(_base.getLongDescription()));
    }
    
    @Test
    public void testChild() {
        Assert.assertTrue("Num children is zero", 0 == _base.getChildCount());
        AtomicBoolean hasThrown = new AtomicBoolean(false);
        try {
            _base.getChild(0);
        } catch (UnsupportedOperationException ex) {
            hasThrown.set(true);
            Assert.assertTrue("Error message is correct", "Not supported.".equals(ex.getMessage()));
        }
        Assert.assertTrue("Exception is thrown", hasThrown.get());
    }
    
    // The minimal setup for log4J
    @Before
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.resetInstanceManager();
        JUnitUtil.initInternalSensorManager();
        JUnitUtil.initInternalTurnoutManager();
        JUnitUtil.initMemoryManager();
        _memory = InstanceManager.getDefault(MemoryManager.class).provide("IM1");
        _base = new StringActionMemory("IQSA321", "StringIO_Memory");
        ((StringActionMemory)_base).setMemory(_memory);
    }

    @After
    public void tearDown() {
        _base.dispose();
        JUnitUtil.tearDown();
    }
    
}
