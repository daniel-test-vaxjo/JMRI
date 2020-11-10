package jmri.jmrit.beantable.oblock;

import jmri.jmrit.logix.Portal;
import jmri.util.JUnitUtil;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Paul Bender Copyright (C) 2017
 * @author Egbert Broerse Copyright (C) 2020
 */
public class PortalEditFrameTest {

    @Test
    public void testCTor() {
        Portal p = new Portal("portal-1");
        PortalEditFrame pef = new PortalEditFrame(
                "Edit Portal-1",
                p,
                null);
        Assert.assertNotNull("exists", pef);
    }

    @BeforeEach
    public void setUp() {
        JUnitUtil.setUp();
    }

    @AfterEach
    public void tearDown() {
        JUnitUtil.tearDown();
    }

    // private final static Logger log = LoggerFactory.getLogger(PortalEditFrameTest.class);

}
