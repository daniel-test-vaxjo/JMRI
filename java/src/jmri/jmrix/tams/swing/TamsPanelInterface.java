package jmri.jmrix.tams.swing;

import jmri.jmrix.tams.TamsSystemConnectionMemo;

import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;

/**
 * JPanel interface to handle providing system connection information to a
 * panel.
 *
 * Based on work by Bob Jacobsen
 *
 * @author Kevin Dickerson Copyright (C) 2012
 */
@API(status = EXPERIMENTAL)
public interface TamsPanelInterface {

    /**
     * 2nd stage of initialization, invoked after the constructor is complete.
     * <p>
     * This needs to be connected to the initContext() method in implementing
     * classes.
     * @param memo system Connection.
     */
    public void initComponents(TamsSystemConnectionMemo memo);

}
