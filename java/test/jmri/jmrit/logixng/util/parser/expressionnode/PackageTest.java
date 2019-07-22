package jmri.jmrit.logixng.util.parser.expressionnode;

import jmri.jmrit.logixng.util.parser.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    ExpressionNodeBooleanOperatorTest.class,
    ExpressionNodeIdentifierTest.class,
    ExpressionNodeNumberTest.class,
    ExpressionNodeStringTest.class,
})

/**
 * Invokes complete set of tests in the jmri.jmrit.logixng.util tree
 *
 * @author Daniel Bergqvist 2018
 */
public class PackageTest {
}
