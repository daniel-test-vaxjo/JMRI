package jmri.jmrit.logixng.digital.implementation.configurexml;

import jmri.ConfigureManager;
import jmri.InstanceManager;
import jmri.configurexml.JmriConfigureXmlException;
import jmri.jmrit.logixng.DigitalActionManager;
import jmri.jmrit.logixng.digital.actions.ActionTurnout;
import jmri.jmrit.logixng.digital.actions.configurexml.ActionTurnoutXml;
import jmri.jmrit.logixng.digital.implementation.DefaultDigitalActionManager;
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
public class DefaultDigitalActionManagerXmlTest {

    @Test
    public void testCTor() {
        DefaultDigitalActionManagerXml b = new DefaultDigitalActionManagerXml();
        Assert.assertNotNull("exists", b);
    }

    @Test
    public void testLoad() {
        DefaultDigitalActionManagerXml b = new DefaultDigitalActionManagerXml();
        
        // Test the method load(Element element, Object o)
        b.load((Element)null, (Object)null);
        JUnitAppender.assertErrorMessage("Invalid method called");
        
        Element e = new Element("logixngDigitalExpressions");
        Element e2 = new Element("missing_class");
        e2.setAttribute("class", "jmri.jmrit.logixng.this.class.does.not.exist.TestClassXml");
        e.addContent(e2);
        b.loadActions(e);
        JUnitAppender.assertErrorMessage("cannot load class jmri.jmrit.logixng.this.class.does.not.exist.TestClassXml");
        
        // Test loading the same class twice, in order to check field "xmlClasses"
        e = new Element("logixngDigitalExpressions");
        e2 = new Element("existing_class");
        e2.setAttribute("class", "jmri.jmrit.logixng.digital.actions.configurexml.ActionTurnoutXml");
        e.addContent(e2);
        e2.addContent(new Element("systemName").addContent("IQDA1"));
        b.loadActions(e);
        
        e = new Element("logixngDigitalExpressions");
        e2 = new Element("existing_class");
        e2.setAttribute("class", "jmri.jmrit.logixng.digital.actions.configurexml.ActionTurnoutXml");
        e.addContent(e2);
        e2.addContent(new Element("systemName").addContent("IQDA2"));
        b.loadActions(e);
        
        // Test trying to load a class with private constructor
        e = new Element("logixngDigitalExpressions");
        e2 = new Element("existing_class");
        e2.setAttribute("class", "jmri.jmrit.logixng.digital.implementation.configurexml.DefaultDigitalActionManagerXmlTest$PrivateConstructorXml");
        e.addContent(e2);
        b.loadActions(e);
        JUnitAppender.assertErrorMessage("cannot create constructor");
        
        // Test trying to load a class which throws an exception
        e = new Element("logixngDigitalExpressions");
        e2 = new Element("existing_class");
        e2.setAttribute("class", "jmri.jmrit.logixng.digital.implementation.configurexml.DefaultDigitalActionManagerXmlTest$ThrowExceptionXml");
        e.addContent(e2);
        b.loadActions(e);
        JUnitAppender.assertErrorMessage("cannot create constructor");
        
//        System.out.format("Class name: %s%n", PrivateConstructorXml.class.getName());
    }

    @Test
    public void testStore() {
        DefaultDigitalActionManagerXml b = new DefaultDigitalActionManagerXml();
        
        // If parameter is null, nothing should happen
        b.store(null);
        
        // Test store a named bean that has no configurexml class
        DigitalActionManager manager = InstanceManager.getDefault(DigitalActionManager.class);
        manager.registerAction(new DefaultDigitalActionManagerXmlTest.MyDigitalAction());
        b.store(manager);
        JUnitAppender.assertErrorMessage("Cannot load configuration adapter for jmri.jmrit.logixng.digital.implementation.configurexml.DefaultDigitalActionManagerXmlTest$MyDigitalAction");
        JUnitAppender.assertErrorMessage("Cannot store configuration for jmri.jmrit.logixng.digital.implementation.configurexml.DefaultDigitalActionManagerXmlTest$MyDigitalAction");
    }
    
    @Test
    public void testReplaceActionManagerWithoutConfigManager() {
        
        // if old manager exists, remove it from configuration process
        if (InstanceManager.getNullableDefault(jmri.jmrit.logixng.DigitalActionManager.class) != null) {
            ConfigureManager cmOD = InstanceManager.getNullableDefault(jmri.ConfigureManager.class);
            if (cmOD != null) {
                cmOD.deregister(InstanceManager.getDefault(jmri.jmrit.logixng.DigitalActionManager.class));
            }

        }

        // register new one with InstanceManager
        DefaultDigitalActionManagerXmlTest.MyManager pManager = new DefaultDigitalActionManagerXmlTest.MyManager();
        InstanceManager.store(pManager, DigitalActionManager.class);
        // register new one for configuration
        ConfigureManager cmOD = InstanceManager.getNullableDefault(jmri.ConfigureManager.class);
        if (cmOD != null) {
            cmOD.registerConfig(pManager, jmri.Manager.ANALOG_ACTIONS);
        }
        
        Assert.assertTrue("manager is a MyManager",
                InstanceManager.getDefault(DigitalActionManager.class)
                        instanceof DefaultDigitalActionManagerXmlTest.MyManager);
        
        // Test replacing the manager
        DefaultDigitalActionManagerXml b = new DefaultDigitalActionManagerXml();
        b.replaceActionManager();
        
        Assert.assertFalse("manager is not a MyManager",
                InstanceManager.getDefault(DigitalActionManager.class)
                        instanceof DefaultDigitalActionManagerXmlTest.MyManager);
    }
    
    @Test
    public void testReplaceActionManagerWithConfigManager() {
        
        JUnitUtil.initConfigureManager();
        
        // if old manager exists, remove it from configuration process
        if (InstanceManager.getNullableDefault(jmri.jmrit.logixng.DigitalActionManager.class) != null) {
            ConfigureManager cmOD = InstanceManager.getNullableDefault(jmri.ConfigureManager.class);
            if (cmOD != null) {
                cmOD.deregister(InstanceManager.getDefault(jmri.jmrit.logixng.DigitalActionManager.class));
            }

        }

        // register new one with InstanceManager
        DefaultDigitalActionManagerXmlTest.MyManager pManager = new DefaultDigitalActionManagerXmlTest.MyManager();
        InstanceManager.store(pManager, DigitalActionManager.class);
        // register new one for configuration
        ConfigureManager cmOD = InstanceManager.getNullableDefault(jmri.ConfigureManager.class);
        if (cmOD != null) {
            cmOD.registerConfig(pManager, jmri.Manager.ANALOG_ACTIONS);
        }
        
        Assert.assertTrue("manager is a MyManager",
                InstanceManager.getDefault(DigitalActionManager.class)
                        instanceof DefaultDigitalActionManagerXmlTest.MyManager);
        
        // Test replacing the manager
        DefaultDigitalActionManagerXml b = new DefaultDigitalActionManagerXml();
        b.replaceActionManager();
        
        Assert.assertFalse("manager is not a MyManager",
                InstanceManager.getDefault(DigitalActionManager.class)
                        instanceof DefaultDigitalActionManagerXmlTest.MyManager);
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
    
    
    
    private class MyDigitalAction extends ActionTurnout {
        
        MyDigitalAction() {
            super("IQDA9999");
        }
        
    }
    
    
    // This class is loaded by reflection. The class cannot be private since
    // Spotbugs will in that case flag it as "is never used locally"
    class PrivateConstructorXml extends ActionTurnoutXml {
        private PrivateConstructorXml() {
        }
    }
    
    // This class is loaded by reflection. The class cannot be private since
    // Spotbugs will in that case flag it as "is never used locally"
    class ThrowExceptionXml extends ActionTurnoutXml {
        @Override
        public boolean load(Element shared, Element perNode) throws JmriConfigureXmlException {
            throw new JmriConfigureXmlException();
        }
    }
    
    
    class MyManager extends DefaultDigitalActionManager {
        
    }
    
}
