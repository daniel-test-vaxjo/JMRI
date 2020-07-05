package jmri.jmrit.operations.trains.tools;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import jmri.jmrit.operations.trains.Train;

import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;

/**
 * Swing action to create and register a TrainByCarTypeFrame.
 *
 * @author Bob Jacobsen Copyright (C) 2001
 * @author Daniel Boudreau Copyright (C) 2010
 */
@API(status = MAINTAINED)
public class TrainByCarTypeAction extends AbstractAction {

    public TrainByCarTypeAction(Train train) {
        super(Bundle.getMessage("MenuItemShowCarTypes"));
        _train = train;
    }

    Train _train;

    @Override
    public void actionPerformed(ActionEvent e) {
        // create frame
        new TrainByCarTypeFrame(_train);
    }
}


