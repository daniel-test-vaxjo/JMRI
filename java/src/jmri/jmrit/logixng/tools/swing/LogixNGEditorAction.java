package jmri.jmrit.logixng.tools.swing;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 * Swing action to create and register a LogixNGEditor object.
 *
 * @author Daniel Bergqvist Copyright (C) 2018
 */
public class LogixNGEditorAction extends AbstractAction {

    public LogixNGEditorAction(String s) {
        super(s);
    }

    public LogixNGEditorAction() {
        this(Bundle.getMessage("MenuLogixNGEditor")); // NOI18N
    }

    static LogixNGEditor logixNGEditorFrame = null;

    @Override
    @SuppressFBWarnings(value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification = "Only one LogixNGEditorFrame")
    public void actionPerformed(ActionEvent e) {
        // create a settings frame
        if (logixNGEditorFrame == null || !logixNGEditorFrame.isVisible()) {
            logixNGEditorFrame = new LogixNGEditor();
            logixNGEditorFrame.initComponents();
        }
        logixNGEditorFrame.setExtendedState(Frame.NORMAL);
        logixNGEditorFrame.setVisible(true); // this also brings the frame into focus
    }
    
}
