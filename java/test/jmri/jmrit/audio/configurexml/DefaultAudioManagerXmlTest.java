package jmri.jmrit.audio.configurexml;

import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * DefaultAudioManagerXmlTest.java
 *
 * Test for the DefaultAudioManagerXml class
 *
 * @author   Paul Bender  Copyright (C) 2016
 */
public class DefaultAudioManagerXmlTest {

    @Test
    public void testCtor(){
      Assert.assertNotNull("DefaultAudioManagerXml constructor",new DefaultAudioManagerXml());
    }

    @Before
    public void setUp() {
        JUnitUtil.setUp();
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }

}

