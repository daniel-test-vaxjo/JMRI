package jmri.jmrit.operations.trains.tools;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import jmri.InstanceManager;
import jmri.jmrit.operations.trains.TrainManager;

import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;

/**
 * Swing action to toggle the show location hyphened name in full.
 *
 * @author Bob Jacobsen Copyright (C) 2001
 * @author Daniel Boudreau Copyright (C) 2020
 */
@API(status = MAINTAINED)
public class ToggleShowFullLocationName extends AbstractAction {

    public ToggleShowFullLocationName() {
        super(Bundle.getMessage("ToggleLocationName"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TrainManager trainManager = InstanceManager.getDefault(TrainManager.class);
        trainManager.setShowLocationHyphenNameEnabled(!trainManager.isShowLocationHyphenNameEnabled());
    }
}


