package jmri.jmrix.loconet.usb_dcs52;

import java.awt.GraphicsEnvironment;
import java.util.ResourceBundle;
import javax.swing.JFrame;
import javax.swing.JPanel;
import jmri.util.swing.JemmyUtil;
import jmri.util.JmriJFrame;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.netbeans.jemmy.operators.JFrameOperator;

/**
 * Tests for ConnectionConfig class.
 *
 * @author Paul Bender      Copyright (C) 2016
 * @author Daniel Bergqvist Copyright (C) 2020
 **/
public class ConnectionConfigTest extends jmri.jmrix.AbstractSerialConnectionConfigTestBase  {

    /**
     * Test the "Configure nodes" button.
     * This button is common to many ConnectionConfig classes which
     * inherit from different parent classes.
     */
    @Test
    public void testConfigNodes() {
        jmri.jmrix.loconet.nodes.swing.ConnectionConfigScaffold.testConfigNodes(cc);
    }
    
    @Before
    @Override
    public void setUp() {
        JUnitUtil.setUp();

        JUnitUtil.initDefaultUserMessagePreferences();
        cc = new ConnectionConfig();
    }

    @After
    @Override
    public void tearDown(){
        cc = null;
        JUnitUtil.tearDown();
    }

}
