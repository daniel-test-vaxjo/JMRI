package jmri.jmrit.logixng.implementation;

import static jmri.NamedBean.UNKNOWN;

import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import jmri.InstanceManager;
import jmri.JmriException;
// import jmri.implementation.JmriSimplePropertyListener;
import jmri.implementation.AbstractNamedBean;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.ConditionalNG;
import jmri.jmrit.logixng.ConditionalNG_Manager;
import jmri.jmrit.logixng.FemaleSocket;
import jmri.jmrit.logixng.LogixNG;
import jmri.jmrit.logixng.LogixNG_Manager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default implementation of LogixNG.
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public class DefaultLogixNG extends AbstractNamedBean
        implements LogixNG {
    
//    private DefaultLogixNG _template;
    private boolean _enabled = false;
//    private boolean _userEnabled = false;
    private final List<ConditionalNG_Entry> _conditionalNG_Entries = new ArrayList<>();
    
    /**
     * Persistant instance variables (saved between runs)
     */
//    List<ConditionalNG> _conditionalNG_List = new ArrayList<>();
//    ArrayList<JmriSimplePropertyListener> _listeners = new ArrayList<>();
    
    /**
     * Maintain a list of conditional objects.  The key is the conditional system name
     */
    HashMap<String, ConditionalNG> _conditionalNGMap = new HashMap<>();
    
    /**
     * Operational instance variables (not saved between runs)
     */
    private boolean _isActivated = false;
    
    public DefaultLogixNG(String sys, String user) throws BadUserNameException, BadSystemNameException  {
        super(sys, user);
    }
    
    private DefaultLogixNG(DefaultLogixNG template) {
        super(InstanceManager.getDefault(LogixNG_Manager.class).getAutoSystemName(), null);
//        _template = template;
    }
    
    /** {@inheritDoc} */
    @Override
    public Base getNewObjectBasedOnTemplate() {
        return new DefaultLogixNG(this);
    }
    
    /** {@inheritDoc} */
    @Override
    public Base getParent() {
        return null;
    }
    
    /** {@inheritDoc} */
    @Override
    public void setParent(Base parent) {
        throw new UnsupportedOperationException("A LogixNG cannot have a parent");
    }
    
    @Override
    public String getBeanType() {
        return Bundle.getMessage("BeanNameLogixNG");
    }

    @Override
    public void setState(int s) throws JmriException {
        log.warn("Unexpected call to setState in DefaultLogixNG.");  // NOI18N
    }

    @Override
    public int getState() {
        log.warn("Unexpected call to getState in DefaultLogixNG.");  // NOI18N
        return UNKNOWN;
    }

    /*.*
     * Set enabled status. Enabled is a bound property All conditionals are set
     * to UNKNOWN state and recalculated when the Logix is enabled, provided the
     * Logix has been previously activated.
     *./
    @Override
    public void setEnabled(boolean state) {

        boolean old = _enabled;
        _enabled = state;
        if (old != state) {
/*            
            boolean active = _isActivated;
            deActivateLogix();
            activateLogix();
            _isActivated = active;
            for (int i = _listeners.size() - 1; i >= 0; i--) {
                _listeners.get(i).setEnabled(state);
            }
            firePropertyChange("Enabled", Boolean.valueOf(old), Boolean.valueOf(state));  // NOI18N
*/            
//        }
//    }

    /*.*
     * Get enabled status
     */
//    @Override
//    public boolean getEnabled() {
//        return _enabled;
//    }

    private final static Logger log = LoggerFactory.getLogger(DefaultLogixNG.class);

    @Override
    public String getShortDescription(Locale locale) {
        return "LogixNG";
    }

    @Override
    public String getLongDescription(Locale locale) {
        return "LogixNG: "+getDisplayName();
    }

    @Override
    public FemaleSocket getChild(int index) throws IllegalArgumentException, UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public int getChildCount() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Category getCategory() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public boolean isExternal() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Lock getLock() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void setLock(Lock lock) {
        throw new UnsupportedOperationException("Not supported.");
    }

    /** {@inheritDoc} */
    @Override
    final public void setup() {
        for (ConditionalNG_Entry entry : _conditionalNG_Entries) {
            if ( entry._conditionalNG == null
                    || !entry._conditionalNG.getSystemName()
                            .equals(entry._systemName)) {

                String systemName = entry._systemName;
                if (systemName != null) {
                    entry._conditionalNG =
                            InstanceManager.getDefault(ConditionalNG_Manager.class)
                                    .getBeanBySystemName(systemName);
                    if (entry._conditionalNG != null) {
                        entry._conditionalNG.setup();
                    } else {
                        log.error("cannot load conditionalNG " + systemName);
                    }
                }
            } else {
                entry._conditionalNG.setup();
            }
        }
        if (1==0) {
            try {
                throw new RuntimeException("Daniel");
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setEnabled(boolean enable) {
        _enabled = enable;
        if (enable) {
            registerListeners();
        } else {
            unregisterListeners();
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean isEnabled() {
        return _enabled;
//        return _enabled && _userEnabled;
    }
    
    /*.*
     * Set whenether this object is enabled or disabled by the user.
     * 
     * @param enable true if this object should be enabled, false otherwise
     *./
    public void setUserEnabled(boolean enable) {
        _userEnabled = enable;
    }
*/    
    /*.*
     * Determines whether this object is enabled by the user.
     * 
     * @return true if the object is enabled, false otherwise
     *./
    public boolean isUserEnabled() {
        return _userEnabled;
    }
*/    
    
    /** {@inheritDoc} */
    @Override
    public String getConditionalNG_SystemName(int index) {
        return _conditionalNG_Entries.get(index)._systemName;
    }
    
    /** {@inheritDoc} */
    @Override
    public void setConditionalNG_SystemName(int index, String systemName) {
        if (index == _conditionalNG_Entries.size()) {
            _conditionalNG_Entries.add(new ConditionalNG_Entry(systemName));
        } else {
            ConditionalNG_Entry entry = _conditionalNG_Entries.get(index);
            entry._systemName = systemName;
            entry._conditionalNG = null;
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public int getNumConditionalNGs() {
        return _conditionalNG_Entries.size();
    }

    /** {@inheritDoc} */
    @Override
    public void swapConditionalNG(int nextInOrder, int row) {
        if (row <= nextInOrder) {
            return;
        }
        ConditionalNG_Entry temp = _conditionalNG_Entries.get(row);
        for (int i = row; i > nextInOrder; i--) {
            _conditionalNG_Entries.set(i, _conditionalNG_Entries.get(i - 1));
        }
        _conditionalNG_Entries.set(nextInOrder, temp);
    }

    /** {@inheritDoc} */
    @Override
    public ConditionalNG getConditionalNG(int order) {
        try {
            return _conditionalNG_Entries.get(order)._conditionalNG;
        } catch (java.lang.IndexOutOfBoundsException ioob) {
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean addConditionalNG(ConditionalNG conditionalNG) {
        ConditionalNG chkDuplicate = _conditionalNGMap.putIfAbsent(conditionalNG.getSystemName(), conditionalNG);
        if (chkDuplicate == null) {
            ConditionalNG_Entry entry = new ConditionalNG_Entry(conditionalNG);
            _conditionalNG_Entries.add(entry);
            conditionalNG.setParent(this);
            return (true);
        }
        log.error("ConditionalNG '{}' has already been added to LogixNG '{}'", conditionalNG.getSystemName(), getSystemName());  // NOI18N
        return (false);
    }

    /** {@inheritDoc} */
    @Override
    public ConditionalNG getConditionalNG(String systemName) {
        return _conditionalNGMap.get(systemName);
    }
    
    /** {@inheritDoc} */
    @Override
    public ConditionalNG getConditionalNGByUserName(String userName) {
        for (ConditionalNG_Entry entry : _conditionalNG_Entries) {
            if (userName.equals(entry._conditionalNG.getUserName())) {
                return entry._conditionalNG;
            }
        }
        return null;
    }
    
    /** {@inheritDoc} */
    @Override
    public void deleteConditionalNG(ConditionalNG conditionalNG) {
        if (_conditionalNG_Entries.size() <= 0) {
            log.error("attempt to delete ConditionalNG not in LogixNG: {}", conditionalNG.getSystemName());  // NOI18N
            return;
        }
        
        boolean found = false;
        // Remove Conditional from this logix
        for (ConditionalNG_Entry entry : _conditionalNG_Entries) {
            if (conditionalNG == entry._conditionalNG) {
                _conditionalNG_Entries.remove(entry);
                found = true;
                break;
            }
        }
        if (!found) {
            log.error("attempt to delete ConditionalNG not in LogixNG: {}", conditionalNG.getSystemName());  // NOI18N
            return;
        }
        _conditionalNGMap.remove(conditionalNG.getSystemName());
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean isActive() {
        return _isActivated;
    }
    
    /** {@inheritDoc} */
    @Override
    public void activateLogixNG() {
        // if the Logix is already busy, simply return
        if (_isActivated) {
            return;
        }
        
        _isActivated = true;
        
        registerListeners();
/*        
        if (_enabled) {
            for (ConditionalNG conditionalNG : _conditionalNGMap.values()) {
                if (conditionalNG.isEnabled()) {
                    conditionalNG.registerListeners();
                }
            }
            
            for (ConditionalNG conditionalNG : _conditionalNGMap.values()) {
                if (conditionalNG.isEnabled()) {
                    if (conditionalNG.supportsEnableExecution()) {
                        if (conditionalNG.isEnabled() && conditionalNG.isExecutionEnabled()) {
                            conditionalNG.execute();
                        }
                    } else {
                        // The conditionalNG doesn't support enableExecution
                        // so we don't test if execution is enabled.
                        conditionalNG.execute();
                    }
                }
            }
        }
*/        
    }

    /** {@inheritDoc} */
    @Override
    public void deActivateLogixNG() {
        if (_isActivated) {
            // Logix is active, deactivate it and all listeners
            _isActivated = false;
            unregisterListeners();
            
            // remove listeners if there are any
//            for (ConditionalNG conditionalNG : _conditionalNGMap.values()) {
//                conditionalNG.unregisterListeners();
//            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void calculateConditionalNGs() {
        for (ConditionalNG_Entry entry : _conditionalNG_Entries) {
            entry._conditionalNG.execute();
        }
    }

    /** {@inheritDoc} */
    @Override
    public ConditionalNG getConditionalNG() {
        throw new UnsupportedOperationException("Not supported.");
    }

    /** {@inheritDoc} */
    @Override
    public LogixNG getLogixNG() {
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public final Base getRoot() {
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public void setParentForAllChildren() {
        for (ConditionalNG_Entry entry : _conditionalNG_Entries) {
            if (entry._conditionalNG != null) {
                entry._conditionalNG.setParent(this);
                entry._conditionalNG.setParentForAllChildren();
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void registerListeners() {
        for (ConditionalNG_Entry entry : _conditionalNG_Entries) {
            entry._conditionalNG.registerListeners();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void unregisterListeners() {
        for (ConditionalNG_Entry entry : _conditionalNG_Entries) {
            entry._conditionalNG.unregisterListeners();
        }
    }
    
    protected void printTreeRow(Locale locale, PrintWriter writer, String currentIndent) {
        writer.append(currentIndent);
        writer.append(getLongDescription(locale));
        writer.println();
    }
    
    /** {@inheritDoc} */
    @Override
    public void printTree(PrintWriter writer, String indent) {
        printTree(Locale.getDefault(), writer, indent, "");
    }
    
    /** {@inheritDoc} */
    @Override
    public void printTree(Locale locale, PrintWriter writer, String indent) {
        printTree(locale, writer, indent, "");
    }
    
    /** {@inheritDoc} */
    @Override
    public void printTree(Locale locale, PrintWriter writer, String indent, String currentIndent) {
        printTreeRow(locale, writer, currentIndent);

        for (int i=0; i < this.getNumConditionalNGs(); i++) {
            getConditionalNG(i).printTree(locale, writer, indent, currentIndent+indent);
        }
    }
    
    
    private static class ConditionalNG_Entry {
        private String _systemName;
        private ConditionalNG _conditionalNG;
        
        private ConditionalNG_Entry(ConditionalNG conditionalNG, String systemName) {
            _systemName = systemName;
            _conditionalNG = conditionalNG;
        }
        
        private ConditionalNG_Entry(ConditionalNG conditionalNG) {
            this._conditionalNG = conditionalNG;
        }
        
        private ConditionalNG_Entry(String systemName) {
            this._systemName = systemName;
        }
        
    }

}