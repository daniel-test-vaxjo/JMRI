package jmri.jmrit.beantable;

import java.awt.GraphicsEnvironment;
import java.util.ResourceBundle;
import javax.swing.JFrame;
import jmri.InstanceManager;
import jmri.jmrit.newlogix.NewLogix;
import jmri.jmrit.newlogix.NewLogixManager;

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


/*
* Tests for the NewLogixTableAction Class
* Re-created using JUnit4 with support for the new conditional editors
* @author Dave Sand Copyright (C) 2017 (for the NewLogixTableAction class)
* @author Daniel Bergqvist Copyright (C) 2019
 */
public class NewLogixTableActionTest extends AbstractTableActionBase {

    static final ResourceBundle rbxNewLogixSwing = ResourceBundle.getBundle("jmri.jmrit.newlogix.tools.swing.NewLogixSwingBundle");

    @Rule
    public Timeout globalTimeout = Timeout.seconds(10); // 10 second timeout for methods in this test class.

    @Rule
    public RetryRule retryRule = new RetryRule(2); // allow 2 retries

    @Test
    public void testCtor() {
        Assert.assertNotNull("NewLogixTableActionTest Constructor Return", new NewLogixTableAction());  // NOI18N
    }

    @Test
    public void testStringCtor() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        Assert.assertNotNull("NewLogixTableAction Constructor Return", new NewLogixTableAction("test"));  // NOI18N
    }

    @Override
    public String getTableFrameName() {
        return Bundle.getMessage("TitleNewLogixTable");  // NOI18N
    }

    @Override
    @Test
    public void testGetClassDescription() {
        Assert.assertEquals("NewLogix Table Action class description", Bundle.getMessage("TitleNewLogixTable"), a.getClassDescription());  // NOI18N
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

    @Ignore     // Not working at the moment
    @Test
    public void testLogixBrowser() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        NewLogixTableAction newLogixTable = (NewLogixTableAction) a;

        newLogixTable.browserPressed("IQ:101");  // NOI18N

        JFrame frame = JFrameOperator.waitJFrame(Bundle.getMessage("BrowserTitle"), true, true);  // NOI18N
        Assert.assertNotNull(frame);
        JUnitUtil.dispose(frame);
    }

    @Test
    public void testTreeEditor() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        InstanceManager.getDefault(jmri.UserPreferencesManager.class).
                setProperty("jmri.jmrit.beantable.NewLogixTableAction", "Edit Mode", "TREEEDIT");  // NOI18N
        a.actionPerformed(null);
        NewLogixTableAction newLogixTable = (NewLogixTableAction) a;
        JFrameOperator newLogixFrame = new JFrameOperator(Bundle.getMessage("TitleNewLogixTable"));  // NOI18N
        Assert.assertNotNull(newLogixFrame);

        newLogixTable.editPressed("IQ:104");  // NOI18N
        JFrameOperator cdlFrame = new JFrameOperator(jmri.Bundle.formatMessage(rbxNewLogixSwing.getString("TitleEditNewLogix"), "IQ:104"));  // NOI18N
        Assert.assertNotNull(cdlFrame);
        new JMenuBarOperator(cdlFrame).pushMenuNoBlock(Bundle.getMessage("MenuFile")+"|"+rbxNewLogixSwing.getString("CloseWindow"), "|");  // NOI18N
        newLogixFrame.dispose();
    }

    @Ignore     // Not working at the moment
    @Test
    public void testAddNewLogixAutoName() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        NewLogixTableAction newLogixTable = (NewLogixTableAction) a;

        newLogixTable.actionPerformed(null); // show table
        JFrame newLogixFrame = JFrameOperator.waitJFrame(Bundle.getMessage("TitleNewLogixTable"), true, true);  // NOI18N
        Assert.assertNotNull("Found NewLogix Frame", newLogixFrame);  // NOI18N

        newLogixTable.addPressed(null);
        JFrameOperator addFrame = new JFrameOperator(Bundle.getMessage("TitleAddNewLogix"));  // NOI18N
        Assert.assertNotNull("Found Add NewLogix Frame", addFrame);  // NOI18N

        new JCheckBoxOperator(addFrame, 0).clickMouse();
        new JTextFieldOperator(addFrame, 1).setText("NewLogix 999");  // NOI18N
        new JButtonOperator(addFrame, Bundle.getMessage("ButtonCreate")).push();  // NOI18N

        NewLogix chk999 = jmri.InstanceManager.getDefault(jmri.jmrit.newlogix.NewLogixManager.class).getNewLogix("NewLogix 999");  // NOI18N
        Assert.assertNotNull("Verify IQ:999 Added", chk999);  // NOI18N

        // Add creates an edit frame; find and dispose
        JFrame editFrame = JFrameOperator.waitJFrame(Bundle.getMessage("TitleEditNewLogix"), true, true);  // NOI18N
        JUnitUtil.dispose(editFrame);

        JUnitUtil.dispose(newLogixFrame);
    }

    @Ignore     // Not working at the moment
    @Test
    public void testAddNewLogix() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        NewLogixTableAction newLogixTable = (NewLogixTableAction) a;

        newLogixTable.actionPerformed(null); // show table
        JFrame newLogixFrame = JFrameOperator.waitJFrame(Bundle.getMessage("TitleNewLogixTable"), true, true);  // NOI18N
        Assert.assertNotNull("Found NewLogix Frame", newLogixFrame);  // NOI18N

        newLogixTable.addPressed(null);
        JFrameOperator addFrame = new JFrameOperator(Bundle.getMessage("TitleAddNewLogix"));  // NOI18N
        Assert.assertNotNull("Found Add NewLogix Frame", addFrame);  // NOI18N

        new JTextFieldOperator(addFrame, 0).setText("105");  // NOI18N
        new JTextFieldOperator(addFrame, 1).setText("NewLogix 105");  // NOI18N
        new JButtonOperator(addFrame, Bundle.getMessage("ButtonCreate")).push();  // NOI18N

        NewLogix chk105 = jmri.InstanceManager.getDefault(NewLogixManager.class).getNewLogix("NewLogix 105");  // NOI18N
        Assert.assertNotNull("Verify IQ:105 Added", chk105);  // NOI18N

        // Add creates an edit frame; find and dispose
        JFrame editFrame = JFrameOperator.waitJFrame(Bundle.getMessage("TitleEditNewLogix"), true, true);  // NOI18N
        JUnitUtil.dispose(editFrame);

        JUnitUtil.dispose(newLogixFrame);
    }

    @Test
    public void testDeleteNewLogix() throws InterruptedException {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        NewLogixTableAction newLogixTable = (NewLogixTableAction) a;

        newLogixTable.actionPerformed(null); // show table
        JFrame newLogixFrame = JFrameOperator.waitJFrame(Bundle.getMessage("TitleNewLogixTable"), true, true);  // NOI18N
        Assert.assertNotNull("Found NewLogix Frame", newLogixFrame);  // NOI18N

        // Delete IQ:102, respond No
        Thread t1 = createModalDialogOperatorThread(Bundle.getMessage("QuestionTitle"), Bundle.getMessage("ButtonNo"));  // NOI18N
        newLogixTable.deletePressed("IQ:102");  // NOI18N
        t1.join();
        NewLogix chk102 = jmri.InstanceManager.getDefault(NewLogixManager.class).getBySystemName("IQ:102");  // NOI18N
        Assert.assertNotNull("Verify IQ:102 Not Deleted", chk102);  // NOI18N

        // Delete IQ:103, respond Yes
        Thread t2 = createModalDialogOperatorThread(Bundle.getMessage("QuestionTitle"), Bundle.getMessage("ButtonYes"));  // NOI18N
        newLogixTable.deletePressed("IQ:103");  // NOI18N
        t2.join();
        NewLogix chk103 = jmri.InstanceManager.getDefault(NewLogixManager.class).getBySystemName("IQ:103");  // NOI18N
        Assert.assertNull("Verify IQ:103 Is Deleted", chk103);  // NOI18N

        JUnitUtil.dispose(newLogixFrame);
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

        InstanceManager.getDefault(NewLogixManager.class).createNewLogix("IQ:101", "NewLogix 101");
        InstanceManager.getDefault(NewLogixManager.class).createNewLogix("IQ:102", "NewLogix 102");
        InstanceManager.getDefault(NewLogixManager.class).createNewLogix("IQ:103", "NewLogix 103");
        InstanceManager.getDefault(NewLogixManager.class).createNewLogix("IQ:104", "NewLogix 104");

        helpTarget = "package.jmri.jmrit.beantable.LogixTable"; 
        a = new NewLogixTableAction();
    }

    @After
    @Override
    public void tearDown() {
        a = null;
        JUnitUtil.tearDown();
    }
}
