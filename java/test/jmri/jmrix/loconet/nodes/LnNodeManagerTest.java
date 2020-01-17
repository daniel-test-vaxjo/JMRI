package jmri.jmrix.loconet.nodes;

import jmri.jmrix.loconet.LnTrafficController;
import jmri.jmrix.loconet.LocoNetSystemConnectionMemo;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test LnNodeManager.
 */
public class LnNodeManagerTest {

    private LnTrafficController lnis;
    private LocoNetSystemConnectionMemo memo;
    
    @Test
    public void testCTor() {
        LnNodeManager m = new LnNodeManager();
        Assert.assertNotNull("exists", m);
    }
    
    @Test
    public void testConstants() {
        LnNodeManager m = new LnNodeManager();
        Assert.assertEquals("PUBLIC_DOMAIN_DIY_MANAGER_ID is correct",
                13, LnNodeManager.PUBLIC_DOMAIN_DIY_MANAGER_ID);
        Assert.assertEquals("PUBLIC_DOMAIN_DIY_MANAGER is correct",
                "Public-domain and DIY", LnNodeManager.PUBLIC_DOMAIN_DIY_MANAGER);
        Assert.assertEquals("String matches",
                LnNodeManager.PUBLIC_DOMAIN_DIY_MANAGER,
                m.getManufacturer(LnNodeManager.PUBLIC_DOMAIN_DIY_MANAGER_ID));
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
        JUnitUtil.tearDown();
    }

}
