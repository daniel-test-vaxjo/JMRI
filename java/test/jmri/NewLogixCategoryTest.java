package jmri;

import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test NewLogixCategory
 * 
 * @author Daniel Bergqvist 2018
 */
public class NewLogixCategoryTest {

    @Test
    public void testEnum() {
        Assert.assertTrue("ITEM".equals(NewLogixCategory.ITEM.name()));
        Assert.assertTrue("COMMON".equals(NewLogixCategory.COMMON.name()));
        Assert.assertTrue("OTHER".equals(NewLogixCategory.OTHER.name()));
    }
    
    // The minimal setup for log4J
    @Before
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.resetInstanceManager();
        JUnitUtil.initInternalSensorManager();
        JUnitUtil.initInternalTurnoutManager();
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }
    
}