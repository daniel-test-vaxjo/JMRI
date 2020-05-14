package jmri.jmrix.loconet.sdfeditor;

import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Paul Bender Copyright (C) 2017
 */
public class BranchToEditorTest {

    @Test
    public void testCTor() {
        BranchToEditor t = new BranchToEditor(new jmri.jmrix.loconet.sdf.BranchTo(1,2));
        Assert.assertNotNull("exists",t);
    }

    @Before
    public void setUp() {
        JUnitUtil.setUp();
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }

    // private final static Logger log = LoggerFactory.getLogger(BranchToEditorTest.class);

}
