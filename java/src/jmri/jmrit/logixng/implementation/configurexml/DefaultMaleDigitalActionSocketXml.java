package jmri.jmrit.logixng.implementation.configurexml;

import java.lang.reflect.Field;
import java.util.List;
import jmri.InstanceManager;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.digital.actions.Many;
import jmri.jmrit.logixng.implementation.DefaultMaleDigitalActionSocket;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jmri.jmrit.logixng.DigitalAction;

/**
 *
 */
public class DefaultMaleDigitalActionSocketXml extends jmri.managers.configurexml.AbstractNamedBeanManagerConfigXML {

    public DefaultMaleDigitalActionSocketXml() {
    }

    private DigitalAction getAction(DefaultMaleDigitalActionSocket maleActionSocket)
            throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
        Field f = maleActionSocket.getClass().getDeclaredField("_action");
        f.setAccessible(true);
        return (DigitalAction) f.get(maleActionSocket);
    }

    private Base.Lock getLock(DefaultMaleDigitalActionSocket maleActionSocket)
            throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
        Field f = maleActionSocket.getClass().getDeclaredField("_lock");
        f.setAccessible(true);
        return (Base.Lock) f.get(maleActionSocket);
    }

    /**
     * Default implementation for storing the contents of a DefaultMaleDigitalActionSocket
     *
     * @param o Object to store, of type DefaultMaleDigitalActionSocket
     * @return Element containing the complete info
     */
    @Override
    public Element store(Object o) {
        DefaultMaleDigitalActionSocket p = (DefaultMaleDigitalActionSocket) o;

        Element element = new Element("maleActionSocket");
        element.setAttribute("class", this.getClass().getName());
        element.addContent(new Element("systemName").addContent(p.getSystemName()));
        element.addContent(new Element("userName").addContent(p.getUserName()));

        try {
//                    log.debug("action system name is " + entry.getSystemName());  // NOI18N
            Element e = jmri.configurexml.ConfigXmlManager.elementFromObject(getAction(p));
            if (e != null) {
                element.addContent(e);
            }
            
            element.addContent(new Element("lock").addContent(getLock(p).name()));
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException e) {
            log.error("Error storing action: {}", e, e);
        }
        
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
        // put it together
        String sys = getSystemName(shared);
        String uname = getUserName(shared);
        DigitalAction h;
        if (uname == null) {
            h = new Many(sys);
        } else {
            h = new Many(sys, uname);
        }

        loadCommon(h, shared);

        InstanceManager.getDefault(jmri.jmrit.logixng.DigitalActionManager.class).register(h);
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

    private final static Logger log = LoggerFactory.getLogger(DefaultMaleDigitalActionSocketXml.class);
}
