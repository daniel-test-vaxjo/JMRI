package jmri.jmrit.operations.trains;

import jmri.jmrit.operations.OperationsTestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Paul Bender Copyright (C) 2017
 */
public class TrainsTableActionTest extends OperationsTestCase {

    @Test
    public void testCTor() {
        TrainsTableAction t = new TrainsTableAction();
        Assert.assertNotNull("exists", t);
    }

    // private final static Logger log = LoggerFactory.getLogger(TrainsTableActionTest.class);

}
