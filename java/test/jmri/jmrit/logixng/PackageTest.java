package jmri.jmrit.logixng;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    jmri.jmrit.logixng.actions.PackageTest.class,
    jmri.jmrit.logixng.analogactions.PackageTest.class,
    jmri.jmrit.logixng.analogexpressions.PackageTest.class,
    jmri.jmrit.logixng.engine.PackageTest.class,
    jmri.jmrit.logixng.expressions.PackageTest.class,
    jmri.jmrit.logixng.log.PackageTest.class,
    jmri.jmrit.logixng.tools.swing.PackageTest.class,
    ExpressionTest.class,
    LogixNGCategoryTest.class,
    LogixNGTest.class,
})

/**
 * Invokes complete set of tests in the jmri.jmrit.newlogix tree
 *
 * @author Daniel Bergqvist 2018
 */
public class PackageTest {
}
