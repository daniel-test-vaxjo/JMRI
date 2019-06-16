package jmri.jmrit.logixng.digital.actions;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    jmri.jmrit.logixng.digital.actions.configureswing.PackageTest.class,
    jmri.jmrit.logixng.digital.actions.configurexml.PackageTest.class,
    DigitalActionPluginSocketTest.class,
    DoAnalogActionTest.class,
    DoStringActionTest.class,
    HoldAnythingTest.class,
    IfThenTest.class,
    ManyTest.class,
    ShutdownComputerTest.class,
    SocketTest.class,
    ActionTurnoutTest.class,
})

/**
 * Invokes complete set of tests in the jmri.jmrit.logixng.actions tree
 *
 * @author Daniel Bergqvist 2018
 */
public class PackageTest {
}
