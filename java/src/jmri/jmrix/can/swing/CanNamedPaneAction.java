package jmri.jmrix.can.swing;

import javax.swing.Icon;
import jmri.jmrix.can.CanSystemConnectionMemo;
import jmri.util.swing.JmriPanel;
import jmri.util.swing.WindowInterface;
import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action to create and load a JmriPanel from just its name.
 *
 * @author Bob Jacobsen Copyright (C) 2012
 */
@API(status = EXPERIMENTAL)
public class CanNamedPaneAction extends jmri.util.swing.JmriNamedPaneAction {

    /**
     * Enhanced constructor for placing the pane in various GUIs.
     * @param s Window Name
     * @param wi JmriJFrameInterface
     * @param paneClass Name of class to open
     * @param memo System Connection
     */
    public CanNamedPaneAction(String s, WindowInterface wi, String paneClass, CanSystemConnectionMemo memo) {
        super(s, wi, paneClass);
        this.memo = memo;
    }

    /**
     * Enhanced constructor for placing the pane in various GUIs.
     * @param s Window Name
     * @param i Icon to display
     * @param wi JmriJFrameInterface
     * @param paneClass Name of class to open
     * @param memo System Connection
     */
    public CanNamedPaneAction(String s, Icon i, WindowInterface wi, String paneClass, CanSystemConnectionMemo memo) {
        super(s, i, wi, paneClass);
        this.memo = memo;
    }

    CanSystemConnectionMemo memo;

    /**
     * Makes Panel and calls initComponents
     * {@inheritDoc}
     */
    @Override
    public JmriPanel makePanel() {
        JmriPanel p = super.makePanel();
        if (p == null) {
            return null;
        }

        try {
            ((CanPanelInterface) p).initComponents(memo);
            return p;
        } catch (Exception ex) {
            log.warn("could not init pane class: {}", paneClass, ex);
        }

        return p;
    }

    private final static Logger log = LoggerFactory.getLogger(CanNamedPaneAction.class);

}
