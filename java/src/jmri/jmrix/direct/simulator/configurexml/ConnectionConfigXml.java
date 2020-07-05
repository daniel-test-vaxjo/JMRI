package jmri.jmrix.direct.simulator.configurexml;

import jmri.jmrix.configurexml.AbstractSerialConnectionConfigXml;
import jmri.jmrix.direct.simulator.ConnectionConfig;
import jmri.jmrix.direct.simulator.SimulatorAdapter;

import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;

/**
 * Handle XML persistence of layout connections by persisting the
 * SerialDriverAdapter (and connections).
 * <p>
 * Note this is named as the XML version of a ConnectionConfig object,
 * but it's actually persisting the SerialDriverAdapter.
 * <p>
 * This class is invoked from jmrix.JmrixConfigPaneXml on write, as that class
 * is the one actually registered. Reads are brought here directly via the class
 * attribute in the XML.
 *
 * @author Bob Jacobsen Copyright (c) 2003 copied from NCE/Tams code
 * @author kcameron Copyright (c) 2014
 */
@API(status = EXPERIMENTAL)
public class ConnectionConfigXml extends AbstractSerialConnectionConfigXml {

    public ConnectionConfigXml() {
        super();
    }

    @Override
    protected void getInstance(Object object) {
        adapter = ((ConnectionConfig) object).getAdapter();
    }

    @Override
    protected void getInstance() {
        if (adapter == null) {
            adapter = new SimulatorAdapter();
        }
    }

    @Override
    protected void register() {
        this.register(new ConnectionConfig(adapter));
    }

}
