package jmri.jmrit.newlogix;

import jmri.jmrit.newlogix.engine.DefaultNewLogix;
import jmri.jmrit.newlogix.actions.ActionDoIf;
import jmri.jmrit.newlogix.actions.ActionTurnout;
import jmri.jmrit.newlogix.expressions.ExpressionAnd;
import jmri.jmrit.newlogix.expressions.ExpressionTurnout;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import jmri.InstanceManager;

/**
 * Test NewLogix
 * 
 * @author Daniel Bergqvist 2018
 */
public class NewLogixTest {

    @Test
    public void testManagers() {
        String systemName;
        NewLogix newLogix = InstanceManager.getDefault(jmri.jmrit.newlogix.NewLogixManager.class).createNewNewLogix("A new logix for test");  // NOI18N
        systemName = InstanceManager.getDefault(jmri.jmrit.newlogix.ExpressionManager.class).getNewSystemName(newLogix);
        Expression expression = new ExpressionTurnout(systemName, "An expression for test");  // NOI18N
        InstanceManager.getDefault(jmri.jmrit.newlogix.ExpressionManager.class).addExpression(expression);
//        InstanceManager.getDefault(jmri.ExpressionManager.class).addExpression(new ExpressionTurnout(systemName, "NewLogix 102, Expression 26"));  // NOI18N
        systemName = InstanceManager.getDefault(jmri.jmrit.newlogix.ActionManager.class).getNewSystemName(newLogix);
        Action action = new ActionTurnout(systemName, "An action for test");  // NOI18N
        InstanceManager.getDefault(jmri.jmrit.newlogix.ActionManager.class).addAction(action);
    }
    
    @Test
    public void testBundle() {
        Assert.assertTrue("bean type is correct", "New Logix".equals(new DefaultNewLogix("IQA55", null, null).getBeanType()));
        Assert.assertTrue("bean type is correct", "Action".equals(new ActionDoIf("IQA55:A321", null, ActionDoIf.Type.TRIGGER_ACTION, null, null).getBeanType()));
        Assert.assertTrue("bean type is correct", "Expression".equals(new ExpressionAnd("IQA55:E321", null).getBeanType()));
    }
    
    // The minimal setup for log4J
    @Before
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.resetInstanceManager();
        JUnitUtil.initInternalSensorManager();
        JUnitUtil.initInternalTurnoutManager();
        JUnitUtil.initNewLogixManager();
        JUnitUtil.initExpressionManager();
        JUnitUtil.initActionManager();
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }
    
}