package jmri.jmrit.logixng.analog.actions;

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
import jmri.jmrit.logixng.analog.expressions.AnalogExpressionMemory;

/**
 * Test SetAnalogIO
 * 
 * @author Daniel Bergqvist 2018
 */
public class AnalogActionMemoryTest extends AbstractAnalogActionTestBase {

    protected Memory _memory;
    
    @Test
    public void testCtor() {
        Assert.assertTrue("object exists", _base != null);
        
        AnalogActionMemory action2;
        Assert.assertNotNull("memory is not null", _memory);
        _memory.setValue(10.2);
        
        action2 = new AnalogActionMemory("IQA55:12:AA11");
        Assert.assertNotNull("object exists", action2);
        Assert.assertTrue("Username matches", null == action2.getUserName());
        Assert.assertTrue("String matches", "Set memory none".equals(action2.getLongDescription()));
        
        action2 = new AnalogActionMemory("IQA55:12:AA11", "My memory");
        Assert.assertNotNull("object exists", action2);
        Assert.assertTrue("Username matches", "My memory".equals(action2.getUserName()));
        Assert.assertTrue("String matches", "Set memory none".equals(action2.getLongDescription()));
        
        action2 = new AnalogActionMemory("IQA55:12:AA11");
        action2.setMemory(_memory);
        Assert.assertNotNull("object exists", action2);
        Assert.assertTrue("Username matches", null == action2.getUserName());
        Assert.assertTrue("String matches", "Set memory IM1".equals(action2.getLongDescription()));
        
        action2 = new AnalogActionMemory("IQA55:12:AA11", "My memory");
        action2.setMemory(_memory);
        Assert.assertNotNull("object exists", action2);
        Assert.assertTrue("Username matches", "My memory".equals(action2.getUserName()));
        Assert.assertTrue("String matches", "Set memory IM1".equals(action2.getLongDescription()));
        
        // Test template
        action2 = (AnalogActionMemory)_base.getNewObjectBasedOnTemplate("IQA55:12:AA12");
        Assert.assertNotNull("object exists", action2);
        Assert.assertNull("Username is null", action2.getUserName());
//        Assert.assertTrue("Username matches", "My memory".equals(expression2.getUserName()));
        System.out.format("AAAAA: %s%n", action2.getLongDescription());
        Assert.assertTrue("String matches", "Set memory IM1".equals(action2.getLongDescription()));
    }
    
    @Test
    public void testAction() throws SocketAlreadyConnectedException, SocketAlreadyConnectedException {
        AnalogActionMemory _action = (AnalogActionMemory)_base;
        _action.setValue(0.0d);
        Assert.assertTrue("Memory has correct value", 0.0d == (Double)_memory.getValue());
        _action.setValue(1.0d);
        Assert.assertTrue("Memory has correct value", 1.0d == (Double)_memory.getValue());
        _action.setMemory((Memory)null);
        _action.setValue(2.0d);
        Assert.assertTrue("Memory has correct value", 1.0d == (Double)_memory.getValue());
    }
    
    @Test
    public void testMemory() {
        AnalogActionMemory _action = (AnalogActionMemory)_base;
        _action.setMemory((Memory)null);
        Assert.assertNull("Memory is null", _action.getMemory());
        ((AnalogActionMemory)_base).setMemory(_memory);
        Assert.assertTrue("Memory matches", _memory == _action.getMemory().getBean());
        
        _action.setMemory((NamedBeanHandle<Memory>)null);
        Assert.assertNull("Memory is null", _action.getMemory());
        Memory otherMemory = InstanceManager.getDefault(MemoryManager.class).provide("IM99");
        Assert.assertNotNull("memory is not null", otherMemory);
        NamedBeanHandle<Memory> memoryHandle = InstanceManager.getDefault(NamedBeanHandleManager.class)
                .getNamedBeanHandle(otherMemory.getDisplayName(), otherMemory);
        ((AnalogActionMemory)_base).setMemory(memoryHandle);
        Assert.assertTrue("Memory matches", memoryHandle == _action.getMemory());
        Assert.assertTrue("Memory matches", otherMemory == _action.getMemory().getBean());
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
    public void testSetup() {
        Assert.assertNotNull("memory is not null", _memory);
        _memory.setValue(10.2);
        AnalogActionMemory action2 = new AnalogActionMemory("IQA55:12:AA321");
//        System.out.format("AAAAA: %s%n", action2.getLongDescription());
        Assert.assertTrue("String matches", "Set memory none".equals(action2.getLongDescription()));
        action2.setup();
        Assert.assertTrue("String matches", "Set memory none".equals(action2.getLongDescription()));
        action2.setMemorySystemName(_memory.getSystemName());
        action2.setup();
        Assert.assertTrue("String matches", "Set memory IM1".equals(action2.getLongDescription()));
        // Test running setup() again when it's already setup
        action2.setup();
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
        _base = new AnalogActionMemory("IQA55:12:AA321", "AnalogIO_Memory");
        ((AnalogActionMemory)_base).setMemory(_memory);
    }

    @After
    public void tearDown() {
        _base.dispose();
        JUnitUtil.tearDown();
    }
    
}
