package jmri.jmrit.operations.rollingstock.cars.tools;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import jmri.jmrit.operations.rollingstock.cars.CarsTableModel;

import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;

/**
 * Swing action to reset checkboxes in the cars window.
 *
 * @author Bob Jacobsen Copyright (C) 2001
 * @author Daniel Boudreau Copyright (C) 2014
 * 
 */
@API(status = MAINTAINED)
public class ResetCheckboxesCarsTableAction extends AbstractAction {

    CarsTableModel _carsTableModel;

    public ResetCheckboxesCarsTableAction(CarsTableModel carsTableModel) {
        super(Bundle.getMessage("TitleResetCheckboxes"));
        _carsTableModel = carsTableModel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        _carsTableModel.resetCheckboxes();
    }
}


