package jmri.jmrit.logixng.digital.implementation.configurexml;

import jmri.ConfigureManager;
import jmri.InstanceManager;
import jmri.configurexml.JmriConfigureXmlException;
import jmri.jmrit.logixng.DigitalExpressionManager;
import jmri.jmrit.logixng.digital.expressions.ExpressionTurnout;
import jmri.jmrit.logixng.digital.expressions.configurexml.ExpressionTurnoutXml;
import jmri.jmrit.logixng.digital.implementation.DefaultDigitalExpressionManager;
import jmri.jmrix.internal.InternalSystemConnectionMemo;
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
public class DefaultDigitalExpressionManagerXmlTest {

    @Test
    public void testCTor() {
        DefaultDigitalExpressionManagerXml b = new DefaultDigitalExpressionManagerXml();
        Assert.assertNotNull("exists", b);
    }

    @Test
    public void testLoad() {
        DefaultDigitalExpressionManagerXml b = new DefaultDigitalExpressionManagerXml();
        
        // Test the method load(Element element, Object o)
        b.load((Element)null, (Object)null);
        JUnitAppender.assertErrorMessage("Invalid method called");
        
        Element e = new Element("logixngAnalogExpressions");
        Element e2 = new Element("missing_class");
        e2.setAttribute("class", "jmri.jmrit.logixng.this.class.does.not.exist.TestClassXml");
        e.addContent(e2);
        b.loadExpressions(e);
        JUnitAppender.assertErrorMessage("cannot load class jmri.jmrit.logixng.this.class.does.not.exist.TestClassXml");
        
        // Test loading the same class twice, in order to check field "xmlClasses"
        e = new Element("logixngAnalogExpressions");
        e2 = new Element("existing_class");
        e2.setAttribute("class", "jmri.jmrit.logixng.digital.expressions.configurexml.ExpressionTurnoutXml");
        e.addContent(e2);
        e2.addContent(new Element("systemName").addContent("IQDE1"));
        b.loadExpressions(e);
        
        e = new Element("logixngAnalogExpressions");
        e2 = new Element("existing_class");
        e2.setAttribute("class", "jmri.jmrit.logixng.digital.expressions.configurexml.ExpressionTurnoutXml");
        e.addContent(e2);
        e2.addContent(new Element("systemName").addContent("IQDE2"));
        b.loadExpressions(e);
        
        // Test trying to load a class with private constructor
        e = new Element("logixngAnalogExpressions");
        e2 = new Element("existing_class");
        e2.setAttribute("class", "jmri.jmrit.logixng.digital.implementation.configurexml.DefaultDigitalExpressionManagerXmlTest$PrivateConstructorXml");
        e.addContent(e2);
        b.loadExpressions(e);
        JUnitAppender.assertErrorMessage("cannot create constructor");
        
        // Test trying to load a class which throws an exception
        e = new Element("logixngAnalogExpressions");
        e2 = new Element("existing_class");
        e2.setAttribute("class", "jmri.jmrit.logixng.digital.implementation.configurexml.DefaultDigitalExpressionManagerXmlTest$ThrowExceptionXml");
        e.addContent(e2);
        b.loadExpressions(e);
        JUnitAppender.assertErrorMessage("cannot create constructor");
        
//        System.out.format("Class name: %s%n", PrivateConstructorXml.class.getName());
    }

    @Test
    public void testStore() {
        DefaultDigitalExpressionManagerXml b = new DefaultDigitalExpressionManagerXml();
        
        // If parameter is null, nothing should happen
        b.store(null);
        
        // Test store a named bean that has no configurexml class
        DigitalExpressionManager manager = InstanceManager.getDefault(DigitalExpressionManager.class);
        manager.registerExpression(new DefaultDigitalExpressionManagerXmlTest.MyDigitalExpression());
        b.store(manager);
        JUnitAppender.assertErrorMessage("Cannot load configuration adapter for jmri.jmrit.logixng.digital.implementation.configurexml.DefaultDigitalExpressionManagerXmlTest$MyDigitalExpression");
        JUnitAppender.assertErrorMessage("Cannot store configuration for jmri.jmrit.logixng.digital.implementation.configurexml.DefaultDigitalExpressionManagerXmlTest$MyDigitalExpression");
    }
    
    @Test
    public void testReplaceActionManagerWithoutConfigManager() {
        
        // if old manager exists, remove it from configuration process
        if (InstanceManager.getNullableDefault(jmri.jmrit.logixng.DigitalExpressionManager.class) != null) {
            ConfigureManager cmOD = InstanceManager.getNullableDefault(jmri.ConfigureManager.class);
            if (cmOD != null) {
                cmOD.deregister(InstanceManager.getDefault(jmri.jmrit.logixng.DigitalExpressionManager.class));
            }

        }

        // register new one with InstanceManager
        DefaultDigitalExpressionManagerXmlTest.MyManager pManager = new DefaultDigitalExpressionManagerXmlTest.MyManager();
        InstanceManager.store(pManager, DigitalExpressionManager.class);
        // register new one for configuration
        ConfigureManager cmOD = InstanceManager.getNullableDefault(jmri.ConfigureManager.class);
        if (cmOD != null) {
            cmOD.registerConfig(pManager, jmri.Manager.ANALOG_ACTIONS);
        }
        
        Assert.assertTrue("manager is a MyManager",
                InstanceManager.getDefault(DigitalExpressionManager.class)
                        instanceof DefaultDigitalExpressionManagerXmlTest.MyManager);
        
        // Test replacing the manager
        DefaultDigitalExpressionManagerXml b = new DefaultDigitalExpressionManagerXml();
        b.replaceExpressionManager();
        
        Assert.assertFalse("manager is not a MyManager",
                InstanceManager.getDefault(DigitalExpressionManager.class)
                        instanceof DefaultDigitalExpressionManagerXmlTest.MyManager);
    }
    
    @Test
    public void testReplaceActionManagerWithConfigManager() {
        
        JUnitUtil.initConfigureManager();
        
        // if old manager exists, remove it from configuration process
        if (InstanceManager.getNullableDefault(jmri.jmrit.logixng.DigitalExpressionManager.class) != null) {
            ConfigureManager cmOD = InstanceManager.getNullableDefault(jmri.ConfigureManager.class);
            if (cmOD != null) {
                cmOD.deregister(InstanceManager.getDefault(jmri.jmrit.logixng.DigitalExpressionManager.class));
            }

        }

        // register new one with InstanceManager
        DefaultDigitalExpressionManagerXmlTest.MyManager pManager = new DefaultDigitalExpressionManagerXmlTest.MyManager();
        InstanceManager.store(pManager, DigitalExpressionManager.class);
        // register new one for configuration
        ConfigureManager cmOD = InstanceManager.getNullableDefault(jmri.ConfigureManager.class);
        if (cmOD != null) {
            cmOD.registerConfig(pManager, jmri.Manager.ANALOG_ACTIONS);
        }
        
        Assert.assertTrue("manager is a MyManager",
                InstanceManager.getDefault(DigitalExpressionManager.class)
                        instanceof DefaultDigitalExpressionManagerXmlTest.MyManager);
        
        // Test replacing the manager
        DefaultDigitalExpressionManagerXml b = new DefaultDigitalExpressionManagerXml();
        b.replaceExpressionManager();
        
        Assert.assertFalse("manager is not a MyManager",
                InstanceManager.getDefault(DigitalExpressionManager.class)
                        instanceof DefaultDigitalExpressionManagerXmlTest.MyManager);
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
    
    
    
    private class MyDigitalExpression extends ExpressionTurnout {
        
        MyDigitalExpression() {
            super("IQDE9999");
        }
        
    }
    
    
    // This class is loaded by reflection. The class cannot be private since
    // Spotbugs will in that case flag it as "is never used locally"
    class PrivateConstructorXml extends ExpressionTurnoutXml {
        private PrivateConstructorXml() {
        }
    }
    
    // This class is loaded by reflection. The class cannot be private since
    // Spotbugs will in that case flag it as "is never used locally"
    class ThrowExceptionXml extends ExpressionTurnoutXml {
        @Override
        public boolean load(Element shared, Element perNode) throws JmriConfigureXmlException {
            throw new JmriConfigureXmlException();
        }
    }
    
    
    class MyManager extends DefaultDigitalExpressionManager {
        MyManager() {
            super(InstanceManager.getDefault(InternalSystemConnectionMemo.class));
        }
    }
    
}
