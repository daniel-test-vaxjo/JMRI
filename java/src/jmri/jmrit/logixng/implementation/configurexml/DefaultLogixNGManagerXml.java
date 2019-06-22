package jmri.jmrit.logixng.implementation.configurexml;

import java.util.List;
import jmri.ConfigureManager;
import jmri.InstanceManager;
import jmri.JmriException;
import jmri.jmrit.logixng.ConditionalNG;
import jmri.jmrit.logixng.implementation.DefaultLogixNGManager;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jmri.jmrit.logixng.LogixNG;
import jmri.jmrit.logixng.implementation.DefaultLogixNG;
import jmri.jmrit.logixng.LogixNG_Manager;
import jmri.jmrit.logixng.MaleSocket;

/**
 * Provides the functionality for configuring LogixNGManagers
 * <P>
 *
 * @author Dave Duchamp Copyright (c) 2007
 * @author Daniel Bergqvist Copyright (c) 2018
 */
public class DefaultLogixNGManagerXml extends jmri.managers.configurexml.AbstractNamedBeanManagerConfigXML {

    public DefaultLogixNGManagerXml() {
    }

    /**
     * Default implementation for storing the contents of a LogixNG_Manager
     *
     * @param o Object to store, of type LogixNG_Manager
     * @return Element containing the complete info
     */
    @Override
    public Element store(Object o) {
        Element logixNGs = new Element("logixngs");
        setStoreElementClass(logixNGs);
        LogixNG_Manager tm = (LogixNG_Manager) o;
        if (tm != null) {
            for (LogixNG logixNG : tm.getNamedBeanSet()) {
                log.debug("logixng system name is " + logixNG.getSystemName());  // NOI18N
                boolean enabled = logixNG.isEnabled();
                Element elem = new Element("logixng");  // NOI18N
                elem.addContent(new Element("systemName").addContent(logixNG.getSystemName()));  // NOI18N

                // store common part
                storeCommon(logixNG, elem);

                // add conditionalNG elements
                DefaultConditionalNGXml defaultConditionalNGXml = new DefaultConditionalNGXml();
                for (int i=0; i < logixNG.getNumConditionalNGs(); i++) {
                    elem.addContent(defaultConditionalNGXml.store(logixNG.getConditionalNG(i)));
                }
/*                
                Element e2 = new Element("socket");
                e2.addContent(new Element("socketName").addContent(logixNG.getChild(0).getName()));
                MaleSocket socket = logixNG.getChild(0).getConnectedSocket();
                if (socket != null) {
                    e2.addContent(new Element("systemName").addContent(socket.getSystemName()));
                }
                elem.addContent(e2);
*/            
//                // As a work-around for backward compatibility, store systemName and username as attribute.
//                // Remove this in e.g. JMRI 4.11.1 and then update all the loadref comparison files
//                String uName = logixNG.getUserName();
//                if (uName != null && !uName.isEmpty()) {
//                    elem.setAttribute("userName", uName);  // NOI18N
//                }

                if (enabled) {
                    elem.setAttribute("enabled", "yes");  // NOI18N
                } else {
                    elem.setAttribute("enabled", "no");  // NOI18N
                }
                
                logixNGs.addContent(elem);
            }
        }
        return (logixNGs);
    }

    /**
     * Subclass provides implementation to create the correct top element,
     * including the type information. Default implementation is to use the
     * local class here.
     *
     * @param logixngs The top-level element being created
     */
    public void setStoreElementClass(Element logixngs) {
        logixngs.setAttribute("class", this.getClass().getName());  // NOI18N
    }

    @Override
    public void load(Element element, Object o) {
        log.error("Invalid method called");  // NOI18N
    }

    /**
     * Create a LogixNG_Manager object of the correct class, then register and
     * fill it.
     *
     * @param sharedLogixNG  Shared top level Element to unpack.
     * @param perNodeLogixNG Per-node top level Element to unpack.
     * @return true if successful
     */
    @Override
    public boolean load(Element sharedLogixNG, Element perNodeLogixNG) {
        // create the master object
        replaceLogixNGManager();
        // load individual sharedLogix
        loadLogixNGs(sharedLogixNG);
        return true;
    }

    /**
     * Utility method to load the individual Logix objects. If there's no
     * additional info needed for a specific logix type, invoke this with the
     * parent of the set of Logix elements.
     *
     * @param logixNGs Element containing the Logix elements to load.
     */
    public void loadLogixNGs(Element logixNGs) {
        List<Element> logixNGList = logixNGs.getChildren("logixng");  // NOI18N
        if (log.isDebugEnabled()) {
            log.debug("Found " + logixNGList.size() + " logixngs");  // NOI18N
        }
        LogixNG_Manager tm = InstanceManager.getDefault(jmri.jmrit.logixng.LogixNG_Manager.class);

        for (int i = 0; i < logixNGList.size(); i++) {
            
            Element logixNG_Element = logixNGList.get(i);

            String sysName = getSystemName(logixNG_Element);
            if (sysName == null) {
                log.warn("unexpected null in systemName " + logixNG_Element);  // NOI18N
                break;
            }

            String userName = getUserName(logixNG_Element);

            String yesno = "";
            if (logixNGList.get(i).getAttribute("enabled") != null) {  // NOI18N
                yesno = logixNG_Element.getAttribute("enabled").getValue();  // NOI18N
            }
            if (log.isDebugEnabled()) {
                log.debug("create logixng: (" + sysName + ")("  // NOI18N
                        + (userName == null ? "<null>" : userName) + ")");  // NOI18N
            }

            // Create a new LogixNG but don't setup the initial tree.
            DefaultLogixNG logixNG = (DefaultLogixNG)tm.createLogixNG(sysName, userName);
            if (logixNG != null) {
                // load common part
                loadCommon(logixNG, logixNGList.get(i));

                // set enabled/disabled if attribute was present
                if ((yesno != null) && (!yesno.equals(""))) {
                    if (yesno.equals("yes")) {  // NOI18N
                        logixNG.setEnabled(true);
                    } else if (yesno.equals("no")) {  // NOI18N
                        logixNG.setEnabled(false);
                    }
                }
                
                // load conditionals, if there are any
                List<Element> logixNGConditionalList = logixNG_Element.getChildren("conditionalng");  // NOI18N
                if (logixNGConditionalList.size() > 0) {
                    
                    // add conditionalNGs
                    DefaultConditionalNGXml defaultConditionalNGXml = new DefaultConditionalNGXml();
                    
                    for (int n = 0; n < logixNGConditionalList.size(); n++) {
                        try {
                            logixNG.addConditionalNG(defaultConditionalNGXml.loadConditionalNG(logixNG, logixNGConditionalList.get(n)));
                        } catch (JmriException e) {
                            log.error("exception thrown", e);
                        }
/*                        
                        if (logixNG_Element.getAttribute("systemName") == null) {  // NOI18N
                            log.warn("unexpected null in systemName " + logixNGConditionalList.get(n)  // NOI18N
                                    + " " + logixNG_Element.getAttributes());
                            break;
                        }
                        String cSysName = logixNG_Element.getAttribute("systemName").getValue();  // NOI18N
//                        int cOrder = Integer.parseInt(logixNG_Element
//                                .getAttribute("order").getValue());  // NOI18N
                        
                        ConditionalNG conditionalNG;
                        // add conditional to logix
                        x.addConditionalNG(conditionalNG);
//                        x.addConditionalNG(cSysName);
*/                        
                    }
                }
/*                
                Element socketSystemName = logixNG_Element.getChild("socket").getChild("systemName");
                if (socketSystemName != null) {
                    x.setSocketSystemName(socketSystemName.getTextTrim());
                }
*/                
/*                
                // load conditionals, if there are any
                List<Element> logixConditionalList = logixNGList.get(i).getChildren("logixConditional");  // NOI18N
                if (logixConditionalList.size() > 0) {
                    // add conditionals
                    for (int n = 0; n < logixConditionalList.size(); n++) {
                        if (logixConditionalList.get(n).getAttribute("systemName") == null) {  // NOI18N
                            log.warn("unexpected null in systemName " + logixConditionalList.get(n)  // NOI18N
                                    + " " + logixConditionalList.get(n).getAttributes());
                            break;
                        }
                        String cSysName = logixConditionalList.get(n)
                                .getAttribute("systemName").getValue();  // NOI18N
                        int cOrder = Integer.parseInt(logixConditionalList.get(n)
                                .getAttribute("order").getValue());  // NOI18N
                        // add conditional to logix
                        x.addConditional(cSysName, cOrder);
                    }
                }
*/                
            }
        }
    }

    /**
     * Replace the current LogixManager, if there is one, with one newly created
     * during a load operation. This is skipped if they are of the same absolute
     * type.
     */
    protected void replaceLogixNGManager() {
        if (InstanceManager.getDefault(jmri.jmrit.logixng.LogixNG_Manager.class).getClass().getName()
                .equals(DefaultLogixNGManager.class.getName())) {
            return;
        }
        // if old manager exists, remove it from configuration process
        if (InstanceManager.getNullableDefault(jmri.LogixManager.class) != null) {
            ConfigureManager cmOD = InstanceManager.getNullableDefault(jmri.ConfigureManager.class);
            if (cmOD != null) {
                cmOD.deregister(InstanceManager.getDefault(jmri.LogixManager.class));
            }

        }

        // register new one with InstanceManager
        DefaultLogixNGManager pManager = DefaultLogixNGManager.instance();
        InstanceManager.store(pManager, LogixNG_Manager.class);
        // register new one for configuration
        ConfigureManager cmOD = InstanceManager.getNullableDefault(jmri.ConfigureManager.class);
        if (cmOD != null) {
            cmOD.registerConfig(pManager, jmri.Manager.LOGIXNGS);
        }
    }

    @Override
    public int loadOrder() {
        return InstanceManager.getDefault(jmri.jmrit.logixng.LogixNG_Manager.class).getXMLOrder();
    }

    private final static Logger log = LoggerFactory.getLogger(DefaultLogixNGManagerXml.class);
}
