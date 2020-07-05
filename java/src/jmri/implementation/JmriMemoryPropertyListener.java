package jmri.implementation;

import java.beans.PropertyChangeEvent;
import jmri.Conditional;
import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A service class for monitoring a bound property in one of the JMRI Named
 * beans For use with properties having two states which are determined by
 * equality to a String value (e.g. Internal Memory).
 * <p>
 * This file is part of JMRI.
 * <p>
 * JMRI is free software; you can redistribute it and/or modify it under the
 * terms of version 2 of the GNU General Public License as published by the Free
 * Software Foundation. See the "COPYING" file for a copy of this license.
 * <p>
 * JMRI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * @author Pete Cressman Copyright (C) 2009
 * @since 2.5.1
 */
@API(status = EXPERIMENTAL)
public class JmriMemoryPropertyListener extends JmriSimplePropertyListener {

    String _data;

    JmriMemoryPropertyListener(String propName, int type, String name, Conditional.Type varType,
            Conditional client, String data) {
        super(propName, type, name, varType, client);
        _data = data;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        log.debug("\"{}\" sent PropertyChangeEvent {}, old value =\"{}\", new value =\"{}, enabled = {}", _varName, evt.getPropertyName(), evt.getOldValue(), evt.getNewValue(), _enabled);
        if (getPropertyName().equals(evt.getPropertyName())) {
            String newValue = (String) evt.getNewValue();
            String oldValue = (String) evt.getOldValue();
            if (newValue == null) {
                return;
            }
            if (oldValue == null) {
                return;
            }
            if (newValue.equals(_data) || oldValue.equals(_data)) {
                // property has changed to/from the watched state, calculate
                super.propertyChange(evt);
            }
        }
    }
    private final static Logger log = LoggerFactory.getLogger(JmriMemoryPropertyListener.class);
}
