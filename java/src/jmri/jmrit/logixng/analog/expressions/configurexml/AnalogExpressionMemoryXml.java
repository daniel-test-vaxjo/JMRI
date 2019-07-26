package jmri.jmrit.logixng.analog.expressions.configurexml;

import jmri.InstanceManager;
import jmri.MemoryManager;
import jmri.NamedBeanHandle;
import jmri.jmrit.logixng.AnalogExpressionManager;
import jmri.jmrit.logixng.analog.expressions.AnalogExpressionMemory;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class AnalogExpressionMemoryXml extends jmri.managers.configurexml.AbstractNamedBeanManagerConfigXML {

    public AnalogExpressionMemoryXml() {
    }
    
    /**
     * Default implementation for storing the contents of a SE8cSignalHead
     *
     * @param o Object to store, of type TripleLightSignalHead
     * @return Element containing the complete info
     */
    @Override
    public Element store(Object o) {
        AnalogExpressionMemory p = (AnalogExpressionMemory) o;

        Element element = new Element("analog-expression-memory");
        element.setAttribute("class", this.getClass().getName());
        element.addContent(new Element("systemName").addContent(p.getSystemName()));

        storeCommon(p, element);

        NamedBeanHandle memory = p.getMemory();
        if (memory != null) {
            element.addContent(new Element("memory").addContent(memory.getName()));
        }
        
        return element;
    }
    
    @Override
    public boolean load(Element shared, Element perNode) {
        String sys = getSystemName(shared);
        String uname = getUserName(shared);
        AnalogExpressionMemory h;
        if (uname == null) {
            h = new AnalogExpressionMemory(sys);
        } else {
            h = new AnalogExpressionMemory(sys, uname);
        }

        loadCommon(h, shared);

        Element memoryName = shared.getChild("memory");
        if (memoryName != null) {
            h.setMemory(InstanceManager.getDefault(MemoryManager.class).getMemory(memoryName.getTextTrim()));
        }

        // this.checkedNamedBeanReference()
        // <T extends NamedBean> T checkedNamedBeanReference(String name, @Nonnull T type, @Nonnull Manager<T> m) {

        InstanceManager.getDefault(AnalogExpressionManager.class).registerExpression(h);
        return true;
    }
    
    @Override
    public void load(Element element, Object o) {
        log.error("Invalid method called");
    }

    private final static Logger log = LoggerFactory.getLogger(AnalogExpressionMemoryXml.class);
}
