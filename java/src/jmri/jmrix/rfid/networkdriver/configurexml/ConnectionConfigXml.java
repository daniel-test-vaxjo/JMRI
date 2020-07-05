package jmri.jmrix.rfid.networkdriver.configurexml;

import jmri.jmrix.configurexml.AbstractNetworkConnectionConfigXml;
import jmri.jmrix.rfid.networkdriver.ConnectionConfig;
import jmri.jmrix.rfid.networkdriver.NetworkDriverAdapter;

import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;

/**
 * Handle XML persistence of layout connections by persisting the
 * NetworkDriverAdapter (and connections).
 * <p>
 * Note this is named as the XML version of a ConnectionConfig object, but it's
 * actually persisting the NetworkDriverAdapter.
 * <p>
 * This class is invoked from jmrix.JmrixConfigPaneXml on write, as that class
 * is the one actually registered. Reads are brought here directly via the class
 * attribute in the XML.
 *
 * @author Bob Jacobsen Copyright: Copyright (c) 2003, 2015
 * @author kcameron Copyright (C) 2010 added multiple connections
 */
@API(status = EXPERIMENTAL)
public class ConnectionConfigXml extends AbstractNetworkConnectionConfigXml {

    public ConnectionConfigXml() {
        super();
    }

    @Override
    protected void getInstance() {
        adapter = new NetworkDriverAdapter();
    }

    @Override
    protected void getInstance(Object object) {
        adapter = ((ConnectionConfig) object).getAdapter();
    }

    @Override
    protected void register() {
        this.register(new ConnectionConfig(adapter));
    }

}
