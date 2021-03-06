package jmri.jmrix.pi.configurexml;

import jmri.util.JUnitUtil;

import org.junit.Assert;
import org.junit.jupiter.api.*;

/**
 * Tests for the RaspberryPiTurnoutManagerXml class.
 *
 * @author   Paul Bender  Copyright (C) 2016
 */
public class RaspberryPiTurnoutManagerXmlTest {

    @Test
    public void testCtor(){
      Assert.assertNotNull("RaspberryPiTurnoutManagerXml constructor", new RaspberryPiTurnoutManagerXml());
    }

    @BeforeEach
    public void setUp() {
        JUnitUtil.setUp();
    }

    @AfterEach
    public void tearDown() {
        JUnitUtil.tearDown();
    }

}

