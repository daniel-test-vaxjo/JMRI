package jmri.jmrix.lenz.configurexml;

import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides load and store functionality for configuring XNetTurnoutManagers.
 * <p>
 * Uses the store method from the abstract base class, but provides a load
 * method here.
 *
 * @author Bob Jacobsen Copyright: Copyright (c) 2002
 */
@API(status = EXPERIMENTAL)
public class XNetTurnoutManagerXml extends jmri.managers.configurexml.AbstractTurnoutManagerConfigXML {

    public XNetTurnoutManagerXml() {
        super();
    }

    @Override
    public void setStoreElementClass(Element turnouts) {
        turnouts.setAttribute("class", "jmri.jmrix.lenz.configurexml.XNetTurnoutManagerXml");
    }

    @Override
    public boolean load(Element shared, Element perNode) {
        // load individual turnouts
        return loadTurnouts(shared, perNode);
    }

//    private static final Logger log = LoggerFactory.getLogger(XNetTurnoutManagerXml.class);

}
