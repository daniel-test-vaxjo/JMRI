package jmri.jmrix.grapevine.configurexml;

import jmri.util.JUnitUtil;

import org.junit.Assert;
import org.junit.jupiter.api.*;

/**
 * Tests for the SerialTurnoutManagerXml class
 *
 * @author   Paul Bender  Copyright (C) 2016
 */
public class SerialTurnoutManagerXmlTest {

    @Test
    public void testCtor(){
      Assert.assertNotNull("SerialTurnoutManagerXml constructor", new SerialTurnoutManagerXml());
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

