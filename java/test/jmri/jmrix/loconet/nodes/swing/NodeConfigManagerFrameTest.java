package jmri.jmrix.loconet.nodes.swing;

import java.awt.GraphicsEnvironment;
import jmri.jmrix.loconet.nodes.*;
import jmri.jmrix.loconet.LnTrafficController;
import jmri.jmrix.loconet.LocoNetSystemConnectionMemo;
import jmri.util.JUnitUtil;
import jmri.util.ThreadingUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/**
 * Test NodeConfigManagerFrame
 */
public class NodeConfigManagerFrameTest {

    private LnTrafficController lnis;
    private LocoNetSystemConnectionMemo memo;
    
    @Test
    public void testCTor() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        
        ThreadingUtil.runOnGUI(() -> {
            NodeConfigManagerFrame b = new NodeConfigManagerFrame(memo);
            Assert.assertNotNull("exists", b);
        });
    }
    
    // The minimal setup for log4J
    @Before
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.resetInstanceManager();
        
        // The class under test uses one LocoNet connection it pulls from the InstanceManager.
        memo = new jmri.jmrix.loconet.LocoNetSystemConnectionMemo();
        lnis = new jmri.jmrix.loconet.LocoNetInterfaceScaffold(memo);
        memo.setLnTrafficController(lnis);
        memo.configureCommandStation(jmri.jmrix.loconet.LnCommandStationType.COMMAND_STATION_DCS100, false, false, false);
        memo.configureManagers();
        jmri.InstanceManager.store(memo, jmri.jmrix.loconet.LocoNetSystemConnectionMemo.class);
        
        JUnitUtil.initInternalSensorManager();
        JUnitUtil.initInternalTurnoutManager();
        JUnitUtil.initLogixManager();
    }

    @After
    public void tearDown() {
        JUnitUtil.deregisterBlockManagerShutdownTask();
        JUnitUtil.tearDown();
    }

}