package jmri.jmrit.logixng.string.actions;

import jmri.StringIO;
import jmri.JmriException;
import jmri.jmrit.logixng.AbstractBaseTestBase;
import jmri.jmrit.logixng.StringActionBean;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test AbstractStringAction
 * 
 * @author Daniel Bergqvist 2018
 */
public class AbstractStringActionTestBase extends AbstractBaseTestBase {

    @Test
    public void testGetBeanType() {
        Assert.assertTrue("String matches", "String action".equals(((StringActionBean)_base).getBeanType()));
    }
    
    @Test
    public void testState() throws JmriException {
        StringActionBean _action = (StringActionBean)_base;
        _action.setState(StringIO.INCONSISTENT);
        Assert.assertTrue("State matches", StringIO.INCONSISTENT == _action.getState());
        jmri.util.JUnitAppender.assertWarnMessage("Unexpected call to getState in AbstractStringAction.");
        _action.setState(StringIO.UNKNOWN);
        Assert.assertTrue("State matches", StringIO.UNKNOWN == _action.getState());
        jmri.util.JUnitAppender.assertWarnMessage("Unexpected call to getState in AbstractStringAction.");
        _action.setState(StringIO.INCONSISTENT);
        Assert.assertTrue("State matches", StringIO.INCONSISTENT == _action.getState());
        jmri.util.JUnitAppender.assertWarnMessage("Unexpected call to getState in AbstractStringAction.");
    }
    
}
