package jmri.jmrit.logixng.actions.configurexml;

import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Level;
import jmri.InstanceManager;
import jmri.NamedBeanHandle;
import jmri.Turnout;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jmri.jmrit.logixng.Action;
import jmri.jmrit.logixng.FemaleActionSocket;
import jmri.jmrit.logixng.FemaleExpressionSocket;
import jmri.jmrit.logixng.actions.ActionIfThen;

/**
 *
 */
public class ActionIfThenXml extends jmri.managers.configurexml.AbstractNamedBeanManagerConfigXML {

    public ActionIfThenXml() {
    }

    private ActionIfThen.Type getType(Action action) throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
        Field f = action.getClass().getDeclaredField("_type");
        f.setAccessible(true);
        return (ActionIfThen.Type) f.get(action);
    }

    private FemaleExpressionSocket getIfExpressionSocket(Action action) throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
        Field f = action.getClass().getDeclaredField("_ifExpressionSocket");
        f.setAccessible(true);
        return (FemaleExpressionSocket) f.get(action);
    }

    private FemaleActionSocket getThenActionSocket(Action action) throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
        Field f = action.getClass().getDeclaredField("_thenActionSocket");
        f.setAccessible(true);
        return (FemaleActionSocket) f.get(action);
    }

    /**
     * Default implementation for storing the contents of a SE8cSignalHead
     *
     * @param o Object to store, of type TripleTurnoutSignalHead
     * @return Element containing the complete info
     */
    @Override
    public Element store(Object o) {
        ActionIfThen p = (ActionIfThen) o;

        Element element = new Element("actionIfThen");
        element.setAttribute("class", this.getClass().getName());
        element.addContent(new Element("systemName").addContent(p.getSystemName()));
        if (p.getUserName() != null) {
            element.addContent(new Element("userName").addContent(p.getUserName()));
        }

        try {
            element.setAttribute("type", getType(p).name());
            FemaleExpressionSocket ifExpressionSocket = getIfExpressionSocket(p);
            if (ifExpressionSocket.isConnected()) {
                element.addContent(new Element("ifSystemName").addContent(ifExpressionSocket.getConnectedSocket().getSystemName()));
            }
            FemaleActionSocket _thenActionSocket = getThenActionSocket(p);
            if (_thenActionSocket.isConnected()) {
                element.addContent(new Element("thenSystemName").addContent(_thenActionSocket.getConnectedSocket().getSystemName()));
            }
        } catch (Exception e) {
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
        Action h;
        if (uname == null) {
            h = new ActionIfThen(sys, ActionIfThen.Type.TRIGGER_ACTION);
        } else {
            h = new ActionIfThen(sys, uname, ActionIfThen.Type.TRIGGER_ACTION);
//            h = new ActionIfThen(sys, low, high, uname);
        }

        loadCommon(h, shared);

        InstanceManager.getDefault(jmri.jmrit.logixng.ActionManager.class).register(h);
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

    private final static Logger log = LoggerFactory.getLogger(ActionIfThenXml.class);
}