package jmri.jmrit.logixng.digital.implementation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    jmri.jmrit.logixng.digital.implementation.configurexml.PackageTest.class,
    DefaultFemaleDigitalActionSocketFactoryTest.class,
    DefaultFemaleDigitalActionSocketTest.class,
    DefaultFemaleDigitalExpressionSocketFactoryTest.class,
    DefaultFemaleDigitalExpressionSocketTest.class,
    DefaultMaleDigitalActionSocketTest.class,
    DefaultMaleDigitalExpressionSocketTest.class,
    DigitalExpressionPluginAdapterTest.class,
})

/**
 * Invokes complete set of tests in the jmri.jmrit.logixng.engine tree
 *
 * @author Daniel Bergqvist 2018
 */
public class PackageTest {

}
