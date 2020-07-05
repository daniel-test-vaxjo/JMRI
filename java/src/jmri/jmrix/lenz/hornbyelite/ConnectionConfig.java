package jmri.jmrix.lenz.hornbyelite;

import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;

/**
 * Handle configuring an XpressNet layout connection via the built in USB port
 * on the Hornby Elite.
 * <p>
 * This uses the {@link EliteAdapter} class to do the actual connection.
 *
 * @author Bob Jacobsen Copyright (C) 2001, 2003
 * @author Paul Bender Copyright (C) 2008
 *
 * @see EliteAdapter
 */
@API(status = EXPERIMENTAL)
public class ConnectionConfig extends jmri.jmrix.lenz.AbstractXNetSerialConnectionConfig {

    /**
     * Ctor for an object being created during load process.
     * Swing init is deferred.
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
        return Bundle.getMessage("HornbyElitePortTitle");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setInstance() {
        if (adapter == null) {
            adapter = new EliteAdapter();
        }
    }

}
