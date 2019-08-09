package jmri.jmrit.logixng.digital.actions.configurexml;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import jmri.configurexml.JmriConfigureXmlException;
import jmri.managers.configurexml.AbstractNamedBeanManagerConfigXML;
import jmri.util.JUnitAppender;
import jmri.util.JUnitUtil;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test ActionTurnoutXml
 * 
 * @author Daniel Bergqvist 2019
 */
public class DigitalActionsXmlTest {

    @Test
    public void testDigitalActions() throws JmriConfigureXmlException {
        AbstractNamedBeanManagerConfigXML b;
        
        b = new ActionLightXml();
        Assert.assertNotNull("exists", b);
        b.load((Element) null, (Object) null);
        JUnitAppender.assertMessage("Invalid method called");
        
        b = new ActionSensorXml();
        Assert.assertNotNull("exists", b);
        b.load((Element) null, (Object) null);
        JUnitAppender.assertMessage("Invalid method called");
        
        b = new ActionTurnoutXml();
        Assert.assertNotNull("exists", b);
        b.load((Element) null, (Object) null);
        JUnitAppender.assertMessage("Invalid method called");
        
        b = new DoAnalogActionXml();
        Assert.assertNotNull("exists", b);
        b.load((Element) null, (Object) null);
        JUnitAppender.assertMessage("Invalid method called");
        
        b = new DoStringActionXml();
        Assert.assertNotNull("exists", b);
        b.load((Element) null, (Object) null);
        JUnitAppender.assertMessage("Invalid method called");
        
        b = new HoldAnythingXml();
        Assert.assertNotNull("exists", b);
        b.load((Element) null, (Object) null);
        JUnitAppender.assertMessage("Invalid method called");
        
        b = new IfThenElseXml();
        Assert.assertNotNull("exists", b);
        b.load((Element) null, (Object) null);
        JUnitAppender.assertMessage("Invalid method called");
        
        b = new ManyXml();
        Assert.assertNotNull("exists", b);
        b.load((Element) null, (Object) null);
        JUnitAppender.assertMessage("Invalid method called");
        
        b = new ShutdownComputerXml();
        Assert.assertNotNull("exists", b);
        b.load((Element) null, (Object) null);
        JUnitAppender.assertMessage("Invalid method called");
    }
    
    @Test
    public void testShutdownComputerXml() {
        Element element = new Element("shutdown-computer");
        element.setAttribute("class", this.getClass().getName());
        element.addContent(new Element("systemName").addContent("IQDA1"));
        
        // Test invalid type. This value should be a number but test it with
        // some letters.
        element.setAttribute("seconds", "abc");
        
        ShutdownComputerXml shutdownComputerXml = new ShutdownComputerXml();
        shutdownComputerXml.load(element, null);
        JUnitAppender.assertErrorMessage("seconds attribute is not an integer");
    }
    
    // The minimal setup for log4J
    @Before
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.resetInstanceManager();
        JUnitUtil.initInternalSensorManager();
        JUnitUtil.initInternalTurnoutManager();
        JUnitUtil.initLogixNGManager();
        JUnitUtil.initAnalogActionManager();
        JUnitUtil.initAnalogExpressionManager();
        JUnitUtil.initDigitalActionManager();
        JUnitUtil.initDigitalExpressionManager();
        JUnitUtil.initStringActionManager();
        JUnitUtil.initStringExpressionManager();
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }

}
