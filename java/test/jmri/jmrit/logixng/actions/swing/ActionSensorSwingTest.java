package jmri.jmrit.logixng.actions.swing;

import java.awt.GraphicsEnvironment;

import javax.swing.JPanel;

import jmri.InstanceManager;
import jmri.SensorManager;
import jmri.jmrit.logixng.*;
import jmri.jmrit.logixng.actions.ActionSensor;
import jmri.jmrit.logixng.swing.SwingConfiguratorInterfaceTestBase;
import jmri.util.JUnitUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.netbeans.jemmy.operators.*;

/**
 * Test ActionSensorSwing
 * 
 * @author Daniel Bergqvist 2018
 */
public class ActionSensorSwingTest extends SwingConfiguratorInterfaceTestBase {

    @Test
    public void testCtor() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        
        ActionSensorSwing t = new ActionSensorSwing();
        Assert.assertNotNull("exists",t);
    }
    
    @Test
    public void testCreatePanel() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        
        Assert.assertTrue("panel is not null",
            null != new ActionSensorSwing().getConfigPanel(new JPanel()));
        Assert.assertTrue("panel is not null",
            null != new ActionSensorSwing().getConfigPanel(new ActionSensor("IQDA1", null), new JPanel()));
    }
    
    @Test
    public void testDialogUseExistingSensor() throws SocketAlreadyConnectedException {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());

        InstanceManager.getDefault(SensorManager.class).provide("IS1");
        InstanceManager.getDefault(SensorManager.class).provide("IS2");

        ConditionalNG conditionalNG = InstanceManager.getDefault(ConditionalNG_Manager.class).createConditionalNG("IQC1", null);
        
        ActionSensor action = new ActionSensor("IQDA1", null);
        MaleSocket maleSocket = InstanceManager.getDefault(DigitalActionManager.class).registerAction(action);
        conditionalNG.getChild(0).connect(maleSocket);
        
        JDialogOperator jdo = editItem(conditionalNG, "Edit ConditionalNG IQC1", "Edit ! ", 0);
        
        new JComboBoxOperator(jdo, 0).setSelectedIndex(1);
        new JComboBoxOperator(jdo, 1).setSelectedItem(ActionSensor.SensorState.Inactive);
        new JButtonOperator(jdo, "OK").push();  // NOI18N
        
        Assert.assertEquals("IS1", action.getSensor().getBean().getSystemName());
        Assert.assertEquals(ActionSensor.SensorState.Inactive, action.getBeanState());
    }

    @Test
    public void testDialogCreateNewSensor() throws SocketAlreadyConnectedException {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());

        InstanceManager.getDefault(SensorManager.class).provide("IS1");
        InstanceManager.getDefault(SensorManager.class).provide("IS2");

        ConditionalNG conditionalNG = InstanceManager.getDefault(ConditionalNG_Manager.class).createConditionalNG("IQC1", null);
        
        ActionSensor action = new ActionSensor("IQDA1", null);
        MaleSocket maleSocket = InstanceManager.getDefault(DigitalActionManager.class).registerAction(action);
        conditionalNG.getChild(0).connect(maleSocket);
        
        JDialogOperator jdo = editItem(conditionalNG, "Edit ConditionalNG IQC1", "Edit ! ", 0);
        
        new JRadioButtonOperator(jdo, 1).clickMouse();
        new JTextFieldOperator(jdo, 3).enterText("IS99");
        
        new JComboBoxOperator(jdo, 1).setSelectedItem(ActionSensor.SensorState.Active);
        new JButtonOperator(jdo, "OK").push();  // NOI18N
        
        Assert.assertEquals("IS99", action.getSensor().getBean().getSystemName());
        Assert.assertEquals(ActionSensor.SensorState.Active, action.getBeanState());
    }
    
    // The minimal setup for log4J
    @Before
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.resetInstanceManager();
        JUnitUtil.resetProfileManager();
        JUnitUtil.initConfigureManager();
        JUnitUtil.initInternalSensorManager();
        JUnitUtil.initInternalSensorManager();
        InstanceManager.getDefault(LogixNGPreferences.class).setShowSystemUserNames(true);
        JUnitUtil.initLogixNGManager();
    }

    @After
    public void tearDown() {
        jmri.jmrit.logixng.util.LogixNG_Thread.stopAllLogixNGThreads();
        JUnitUtil.tearDown();
    }
    
}