package jmri.jmrit.beantable;

import java.awt.GraphicsEnvironment;
import java.util.ResourceBundle;
import javax.swing.JFrame;
import jmri.InstanceManager;

import jmri.util.*;
import jmri.util.junit.rules.*;

import org.junit.*;
import org.junit.rules.*;

import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JCheckBoxOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JMenuBarOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import jmri.jmrit.logixng.LogixNG;
import jmri.jmrit.logixng.LogixNG_Manager;


/*
* Tests for the LogixNGTableAction Class
* Re-created using JUnit4 with support for the new conditional editors
* @author Dave Sand Copyright (C) 2017 (for the LogixTableActionTest class)
* @author Daniel Bergqvist Copyright (C) 2019
*/
public class LogixNGTableActionTest extends AbstractTableActionBase {

    static final ResourceBundle rbxLogixNGSwing = ResourceBundle.getBundle("jmri.jmrit.logixng.tools.swing.LogixNGSwingBundle");

    @Rule
    public Timeout globalTimeout = Timeout.seconds(10); // 10 second timeout for methods in this test class.

    @Rule
    public RetryRule retryRule = new RetryRule(2); // allow 2 retries

    @Test
    public void testCtor() {
        Assert.assertNotNull("LogixNGTableActionTest Constructor Return", new LogixNGTableAction());  // NOI18N
    }

    @Test
    public void testStringCtor() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        Assert.assertNotNull("LogixNGTableAction Constructor Return", new LogixNGTableAction("test"));  // NOI18N
    }

    @Override
    public String getTableFrameName() {
        return Bundle.getMessage("TitleLogixNGTable");  // NOI18N
    }

    @Override
    @Test
    public void testGetClassDescription() {
        Assert.assertEquals("LogixNG Table Action class description", Bundle.getMessage("TitleLogixNGTable"), a.getClassDescription());  // NOI18N
    }

    /**
     * Check the return value of includeAddButton.
     * <p>
     * The table generated by this action includes an Add Button.
     */
    @Override
    @Test
    public void testIncludeAddButton() {
        Assert.assertTrue("Default include add button", a.includeAddButton());  // NOI18N
    }

    @Test
    public void testLogixBrowser() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        LogixNGTableAction logixNGTable = (LogixNGTableAction) a;

        logixNGTable.browserPressed("IQ101");  // NOI18N

        JFrame frame = JFrameOperator.waitJFrame(Bundle.getMessage("BrowserTitle"), true, true);  // NOI18N
        Assert.assertNotNull(frame);
        JUnitUtil.dispose(frame);
    }

    @Test
    public void testTreeEditor() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        InstanceManager.getDefault(jmri.UserPreferencesManager.class).
                setProperty("jmri.jmrit.beantable.LogixNGTableAction", "Edit Mode", "TREEEDIT");  // NOI18N
        a.actionPerformed(null);
        LogixNGTableAction logixNGTable = (LogixNGTableAction) a;
        JFrameOperator logixNGFrame = new JFrameOperator(Bundle.getMessage("TitleLogixNGTable"));  // NOI18N
        Assert.assertNotNull(logixNGFrame);

        logixNGTable.editPressed("IQ104");  // NOI18N
        JFrameOperator cdlFrame = new JFrameOperator(jmri.Bundle.formatMessage(rbxLogixNGSwing.getString("TitleEditLogixNG"), "IQ104"));  // NOI18N
        Assert.assertNotNull(cdlFrame);
        new JMenuBarOperator(cdlFrame).pushMenuNoBlock(Bundle.getMessage("MenuFile")+"|"+rbxLogixNGSwing.getString("CloseWindow"), "|");  // NOI18N
        logixNGFrame.dispose();
    }

    @Test
    public void testAddLogixNGAutoName() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        LogixNGTableAction logixNGTable = (LogixNGTableAction) a;

        logixNGTable.actionPerformed(null); // show table
        JFrame logixNGFrame = JFrameOperator.waitJFrame(Bundle.getMessage("TitleLogixNGTable"), true, true);  // NOI18N
        Assert.assertNotNull("Found LogixNG Frame", logixNGFrame);  // NOI18N

        logixNGTable.addPressed(null);
        JFrameOperator addFrame = new JFrameOperator(Bundle.getMessage("TitleAddLogixNG"));  // NOI18N
        Assert.assertNotNull("Found Add LogixNG Frame", addFrame);  // NOI18N

        new JCheckBoxOperator(addFrame, 0).clickMouse();
        new JTextFieldOperator(addFrame, 1).setText("LogixNG 999");  // NOI18N
        new JButtonOperator(addFrame, Bundle.getMessage("ButtonCreate")).push();  // NOI18N

        LogixNG chk999 = jmri.InstanceManager.getDefault(jmri.jmrit.logixng.LogixNG_Manager.class).getLogixNG("LogixNG 999");  // NOI18N
        Assert.assertNotNull("Verify 'LogixNG 999' Added", chk999);  // NOI18N

        // Add creates an edit frame; find and dispose
        JFrame editFrame = JFrameOperator.waitJFrame(jmri.Bundle.formatMessage(rbxLogixNGSwing.getString("TitleEditLogixNG2"), "IQA0001", "LogixNG 999"), true, true);  // NOI18N
        JUnitUtil.dispose(editFrame);

        JUnitUtil.dispose(logixNGFrame);
    }

    @Test
    public void testAddLogixNG() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        LogixNGTableAction logixNGTable = (LogixNGTableAction) a;

        logixNGTable.actionPerformed(null); // show table
        JFrame logixNGFrame = JFrameOperator.waitJFrame(Bundle.getMessage("TitleLogixNGTable"), true, true);  // NOI18N
        Assert.assertNotNull("Found LogixNG Frame", logixNGFrame);  // NOI18N

        logixNGTable.addPressed(null);
        JFrameOperator addFrame = new JFrameOperator(Bundle.getMessage("TitleAddLogixNG"));  // NOI18N
        Assert.assertNotNull("Found Add LogixNG Frame", addFrame);  // NOI18N

        new JTextFieldOperator(addFrame, 0).setText("105");  // NOI18N
        new JTextFieldOperator(addFrame, 1).setText("LogixNG 105");  // NOI18N
        new JButtonOperator(addFrame, Bundle.getMessage("ButtonCreate")).push();  // NOI18N

        LogixNG chk105 = jmri.InstanceManager.getDefault(LogixNG_Manager.class).getLogixNG("LogixNG 105");  // NOI18N
        Assert.assertNotNull("Verify IQ105 Added", chk105);  // NOI18N

        // Add creates an edit frame; find and dispose
        JFrame editFrame = JFrameOperator.waitJFrame(jmri.Bundle.formatMessage(rbxLogixNGSwing.getString("TitleEditLogixNG2"), "IQ105", "LogixNG 105"), true, true);  // NOI18N
        JUnitUtil.dispose(editFrame);

        JUnitUtil.dispose(logixNGFrame);
    }

    @Test
    public void testDeleteLogixNG() throws InterruptedException {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        LogixNGTableAction logixNGTable = (LogixNGTableAction) a;

        logixNGTable.actionPerformed(null); // show table
        JFrame logixNGFrame = JFrameOperator.waitJFrame(Bundle.getMessage("TitleLogixNGTable"), true, true);  // NOI18N
        Assert.assertNotNull("Found LogixNG Frame", logixNGFrame);  // NOI18N

        // Delete IQ102, respond No
        Thread t1 = createModalDialogOperatorThread(Bundle.getMessage("QuestionTitle"), Bundle.getMessage("ButtonNo"));  // NOI18N
        logixNGTable.deletePressed("IQ102");  // NOI18N
        t1.join();
        LogixNG chk102 = jmri.InstanceManager.getDefault(LogixNG_Manager.class).getBySystemName("IQ102");  // NOI18N
        Assert.assertNotNull("Verify IQ102 Not Deleted", chk102);  // NOI18N

        // Delete IQ103, respond Yes
        Thread t2 = createModalDialogOperatorThread(Bundle.getMessage("QuestionTitle"), Bundle.getMessage("ButtonYes"));  // NOI18N
        logixNGTable.deletePressed("IQ103");  // NOI18N
        t2.join();
        LogixNG chk103 = jmri.InstanceManager.getDefault(LogixNG_Manager.class).getBySystemName("IQ103");  // NOI18N
        Assert.assertNull("Verify IQ103 Is Deleted", chk103);  // NOI18N

        JUnitUtil.dispose(logixNGFrame);
    }

    Thread createModalDialogOperatorThread(String dialogTitle, String buttonText) {
        Thread t = new Thread(() -> {
            // constructor for jdo will wait until the dialog is visible
            JDialogOperator jdo = new JDialogOperator(dialogTitle);
            JButtonOperator jbo = new JButtonOperator(jdo, buttonText);
            jbo.pushNoBlock();
        });
        t.setName(dialogTitle + " Close Dialog Thread");
        t.start();
        return t;
    }

    @Before
    @Override
    public void setUp() {
        JUnitUtil.setUp();
        jmri.util.JUnitUtil.resetProfileManager();
        jmri.util.JUnitUtil.initLogixManager();
        jmri.util.JUnitUtil.initDefaultUserMessagePreferences();

        InstanceManager.getDefault(LogixNG_Manager.class).createLogixNG("IQ101", "LogixNG 101");
        InstanceManager.getDefault(LogixNG_Manager.class).createLogixNG("IQ102", "LogixNG 102");
        InstanceManager.getDefault(LogixNG_Manager.class).createLogixNG("IQ103", "LogixNG 103");
        InstanceManager.getDefault(LogixNG_Manager.class).createLogixNG("IQ104", "LogixNG 104");

        helpTarget = "package.jmri.jmrit.beantable.LogixTable"; 
        a = new LogixNGTableAction();
    }

    @After
    @Override
    public void tearDown() {
        a = null;
        JUnitUtil.tearDown();
    }
}
