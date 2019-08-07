package jmri.jmrit.logixng.implementation.configurexml;

import jmri.ConfigureManager;
import jmri.InstanceManager;
import jmri.jmrit.logixng.LogixNG;
import jmri.jmrit.logixng.LogixNG_Manager;
import jmri.jmrit.logixng.implementation.DefaultLogixNGManager;
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
public class DefaultLogixNGManagerXmlTest {

    @Test
    public void testCTor() {
        DefaultLogixNGManagerXml b = new DefaultLogixNGManagerXml();
        Assert.assertNotNull("exists", b);
    }

    @Test
    public void testLoad() {
        DefaultLogixNGManagerXml b = new DefaultLogixNGManagerXml();
        Assert.assertNotNull("exists", b);
        
        // Test the method load(Element element, Object o)
        b.load((Element)null, (Object)null);
        JUnitAppender.assertErrorMessage("Invalid method called");
        
        // Test the method load(Element element, Object o)
        b.load((Element)null, (Object)null);
        JUnitAppender.assertErrorMessage("Invalid method called");
        
        // Test loading a logixng without system name
        Element e = new Element("logixngs");
        Element e2 = new Element("logixng");
        e.addContent(e2);
        b.loadLogixNGs(e);
        JUnitAppender.assertWarnMessage("unexpected null in systemName [Element: <logixng/>]");
        
        
        // Test load LogixNG without attribute "enable"
        e = new Element("logixngs");
        e2 = new Element("logixng");
        e2.addContent(new Element("systemName").addContent("IQ1001"));
        e.addContent(e2);
        b.loadLogixNGs(e);
        
        // Test load LogixNG with bad conditionalng (no systemName in the conditionalNG)
        e = new Element("logixngs");
        e2 = new Element("logixng");
        e2.addContent(new Element("systemName").addContent("IQ1002"));
        Element eConditional = new Element("conditionalng");
        e2.addContent(eConditional);
        e.addContent(e2);
        b.loadLogixNGs(e);
        JUnitAppender.assertWarnMessage("unexpected null in systemName [Element: <conditionalng/>]");
        JUnitAppender.assertErrorMessage("exception thrown");
        
        // Test loading a LogixNG that already exists
        e = new Element("logixngs");
        e2 = new Element("logixng");
        String systemName = "IQ1001";
        Assert.assertNotNull("bean exists",
                InstanceManager.getDefault(LogixNG_Manager.class).getBeanBySystemName(systemName));
        e2.addContent(new Element("systemName").addContent(systemName));
        e.addContent(e2);
        b.loadLogixNGs(e);
        
        // Test load LogixNG with attribute "enable" as empty string
        e = new Element("logixngs");
        e2 = new Element("logixng");
        e2.addContent(new Element("systemName").addContent("IQ1003"));
        e2.setAttribute("enabled", "");
        e.addContent(e2);
        b.loadLogixNGs(e);
        LogixNG logixNG = InstanceManager.getDefault(LogixNG_Manager.class).getBeanBySystemName("IQ1003");
        Assert.assertNotNull("bean is not null", logixNG);
        Assert.assertFalse("bean is not enabled", logixNG.isEnabled());
        
        // Test load LogixNG with attribute "enable" as invalid value
        e = new Element("logixngs");
        e2 = new Element("logixng");
        e2.addContent(new Element("systemName").addContent("IQ1004"));
        e2.setAttribute("enabled", "invalid value");
        e.addContent(e2);
        b.loadLogixNGs(e);
        logixNG = InstanceManager.getDefault(LogixNG_Manager.class).getBeanBySystemName("IQ1004");
        Assert.assertNotNull("bean is not null", logixNG);
        Assert.assertFalse("bean is not enabled", logixNG.isEnabled());
        
        // Test load LogixNG with attribute "enable" as yes
        e = new Element("logixngs");
        e2 = new Element("logixng");
        e2.addContent(new Element("systemName").addContent("IQ1005"));
        e2.setAttribute("enabled", "yes");
        e.addContent(e2);
        b.loadLogixNGs(e);
        logixNG = InstanceManager.getDefault(LogixNG_Manager.class).getBeanBySystemName("IQ1005");
        Assert.assertNotNull("bean is not null", logixNG);
        Assert.assertTrue("bean is enabled", logixNG.isEnabled());
        
/*        
        // Test loading the same class twice, in order to check field "xmlClasses"
        e = new Element("logixngs");
        e2 = new Element("existing_class");
        e2.setAttribute("class", "jmri.jmrit.logixng.analog.actions.configurexml.AnalogActionMemoryXml");
        e.addContent(e2);
        e2.addContent(new Element("systemName").addContent("IQAA1"));
        b.loadLogixNGs(e);
        
        e = new Element("logixngs");
        e2 = new Element("existing_class");
        e2.setAttribute("class", "jmri.jmrit.logixng.analog.actions.configurexml.AnalogActionMemoryXml");
        e.addContent(e2);
        e2.addContent(new Element("systemName").addContent("IQAA2"));
        b.loadLogixNGs(e);
/*        
        // Test trying to load a class with private constructor
        e = new Element("logixngs");
        e2 = new Element("existing_class");
        e2.setAttribute("class", "jmri.jmrit.logixng.analog.implementation.configurexml.DefaultAnalogActionManagerXmlTest$PrivateConstructorXml");
        e.addContent(e2);
        b.loadLogixNGs(e);
        JUnitAppender.assertErrorMessage("cannot create constructor");
        
        // Test trying to load a class which throws an exception
        e = new Element("logixngs");
        e2 = new Element("existing_class");
        e2.setAttribute("class", "jmri.jmrit.logixng.analog.implementation.configurexml.DefaultAnalogActionManagerXmlTest$ThrowExceptionXml");
        e.addContent(e2);
        b.loadLogixNGs(e);
        JUnitAppender.assertErrorMessage("cannot create constructor");
*/
    }

    @Test
    public void testStore() {
        DefaultLogixNGManagerXml b = new DefaultLogixNGManagerXml();
        Assert.assertNotNull("exists", b);
        // Calling store() with null is OK.
        b.store((Object)null);
    }

    @Test
    public void testReplaceActionManagerWithoutConfigManager() {
        
        // if old manager exists, remove it from configuration process
        if (InstanceManager.getNullableDefault(jmri.jmrit.logixng.AnalogActionManager.class) != null) {
            ConfigureManager cmOD = InstanceManager.getNullableDefault(jmri.ConfigureManager.class);
            if (cmOD != null) {
                cmOD.deregister(InstanceManager.getDefault(jmri.jmrit.logixng.AnalogActionManager.class));
            }

        }

        // register new one with InstanceManager
        MyManager pManager = new MyManager();
        InstanceManager.store(pManager, LogixNG_Manager.class);
        // register new one for configuration
        ConfigureManager cmOD = InstanceManager.getNullableDefault(jmri.ConfigureManager.class);
        if (cmOD != null) {
            cmOD.registerConfig(pManager, jmri.Manager.LOGIXNGS);
        }
        
        Assert.assertTrue("manager is a MyManager",
                InstanceManager.getDefault(LogixNG_Manager.class)
                        instanceof MyManager);
        
        // Test replacing the manager
        DefaultLogixNGManagerXml b = new DefaultLogixNGManagerXml();
        b.replaceLogixNGManager();
        
        Assert.assertFalse("manager is not a MyManager",
                InstanceManager.getDefault(LogixNG_Manager.class)
                        instanceof MyManager);
        
        // Test replace the manager when where is no manager registered yet
        InstanceManager.deregister(
                InstanceManager.getDefault(LogixNG_Manager.class),
                LogixNG_Manager.class);
        
        Assert.assertNotNull("manager is not null",
                InstanceManager.getDefault(LogixNG_Manager.class));
    }
    
    @Test
    public void testReplaceActionManagerWithConfigManager() {
        
        JUnitUtil.initConfigureManager();
        
        // if old manager exists, remove it from configuration process
        if (InstanceManager.getNullableDefault(jmri.jmrit.logixng.AnalogActionManager.class) != null) {
            ConfigureManager cmOD = InstanceManager.getNullableDefault(jmri.ConfigureManager.class);
            if (cmOD != null) {
                cmOD.deregister(InstanceManager.getDefault(jmri.jmrit.logixng.AnalogActionManager.class));
            }

        }

        // register new one with InstanceManager
        MyManager pManager = new MyManager();
        InstanceManager.store(pManager, LogixNG_Manager.class);
        // register new one for configuration
        ConfigureManager cmOD = InstanceManager.getNullableDefault(jmri.ConfigureManager.class);
        if (cmOD != null) {
            cmOD.registerConfig(pManager, jmri.Manager.LOGIXNGS);
        }
        
        Assert.assertTrue("manager is a MyManager",
                InstanceManager.getDefault(LogixNG_Manager.class)
                        instanceof MyManager);
        
        // Test replacing the manager
        DefaultLogixNGManagerXml b = new DefaultLogixNGManagerXml();
        b.replaceLogixNGManager();
        
        Assert.assertFalse("manager is not a MyManager",
                InstanceManager.getDefault(LogixNG_Manager.class)
                        instanceof MyManager);
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
    
/*    
    private class MyLogixNG extends jmri.jmrit.logixng.implementation.DefaultLogixNG {
        
        MyLogixNG() {
            super("IQ9999");
        }
        
    }
    
/*    
    // This class is loaded by reflection. The class cannot be private since
    // Spotbugs will in that case flag it as "is never used locally"
    class PrivateConstructorXml extends DefaultLogixNGXml {
        private PrivateConstructorXml() {
        }
    }
    
    // This class is loaded by reflection. The class cannot be private since
    // Spotbugs will in that case flag it as "is never used locally"
    class ThrowExceptionXml extends DefaultLogixNGXml {
        @Override
        public boolean load(Element shared, Element perNode) throws JmriConfigureXmlException {
            throw new JmriConfigureXmlException();
        }
    }
*/    
    class MyManager extends DefaultLogixNGManager {
        MyManager() {
            super(InstanceManager.getDefault(InternalSystemConnectionMemo.class));
        }
    }
    
}
