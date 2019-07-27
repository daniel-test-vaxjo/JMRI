package jmri.jmrit.logixng.digital.actions.configurexml;

import java.lang.reflect.Field;
import jmri.InstanceManager;
import jmri.jmrit.logixng.FemaleDigitalActionSocket;
import jmri.jmrit.logixng.FemaleDigitalExpressionSocket;
import jmri.jmrit.logixng.DigitalActionManager;
import jmri.jmrit.logixng.digital.actions.IfThen;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jmri.jmrit.logixng.DigitalActionBean;
import jmri.jmrit.logixng.MaleSocket;

/**
 * Handle XML configuration for ActionLightXml objects.
 *
 * @author Bob Jacobsen Copyright: Copyright (c) 2004, 2008, 2010
 * @author Daniel Bergqvist Copyright (C) 2019
 */
public class IfThenXml extends jmri.managers.configurexml.AbstractNamedBeanManagerConfigXML {

    public IfThenXml() {
    }

    private FemaleDigitalExpressionSocket getIfExpressionSocket(DigitalActionBean action) throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
        Field f = action.getClass().getDeclaredField("_ifExpressionSocket");
        f.setAccessible(true);
        return (FemaleDigitalExpressionSocket) f.get(action);
    }

    private FemaleDigitalActionSocket getThenActionSocket(DigitalActionBean action) throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
        Field f = action.getClass().getDeclaredField("_thenActionSocket");
        f.setAccessible(true);
        return (FemaleDigitalActionSocket) f.get(action);
    }

    /**
     * Default implementation for storing the contents of a SE8cSignalHead
     *
     * @param o Object to store, of type TripleTurnoutSignalHead
     * @return Element containing the complete info
     */
    @Override
    public Element store(Object o) {
        IfThen p = (IfThen) o;

        Element element = new Element("if-then");
        element.setAttribute("class", this.getClass().getName());
        element.addContent(new Element("systemName").addContent(p.getSystemName()));
        
        storeCommon(p, element);

        element.setAttribute("type", p.getType().name());
        
        try {
            Element e2 = new Element("ifSocket");
            e2.addContent(new Element("socketName").addContent(p.getChild(0).getName()));
            MaleSocket socket = getIfExpressionSocket(p).getConnectedSocket();
            String socketSystemName;
            if (socket != null) {
                socketSystemName = socket.getSystemName();
            } else {
                socketSystemName = p.getIfExpressionSocketSystemName();
            }
            if (socketSystemName != null) {
                e2.addContent(new Element("systemName").addContent(socketSystemName));
            }
            element.addContent(e2);
            
            e2 = new Element("thenSocket");
            e2.addContent(new Element("socketName").addContent(p.getChild(1).getName()));
            socket = getThenActionSocket(p).getConnectedSocket();
            if (socket != null) {
                socketSystemName = socket.getSystemName();
            } else {
                socketSystemName = p.getThenExpressionSocketSystemName();
            }
            if (socketSystemName != null) {
                e2.addContent(new Element("systemName").addContent(socketSystemName));
            }
            element.addContent(e2);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException e) {
            log.error("Error storing action: {}", e, e);
        }

        return element;
    }
    
    @Override
    public boolean load(Element shared, Element perNode) {
        
        IfThen.Type type = IfThen.Type.TRIGGER_ACTION;
        Attribute typeAttribute = shared.getAttribute("type");
        if (typeAttribute != null) {
            type = IfThen.Type.valueOf(typeAttribute.getValue());
        }
        
        String sys = getSystemName(shared);
        String uname = getUserName(shared);
        IfThen h;
        if (uname == null) {
            h = new IfThen(sys, type);
        } else {
            h = new IfThen(sys, uname, type);
        }

        loadCommon(h, shared);
        
        Element socketName = shared.getChild("ifSocket").getChild("socketName");
        if (socketName != null) {
            h.getChild(0).setName(socketName.getTextTrim());
        }
        Element socketSystemName = shared.getChild("ifSocket").getChild("systemName");
        if (socketSystemName != null) {
            h.setIfExpressionSocketSystemName(socketSystemName.getTextTrim());
        }
        
        socketName = shared.getChild("thenSocket").getChild("socketName");
        if (socketName != null) {
            h.getChild(1).setName(socketName.getTextTrim());
        }
        socketSystemName = shared.getChild("thenSocket").getChild("systemName");
        if (socketSystemName != null) {
            h.setThenActionSocketSystemName(socketSystemName.getTextTrim());
        }
        
        InstanceManager.getDefault(DigitalActionManager.class).registerAction(h);
        return true;
    }
    
    @Override
    public void load(Element element, Object o) {
        log.error("Invalid method called");
    }

    private final static Logger log = LoggerFactory.getLogger(IfThenXml.class);
}
