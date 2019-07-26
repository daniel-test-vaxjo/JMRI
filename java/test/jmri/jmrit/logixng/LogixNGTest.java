package jmri.jmrit.logixng;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import jmri.InstanceManager;
import jmri.NamedBean;
import jmri.jmrit.logixng.implementation.DefaultLogixNG;
import jmri.jmrit.logixng.implementation.DefaultConditionalNG;
import jmri.jmrit.logixng.digital.actions.IfThen;
import jmri.jmrit.logixng.digital.actions.ActionTurnout;
import jmri.jmrit.logixng.digital.expressions.And;
import jmri.jmrit.logixng.digital.expressions.ExpressionTurnout;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test LogixNG
 * 
 * @author Daniel Bergqvist 2018
 */
public class LogixNGTest {
    
    @Test
    public void testBaseLock() {
        Assert.assertTrue("isChangeableByUser is correct", Base.Lock.NONE.isChangeableByUser());
        Assert.assertTrue("isChangeableByUser is correct", Base.Lock.USER_LOCK.isChangeableByUser());
        Assert.assertFalse("isChangeableByUser is correct", Base.Lock.HARD_LOCK.isChangeableByUser());
        Assert.assertFalse("isChangeableByUser is correct", Base.Lock.TEMPLATE_LOCK.isChangeableByUser());
    }
    
    @Test
    public void testBundleClass() {
        Assert.assertTrue("bundle is correct", "Test Bundle bb aa cc".equals(Bundle.getMessage("TestBundle", "aa", "bb", "cc")));
        Assert.assertTrue("bundle is correct", "Generic".equals(Bundle.getMessage(Locale.US, "SocketTypeGeneric")));
        Assert.assertTrue("bundle is correct", "Test Bundle bb aa cc".equals(Bundle.getMessage(Locale.US, "TestBundle", "aa", "bb", "cc")));
    }
    
    @Test
    public void testCategory() {
        Assert.assertTrue("isChangeableByUser is correct", "Item".equals(Category.ITEM.toString()));
        Assert.assertTrue("isChangeableByUser is correct", "Common".equals(Category.COMMON.toString()));
        Assert.assertTrue("isChangeableByUser is correct", "Other".equals(Category.OTHER.toString()));
        Assert.assertTrue("isChangeableByUser is correct", "Extravaganza".equals(Category.EXRAVAGANZA.toString()));
    }
    
    @Test
    public void testManagers() throws SocketAlreadyConnectedException {
        String systemName;
        LogixNG logixNG = InstanceManager.getDefault(LogixNG_Manager.class).createLogixNG("A new logix for test");  // NOI18N
        ConditionalNG conditionalNG = new DefaultConditionalNG(logixNG.getSystemName()+":1");
        logixNG.addConditionalNG(conditionalNG);
        InstanceManager.getDefault(LogixNG_Manager.class).setupInitialConditionalNGTree(conditionalNG);
        MaleSocket many = conditionalNG.getChild(0).getConnectedSocket();
//        System.err.format("aa: %s%n", many.getLongDescription());
        Assert.assertTrue("description is correct", "Many".equals(many.getLongDescription()));
        MaleSocket ifThen = many.getChild(1).getConnectedSocket();
//        System.err.format("aa: %s%n", ifThen.getLongDescription());
        Assert.assertTrue("description is correct", "If E then A".equals(ifThen.getLongDescription()));
        systemName = InstanceManager.getDefault(DigitalExpressionManager.class).getNewSystemName();
        DigitalExpressionBean expression = new ExpressionTurnout(systemName, "An expression for test");  // NOI18N
        MaleSocket digitalExpressionBean = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(expression);
        ifThen.getChild(0).connect(digitalExpressionBean);
//        InstanceManager.getDefault(jmri.DigitalExpressionManager.class).addExpression(new ExpressionTurnout(systemName, "LogixNG 102, DigitalExpressionBean 26"));  // NOI18N
        systemName = InstanceManager.getDefault(DigitalActionManager.class).getNewSystemName();
        DigitalActionBean action = new ActionTurnout(systemName, "An action for test");  // NOI18N
        MaleSocket digitalActionBean = InstanceManager.getDefault(DigitalActionManager.class).registerAction(action);
        ifThen.getChild(1).connect(digitalActionBean);
        
        logixNG.setParentForAllChildren();
        
        Assert.assertTrue("conditionalng is correct", conditionalNG == digitalActionBean.getConditionalNG());
        Assert.assertTrue("conditionalng is correct", conditionalNG == conditionalNG.getConditionalNG());
        Assert.assertTrue("logixlng is correct", logixNG == digitalActionBean.getLogixNG());
        Assert.assertTrue("logixlng is correct", logixNG == logixNG.getLogixNG());
        
        Assert.assertTrue("instance manager is correct", Base.InstanceManagerContainer.defaultInstanceManager == digitalActionBean.getInstanceManager());
        Assert.assertTrue("instance manager is correct", Base.InstanceManagerContainer.defaultInstanceManager == conditionalNG.getInstanceManager());
        Assert.assertTrue("instance manager is correct", Base.InstanceManagerContainer.defaultInstanceManager == logixNG.getInstanceManager());
    }
    
    @Test
    public void testSetup() throws SocketAlreadyConnectedException {
        
        LogixNG logixNG = InstanceManager.getDefault(LogixNG_Manager.class).createLogixNG("A new logix for test");  // NOI18N
        DefaultConditionalNG conditionalNG = new DefaultConditionalNG(logixNG.getSystemName()+":1");
        logixNG.addConditionalNG(conditionalNG);
        
        String systemName = InstanceManager.getDefault(DigitalActionManager.class).getNewSystemName();
        DigitalActionBean action = new ActionTurnout(systemName, "An action for test");  // NOI18N
        MaleSocket digitalActionBean = InstanceManager.getDefault(DigitalActionManager.class).registerAction(action);
        
        conditionalNG.setSocketSystemName(systemName);
        logixNG.setup();
        
        logixNG.setParentForAllChildren();
        
//        System.err.format("%s%n", conditionalNG.getChild(0).getConnectedSocket().getLongDescription());
        Assert.assertTrue("conditionalng child is correct",
                "Set turnout '' to Thrown"
                        .equals(conditionalNG.getChild(0).getConnectedSocket().getLongDescription()));
        Assert.assertTrue("conditionalng is correct", conditionalNG == digitalActionBean.getConditionalNG());
        Assert.assertTrue("logixlng is correct", logixNG == digitalActionBean.getLogixNG());
    }
    
    @Test
    public void testExceptions() {
        new SocketAlreadyConnectedException().getMessage();
    }
    
    @Test
    public void testBundle() {
        Assert.assertTrue("bean type is correct", "LogixNG".equals(new DefaultLogixNG("IQA55").getBeanType()));
        Assert.assertTrue("bean type is correct", "Digital action".equals(new IfThen("IQDA321", IfThen.Type.TRIGGER_ACTION).getBeanType()));
        Assert.assertTrue("bean type is correct", "Digital expression".equals(new And("IQDE321").getBeanType()));
    }
    
    // The minimal setup for log4J
    @Before
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.resetInstanceManager();
        JUnitUtil.initInternalSensorManager();
        JUnitUtil.initInternalTurnoutManager();
        JUnitUtil.initLogixNGManager();
        JUnitUtil.initDigitalExpressionManager();
        JUnitUtil.initDigitalActionManager();
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }
    
}
