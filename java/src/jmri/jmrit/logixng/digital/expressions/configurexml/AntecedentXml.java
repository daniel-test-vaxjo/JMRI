package jmri.jmrit.logixng.digital.expressions.configurexml;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jmri.InstanceManager;
import jmri.jmrit.logixng.DigitalExpressionManager;
import jmri.jmrit.logixng.MaleSocket;
import jmri.jmrit.logixng.digital.expressions.Antecedent;
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
public class AntecedentXml extends jmri.managers.configurexml.AbstractNamedBeanManagerConfigXML {

    public AntecedentXml() {
    }
    
    /**
     * Default implementation for storing the contents of a ActionMany
     *
     * @param o Object to store, of type ActionMany
     * @return Element containing the complete info
     */
    @Override
    public Element store(Object o) {
        Antecedent p = (Antecedent) o;

        Element element = new Element("antecedent");
        element.setAttribute("class", this.getClass().getName());
        element.addContent(new Element("systemName").addContent(p.getSystemName()));
        
        storeCommon(p, element);
        
        Element e = new Element("expressions");
        for (int i=0; i < p.getChildCount(); i++) {
            Element e2 = new Element("socket");
            e2.addContent(new Element("socketName").addContent(p.getChild(i).getName()));
            MaleSocket socket = p.getChild(i).getConnectedSocket();
            String socketSystemName;
            if (socket != null) {
                socketSystemName = socket.getSystemName();
            } else {
                socketSystemName = p.getExpressionSystemName(i);
            }
            if (socketSystemName != null) {
                e2.addContent(new Element("systemName").addContent(socketSystemName));
            }
            e.addContent(e2);
        }
        element.addContent(e);

        return element;
    }
    
    @Override
    public boolean load(Element shared, Element perNode) {
        List<Map.Entry<String, String>> expressionSystemNames = new ArrayList<>();
        
        Element actionElement = shared.getChild("expressions");
        for (Element socketElement : actionElement.getChildren()) {
            String socketName = socketElement.getChild("socketName").getTextTrim();
            Element systemNameElement = socketElement.getChild("systemName");
            String systemName = null;
            if (systemNameElement != null) {
                systemName = systemNameElement.getTextTrim();
            }
            expressionSystemNames.add(new AbstractMap.SimpleEntry<>(socketName, systemName));
        }
        
        // put it together
        String sys = getSystemName(shared);
        String uname = getUserName(shared);
        DigitalExpressionBean h;
        if (uname == null) {
            h = new Antecedent(sys, null, "", expressionSystemNames);
        } else {
            h = new Antecedent(sys, uname, "", expressionSystemNames);
        }

        loadCommon(h, shared);

        InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(h);
        return true;
    }
    
    @Override
    public void load(Element element, Object o) {
        log.error("Invalid method called");
    }

    private final static Logger log = LoggerFactory.getLogger(AntecedentXml.class);
}
