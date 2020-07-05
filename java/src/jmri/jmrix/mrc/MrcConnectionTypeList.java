package jmri.jmrix.mrc;

import jmri.jmrix.ConnectionTypeList;
import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;
import org.openide.util.lookup.ServiceProvider;

/**
 * Returns a list of valid MRC Connection Types
 *
 * @author Bob Jacobsen Copyright (C) 2010
 * @author Kevin Dickerson Copyright (C) 2014
 *
 */
@ServiceProvider(service = ConnectionTypeList.class)
@API(status = EXPERIMENTAL)
public class MrcConnectionTypeList implements jmri.jmrix.ConnectionTypeList {

    public static final String MRC = "MRC";

    @Override
    public String[] getAvailableProtocolClasses() {
        return new String[]{
            "jmri.jmrix.mrc.serialdriver.ConnectionConfig",
            "jmri.jmrix.mrc.simulator.ConnectionConfig",}; // NOI18N
    }

    @Override
    public String[] getManufacturers() {
        return new String[]{MRC};
    }

}
