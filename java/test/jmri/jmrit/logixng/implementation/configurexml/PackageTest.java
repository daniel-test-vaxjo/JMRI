package jmri.jmrit.logixng.implementation.configurexml;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    DefaultDigitalActionManagerXmlTest.class,
    DefaultDigitalExpressionManagerXmlTest.class,
    DefaultLogixNGManagerXmlTest.class,
    DefaultMaleDigitalActionSocketXmlTest.class,
})

/**
 * Invokes complete set of tests in the jmri.jmrit.logixng.engine.configurexml tree
 *
 * @author Daniel Bergqvist 2018
 */
public class PackageTest {

}
