package jmri.jmrit.logixng.digital.expressions.configurexml;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jmri.InstanceManager;
import jmri.jmrit.logixng.DigitalExpressionManager;
import jmri.jmrit.logixng.MaleSocket;
import jmri.jmrit.logixng.digital.expressions.And;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jmri.jmrit.logixng.DigitalExpressionBean;

/**
 *
 */
public class AndXml extends jmri.managers.configurexml.AbstractNamedBeanManagerConfigXML {

    public AndXml() {
//        jmri.managers.configurexml.DefaultConditionalManagerXml a;
    }
/*
    @SuppressWarnings("unchecked")  // Reflection does not support generics
    private List<ActionMany.ActionEntry> getActionEntry(ActionMany actionMany)
            throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
        
        Field f = actionMany.getClass().getDeclaredField("actionEntries");
        f.setAccessible(true);
        return (List<ActionMany.ActionEntry>) f.get(actionMany);
    }
*/
    /**
     * Default implementation for storing the contents of a ActionMany
     *
     * @param o Object to store, of type ActionMany
     * @return Element containing the complete info
     */
    @Override
    public Element store(Object o) {
        And p = (And) o;

        Element element = new Element("and");
        element.setAttribute("class", this.getClass().getName());
        element.addContent(new Element("systemName").addContent(p.getSystemName()));
        if (p.getUserName() != null) {
            element.addContent(new Element("userName").addContent(p.getUserName()));
        }

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
/*        
        for (int i=0; i < p.getChildCount(); i++) {
            try {
//                    log.debug("action system name is " + entry.getSystemName());  // NOI18N
                Element e = new Element("item");
                e.addContent(new Element("index").addContent(Integer.toString(i)));
                
//                FemaleSocket socket = p.getChild(i);
                MaleSocket socket = p.getChild(i).getConnectedSocket();
                if (socket != null) {
//                    Element e2 = jmri.configurexml.ConfigXmlManager.elementFromObject(socket.getConnectedSocket());
                    Element e2 = jmri.configurexml.ConfigXmlManager.elementFromObject(socket);
                    if (e2 != null) {
                        e.addContent(e2);
                    }
                }
                
                element.addContent(e);
            } catch (Exception e) {
                log.error("Error storing action: {}", e, e);
            }
        }
*/        
        storeCommon(p, element);

//        element.addContent(addTurnoutElement(p.getLow(), "low"));
//        element.addContent(addTurnoutElement(p.getHigh(), "high"));

        return element;
    }
/*
    Element addTurnoutElement(NamedBeanHandle<Turnout> to, String which) {
        Element el = new Element("turnoutname");
        el.setAttribute("defines", which);
        el.addContent(to.getName());
        return el;
    }

    Element addTurnoutElement(Turnout to) {
        String user = to.getUserName();
        String sys = to.getSystemName();

        Element el = new Element("turnout");
        el.setAttribute("systemName", sys);
        if (user != null) {
            el.setAttribute("userName", user);
        }

        return el;
    }
*/
    @Override
    public boolean load(Element shared, Element perNode) {
//        List<Element> l = shared.getChildren("turnoutname");
/*        
        if (l.size() == 0) {
            l = shared.getChildren("turnout");  // older form
        }
        NamedBeanHandle<Turnout> low = loadTurnout(l.get(0));
        NamedBeanHandle<Turnout> high = loadTurnout(l.get(1));
*/        
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
            h = new And(sys, expressionSystemNames);
        } else {
            h = new And(sys, uname, expressionSystemNames);
        }

        loadCommon(h, shared);

        InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(h);
        return true;
    }
/*
    /.**
     * Process stored signal head output (turnout).
     * <p>
     * Needs to handle two types of element: turnoutname is new form; turnout is
     * old form.
     *
     * @param o xml object defining a turnout on an SE8C signal head
     * @return named bean for the turnout
     *./
    NamedBeanHandle<Turnout> loadTurnout(Object o) {
        Element e = (Element) o;

        if (e.getName().equals("turnout")) {
            String name = e.getAttribute("systemName").getValue();
            Turnout t;
            if (e.getAttribute("userName") != null
                    && !e.getAttribute("userName").getValue().equals("")) {
                name = e.getAttribute("userName").getValue();
                t = InstanceManager.turnoutManagerInstance().getTurnout(name);
            } else {
                t = InstanceManager.turnoutManagerInstance().getBySystemName(name);
            }
            return jmri.InstanceManager.getDefault(jmri.NamedBeanHandleManager.class).getNamedBeanHandle(name, t);
        } else {
            String name = e.getText();
            try {
                Turnout t = InstanceManager.turnoutManagerInstance().provideTurnout(name);
                return jmri.InstanceManager.getDefault(jmri.NamedBeanHandleManager.class).getNamedBeanHandle(name, t);
            } catch (IllegalArgumentException ex) {
                log.warn("Failed to provide Turnout \"{}\" in loadTurnout", name);
                return null;
            }
        }
    }
*/
    @Override
    public void load(Element element, Object o) {
        log.error("Invalid method called");
    }

    private final static Logger log = LoggerFactory.getLogger(AndXml.class);
}
