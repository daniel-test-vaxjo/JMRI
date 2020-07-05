package jmri.jmrit.operations.locations.schedules;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;

/**
 * Swing action to create and register a ScheduleTableFrame object.
 *
 * @author Bob Jacobsen Copyright (C) 2001
 * @author Daniel Boudreau Copyright (C) 2009
 */
@API(status = MAINTAINED)
public class SchedulesTableAction extends AbstractAction {

    public SchedulesTableAction() {
        super(Bundle.getMessage("Schedules"));
    }

    SchedulesTableFrame f = null;

    @Override
    public void actionPerformed(ActionEvent e) {
        // create a schedule table frame
        if (f == null || !f.isVisible()) {
            f = new SchedulesTableFrame();
        }
        f.setExtendedState(Frame.NORMAL);
        f.setVisible(true); // this also brings the frame into focus
    }
}


