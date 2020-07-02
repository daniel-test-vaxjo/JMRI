package jmri.jmrix.ieee802154.xbee;

import org.junit.Assert;
import org.junit.jupiter.api.*;

/**
 * XBeeLightTest.java
 *
 * Test for the jmri.jmrix.ieee802154.xbee.XBeeLight class
 *
 * @author Paul Bender copyright (C) 2012,2016
 */
public class XBeeLightTest {

    XBeeTrafficController tc;
    XBeeConnectionMemo memo;

    @Test
    public void testCtor() {
        XBeeLight s = new XBeeLight("ABCL1234", "XBee Light Test", tc);
        Assert.assertNotNull("exists", s);
    }

    @Test
    public void testCtorEncoderPinName() {
        XBeeLight s = new XBeeLight("ABCL123:4", "XBee Light Test", tc);
        Assert.assertNotNull("exists", s);
    }
 
    @Test
    public void testCtorHexNodeAddress() {
        XBeeLight s = new XBeeLight("ABCLABCD:4", "XBee Light Test", tc);
        Assert.assertNotNull("exists", s);
    }

    @BeforeEach
    public void setUp() {
        jmri.util.JUnitUtil.setUp();
        tc = new XBeeInterfaceScaffold();
        memo = new XBeeConnectionMemo();
        memo.setSystemPrefix("ABC");
        tc.setAdapterMemo(memo);
        memo.setLightManager(new XBeeLightManager(memo));
    }

    @AfterEach
    public void tearDown() {
        tc.terminate();
        jmri.util.JUnitUtil.clearShutDownManager(); // put in place because AbstractMRTrafficController implementing subclass was not terminated properly
        jmri.util.JUnitUtil.tearDown();

    }

}
