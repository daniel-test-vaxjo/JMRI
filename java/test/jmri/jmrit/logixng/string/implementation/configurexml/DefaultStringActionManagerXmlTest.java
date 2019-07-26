package jmri.jmrit.logixng.string.implementation.configurexml;

import jmri.ConfigureManager;
import jmri.InstanceManager;
import jmri.configurexml.JmriConfigureXmlException;
import jmri.jmrit.logixng.StringActionManager;
import jmri.jmrit.logixng.string.actions.StringActionMemory;
import jmri.jmrit.logixng.string.actions.configurexml.StringActionMemoryXml;
import jmri.jmrit.logixng.string.implementation.DefaultStringActionManager;
import jmri.jmrit.logixng.string.implementation.configurexml.DefaultStringActionManagerXml;
import jmri.jmrit.logixng.string.implementation.configurexml.DefaultStringActionManagerXmlTest;
import jmri.jmrit.logixng.string.implementation.configurexml.DefaultStringActionManagerXml;
import jmri.util.JUnitAppender;
import jmri.util.JUnitUtil;
import org.jdom2.Element;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Daniel Bergqvist Copyright (C) 2018
 */
public class DefaultStringActionManagerXmlTest {

    @Test
    public void testCTor() {
        DefaultStringActionManagerXml b = new DefaultStringActionManagerXml();
        Assert.assertNotNull("exists", b);
    }

    @Test
    public void testLoad() {
        DefaultStringActionManagerXml b = new DefaultStringActionManagerXml();
        
        // Test the method load(Element element, Object o)
        b.load((Element)null, (Object)null);
        JUnitAppender.assertErrorMessage("Invalid method called");
        
        Element e = new Element("logixngStringExpressions");
        Element e2 = new Element("missing_class");
        e2.setAttribute("class", "jmri.jmrit.logixng.this.class.does.not.exist.TestClassXml");
        e.addContent(e2);
        b.loadActions(e);
        JUnitAppender.assertErrorMessage("cannot load class jmri.jmrit.logixng.this.class.does.not.exist.TestClassXml");
        
        // Test loading the same class twice, in order to check field "xmlClasses"
        e = new Element("logixngStringExpressions");
        e2 = new Element("existing_class");
        e2.setAttribute("class", "jmri.jmrit.logixng.string.actions.configurexml.StringActionMemoryXml");
        e.addContent(e2);
        e2.addContent(new Element("systemName").addContent("IQSA1"));
        b.loadActions(e);
        
        e = new Element("logixngStringExpressions");
        e2 = new Element("existing_class");
        e2.setAttribute("class", "jmri.jmrit.logixng.string.actions.configurexml.StringActionMemoryXml");
        e.addContent(e2);
        e2.addContent(new Element("systemName").addContent("IQSA2"));
        b.loadActions(e);
        
        // Test trying to load a class with private constructor
        e = new Element("logixngStringExpressions");
        e2 = new Element("existing_class");
        e2.setAttribute("class", "jmri.jmrit.logixng.string.implementation.configurexml.DefaultStringActionManagerXmlTest$PrivateConstructorXml");
        e.addContent(e2);
        b.loadActions(e);
        JUnitAppender.assertErrorMessage("cannot create constructor");
        
        // Test trying to load a class which throws an exception
        e = new Element("logixngStringExpressions");
        e2 = new Element("existing_class");
        e2.setAttribute("class", "jmri.jmrit.logixng.string.implementation.configurexml.DefaultStringActionManagerXmlTest$ThrowExceptionXml");
        e.addContent(e2);
        b.loadActions(e);
        JUnitAppender.assertErrorMessage("cannot create constructor");
        
//        System.out.format("Class name: %s%n", PrivateConstructorXml.class.getName());
    }

    @Test
    public void testStore() {
        DefaultStringActionManagerXml b = new DefaultStringActionManagerXml();
        
        // If parameter is null, nothing should happen
        b.store(null);
        
        // Test store a named bean that has no configurexml class
        StringActionManager manager = InstanceManager.getDefault(StringActionManager.class);
        manager.registerAction(new DefaultStringActionManagerXmlTest.MyStringAction());
        b.store(manager);
        JUnitAppender.assertErrorMessage("Cannot load configuration adapter for jmri.jmrit.logixng.string.implementation.configurexml.DefaultStringActionManagerXmlTest$MyStringAction");
        JUnitAppender.assertErrorMessage("Cannot store configuration for jmri.jmrit.logixng.string.implementation.configurexml.DefaultStringActionManagerXmlTest$MyStringAction");
    }
    
    @Test
    public void testReplaceActionManagerWithoutConfigManager() {
        
        // if old manager exists, remove it from configuration process
        if (InstanceManager.getNullableDefault(jmri.jmrit.logixng.StringActionManager.class) != null) {
            ConfigureManager cmOD = InstanceManager.getNullableDefault(jmri.ConfigureManager.class);
            if (cmOD != null) {
                cmOD.deregister(InstanceManager.getDefault(jmri.jmrit.logixng.StringActionManager.class));
            }

        }

        // register new one with InstanceManager
        DefaultStringActionManagerXmlTest.MyManager pManager = new DefaultStringActionManagerXmlTest.MyManager();
        InstanceManager.store(pManager, StringActionManager.class);
        // register new one for configuration
        ConfigureManager cmOD = InstanceManager.getNullableDefault(jmri.ConfigureManager.class);
        if (cmOD != null) {
            cmOD.registerConfig(pManager, jmri.Manager.ANALOG_ACTIONS);
        }
        
        Assert.assertTrue("manager is a MyManager",
                InstanceManager.getDefault(StringActionManager.class)
                        instanceof DefaultStringActionManagerXmlTest.MyManager);
        
        // Test replacing the manager
        DefaultStringActionManagerXml b = new DefaultStringActionManagerXml();
        b.replaceActionManager();
        
        Assert.assertFalse("manager is not a MyManager",
                InstanceManager.getDefault(StringActionManager.class)
                        instanceof DefaultStringActionManagerXmlTest.MyManager);
    }
    
    @Test
    public void testReplaceActionManagerWithConfigManager() {
        
        JUnitUtil.initConfigureManager();
        
        // if old manager exists, remove it from configuration process
        if (InstanceManager.getNullableDefault(jmri.jmrit.logixng.StringActionManager.class) != null) {
            ConfigureManager cmOD = InstanceManager.getNullableDefault(jmri.ConfigureManager.class);
            if (cmOD != null) {
                cmOD.deregister(InstanceManager.getDefault(jmri.jmrit.logixng.StringActionManager.class));
            }

        }

        // register new one with InstanceManager
        DefaultStringActionManagerXmlTest.MyManager pManager = new DefaultStringActionManagerXmlTest.MyManager();
        InstanceManager.store(pManager, StringActionManager.class);
        // register new one for configuration
        ConfigureManager cmOD = InstanceManager.getNullableDefault(jmri.ConfigureManager.class);
        if (cmOD != null) {
            cmOD.registerConfig(pManager, jmri.Manager.ANALOG_ACTIONS);
        }
        
        Assert.assertTrue("manager is a MyManager",
                InstanceManager.getDefault(StringActionManager.class)
                        instanceof DefaultStringActionManagerXmlTest.MyManager);
        
        // Test replacing the manager
        DefaultStringActionManagerXml b = new DefaultStringActionManagerXml();
        b.replaceActionManager();
        
        Assert.assertFalse("manager is not a MyManager",
                InstanceManager.getDefault(StringActionManager.class)
                        instanceof DefaultStringActionManagerXmlTest.MyManager);
    }
    
    // The minimal setup for log4J
    @Before
    public void setUp() {
        JUnitUtil.setUp();
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }
    
    
    
    private class MyStringAction extends StringActionMemory {
        
        MyStringAction() {
            super("IQSA9999");
        }
        
    }
    
    
    // This class is loaded by reflection. The class cannot be private since
    // Spotbugs will in that case flag it as "is never used locally"
    class PrivateConstructorXml extends StringActionMemoryXml {
        private PrivateConstructorXml() {
        }
    }
    
    // This class is loaded by reflection. The class cannot be private since
    // Spotbugs will in that case flag it as "is never used locally"
    class ThrowExceptionXml extends StringActionMemoryXml {
        @Override
        public boolean load(Element shared, Element perNode) throws JmriConfigureXmlException {
            throw new JmriConfigureXmlException();
        }
    }
    
    
    class MyManager extends DefaultStringActionManager {
        
    }
    
}
