package jmri.jmrit.logixng.digital.expressions.configurexml;

import jmri.InstanceManager;
import jmri.jmrit.logixng.DigitalExpressionManager;
import jmri.jmrit.logixng.MaleSocket;
import jmri.jmrit.logixng.digital.expressions.Hold;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jmri.jmrit.logixng.DigitalExpressionBean;

/**
 * Handle XML configuration for ActionLightXml objects.
 *
 * @author Bob Jacobsen Copyright: Copyright (c) 2004, 2008, 2010
 * @author Daniel Bergqvist Copyright (C) 2019
 */
public class HoldXml extends jmri.managers.configurexml.AbstractNamedBeanManagerConfigXML {

    public HoldXml() {
    }
    
    /**
     * Default implementation for storing the contents of a ActionMany
     *
     * @param o Object to store, of type ActionMany
     * @return Element containing the complete info
     */
    @Override
    public Element store(Object o) {
        Hold p = (Hold) o;

        Element element = new Element("hold");
        element.setAttribute("class", this.getClass().getName());
        element.addContent(new Element("systemName").addContent(p.getSystemName()));
        
        storeCommon(p, element);
        
        Element e = new Element("expressions");
        for (int i=0; i < p.getChildCount(); i++) {
            MaleSocket socket = p.getChild(i).getConnectedSocket();
            if (socket != null) {
                e.addContent(new Element("systemName").addContent(socket.getSystemName()));
            }
        }
        element.addContent(e);

        return element;
    }
    
    @Override
    public boolean load(Element shared, Element perNode) {
        String sys = getSystemName(shared);
        String uname = getUserName(shared);
        DigitalExpressionBean h;
        if (uname == null) {
            h = new Hold(sys);
        } else {
            h = new Hold(sys, uname);
        }

        loadCommon(h, shared);

        InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(h);
        return true;
    }
    
    @Override
    public void load(Element element, Object o) {
        log.error("Invalid method called");
    }

    private final static Logger log = LoggerFactory.getLogger(HoldXml.class);
}
