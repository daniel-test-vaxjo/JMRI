package jmri.jmrix.rps.configurexml;

import jmri.configurexml.JmriConfigureXmlException;
import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides load and store functionality for configuring RpsSensorManagers.
 * <p>
 * Uses the store method from the abstract base class, but provides a load
 * method here.
 *
 * @author Bob Jacobsen Copyright: Copyright (c) 2007
 */
@API(status = EXPERIMENTAL)
public class RpsSensorManagerXml extends jmri.managers.configurexml.AbstractSensorManagerConfigXML {

    public RpsSensorManagerXml() {
        super();
    }

    @Override
    public void setStoreElementClass(Element sensors) {
        sensors.setAttribute("class", this.getClass().getName());
    }

    @Override
    public boolean load(Element shared, Element perNode) throws JmriConfigureXmlException {
        // load individual sensors
        return loadSensors(shared);
    }

//    private final static Logger log = LoggerFactory.getLogger(RpsSensorManagerXml.class);

}
