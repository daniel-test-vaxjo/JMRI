package jmri.jmrit.logixng.tools.swing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    LogixNGEditorTest.class,
    TimeDiagramTest.class,
    ImportLogixTest.class,
})

/**
 * Invokes complete set of tests in the jmri.jmrit.logixng.tools.swing tree
 *
 * @author Daniel Bergqvist 2018
 */
public class PackageTest {
}
