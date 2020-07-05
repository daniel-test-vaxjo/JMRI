package jmri.jmrix.lenz.xnetsimulator;

import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;

/**
 * Handle configuring an XpressNet layout connection via an XNetSimulator
 * adapter.
 * <p>
 * This uses the {@link XNetSimulatorAdapter} class to do the actual connection.
 *
 * @author Bob Jacobsen Copyright (C) 2001, 2003
 * @author Paul Bender Copyright (C) 2009
 *
 * @see XNetSimulatorAdapter
 */
@API(status = EXPERIMENTAL)
public class ConnectionConfig extends jmri.jmrix.AbstractSimulatorConnectionConfig {

    /**
     * Ctor for an object being created during load process; Swing init is
     * deferred.
     * @param p serial port adapter.
     */
    public ConnectionConfig(jmri.jmrix.SerialPortAdapter p) {
        super(p);
    }

    /**
     * Ctor for a connection configuration with no preexisting adapter.
     * {@link #setInstance()} will fill the adapter member.
     */
    public ConnectionConfig() {
        super();
    }

    @Override
    public String name() {
        return Bundle.getMessage("XNetSimulatorName");
    }

    String manufacturerName = "Lenz"; // NOI18N

    @Override
    public String getManufacturer() {
        return manufacturerName;
    }

    @Override
    public void setManufacturer(String manu) {
        manufacturerName = manu;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setInstance() {
        if (adapter == null) {
            adapter = new XNetSimulatorAdapter();
        }
    }

}
