package jmri.jmrit.newlogix.engine;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    jmri.jmrit.newlogix.engine.configurexml.PackageTest.class,
    InternalActionTest.class,
    InternalExpressionTest.class,
    ExpressionPluginAdapterTest.class,
})

/**
 * Invokes complete set of tests in the jmri.jmrit.newlogix.internal tree
 *
 * @author Daniel Bergqvist 2018
 */
public class PackageTest {

}