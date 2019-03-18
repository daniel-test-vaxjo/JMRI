package jmri.jmrit.logixng.implementation.configurexml;

import jmri.jmrit.logixng.implementation.configurexml.DefaultLogixNGManagerXml;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Daniel Bergqvist Copyright (C) 2018
 */
public class DefaultLogixNGManagerXmlTest {

    @Test
    public void testCTor() {
        new DefaultLogixNGManagerXml();
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

    // private final static Logger log = LoggerFactory.getLogger(DefaultLogixNGManagerXmlTest.class);

}
