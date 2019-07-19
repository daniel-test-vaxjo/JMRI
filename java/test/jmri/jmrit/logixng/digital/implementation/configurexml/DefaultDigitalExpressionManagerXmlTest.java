package jmri.jmrit.logixng.digital.implementation.configurexml;

import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Daniel Bergqvist Copyright (C) 2018
 */
public class DefaultDigitalExpressionManagerXmlTest {

    @Test
    public void testCTor() {
        DefaultDigitalExpressionManagerXml b = new DefaultDigitalExpressionManagerXml();
        Assert.assertNotNull("exists", b);
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

    // private final static Logger log = LoggerFactory.getLogger(DefaultDigitalExpressionManagerXmlTest.class);

}
