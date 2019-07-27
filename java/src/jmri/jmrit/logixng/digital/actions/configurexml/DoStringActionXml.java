package jmri.jmrit.logixng.digital.actions.configurexml;

import java.lang.reflect.Field;
import jmri.InstanceManager;
import jmri.jmrit.logixng.FemaleStringActionSocket;
import jmri.jmrit.logixng.FemaleStringExpressionSocket;
import jmri.jmrit.logixng.DigitalActionManager;
import jmri.jmrit.logixng.digital.actions.DoStringAction;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jmri.jmrit.logixng.DigitalActionBean;

/**
 * Handle XML configuration for ActionLightXml objects.
 *
 * @author Bob Jacobsen Copyright: Copyright (c) 2004, 2008, 2010
 * @author Daniel Bergqvist Copyright (C) 2019
 */
public class DoStringActionXml extends jmri.managers.configurexml.AbstractNamedBeanManagerConfigXML {

    public DoStringActionXml() {
    }

    private FemaleStringExpressionSocket getAnalogExpressionSocket(DigitalActionBean action) throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
        Field f = action.getClass().getDeclaredField("_stringExpressionSocket");
        f.setAccessible(true);
        return (FemaleStringExpressionSocket) f.get(action);
    }

    private FemaleStringActionSocket getAnalogActionSocket(DigitalActionBean action) throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
        Field f = action.getClass().getDeclaredField("_stringActionSocket");
        f.setAccessible(true);
        return (FemaleStringActionSocket) f.get(action);
    }

    /**
     * Default implementation for storing the contents of a DoStringAction
     *
     * @param o Object to store, of type TripleTurnoutSignalHead
     * @return Element containing the complete info
     */
    @Override
    public Element store(Object o) {
        DoStringAction p = (DoStringAction) o;

        Element element = new Element("do-string-action");
        element.setAttribute("class", this.getClass().getName());
        element.addContent(new Element("systemName").addContent(p.getSystemName()));
        
        storeCommon(p, element);

        try {
            FemaleStringExpressionSocket ifExpressionSocket = getAnalogExpressionSocket(p);
            if (ifExpressionSocket.isConnected()) {
                element.addContent(new Element("expressionSystemName").addContent(ifExpressionSocket.getConnectedSocket().getSystemName()));
            }
            FemaleStringActionSocket _thenActionSocket = getAnalogActionSocket(p);
            if (_thenActionSocket.isConnected()) {
                element.addContent(new Element("actionSystemName").addContent(_thenActionSocket.getConnectedSocket().getSystemName()));
            }
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException e) {
            log.error("Error storing action: {}", e, e);
        }

        return element;
    }
    
    @Override
    public boolean load(Element shared, Element perNode) {
        
        String sys = getSystemName(shared);
        String uname = getUserName(shared);
        DoStringAction h;
        if (uname == null) {
            h = new DoStringAction(sys);
        } else {
            h = new DoStringAction(sys, uname);
        }

        loadCommon(h, shared);

        Element ifSystemNameElement = shared.getChild("ifSystemName");
        if (ifSystemNameElement != null) {
            h.setStringActionSocketSystemName(ifSystemNameElement.getTextTrim());
        }
        Element thenSystemNameElement = shared.getChild("thenSystemName");
        if (thenSystemNameElement != null) {
            h.setStringExpressionSocketSystemName(thenSystemNameElement.getTextTrim());
        }
        
        InstanceManager.getDefault(DigitalActionManager.class).registerAction(h);
        return true;
    }
    
    @Override
    public void load(Element element, Object o) {
        log.error("Invalid method called");
    }

    private final static Logger log = LoggerFactory.getLogger(DoStringActionXml.class);
}
