package jmri.jmrit.display.layoutEditor;

import java.awt.GraphicsEnvironment;
import jmri.BlockManager;
import jmri.InstanceManager;
import jmri.ShutDownManager;
import jmri.util.JUnitUtil;
import org.junit.*;

/**
 * Test simple functioning of LayoutEditorHorizontalToolBarPanel
 *
 * @author	George Warner Copyright (C) 2019
 */
public class LayoutEditorHorizontalToolBarPanelTest {

    @Test
    public void testCtor() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        LayoutEditor le = new LayoutEditor();
        new LayoutEditorHorizontalToolBarPanel(le);
    }


    // from here down is testing infrastructure
    @Before
    public void setUp() throws Exception {
        JUnitUtil.setUp();
    }

    @After
    public void tearDown() throws Exception {
        InstanceManager.getDefault(ShutDownManager.class).deregister(InstanceManager.getDefault(BlockManager.class).shutDownTask);
        JUnitUtil.tearDown();
    }
    // private final static Logger log = LoggerFactory.getLogger(LayoutEditorHorizontalToolBarPanelTest.class);
}
