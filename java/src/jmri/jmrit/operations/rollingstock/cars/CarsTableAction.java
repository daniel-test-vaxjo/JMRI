package jmri.jmrit.operations.rollingstock.cars;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;

/**
 * Swing action to create and register a CarsTableFrame object.
 *
 * @author Bob Jacobsen Copyright (C) 2001
 * @author Daniel Boudreau Copyright (C) 2008
 */
@API(status = MAINTAINED)
public class CarsTableAction extends AbstractAction {

    public CarsTableAction() {
        super(Bundle.getMessage("MenuCars")); // NOI18N
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // create a car table frame
        new CarsTableFrame(true, null, null);
    }
}


