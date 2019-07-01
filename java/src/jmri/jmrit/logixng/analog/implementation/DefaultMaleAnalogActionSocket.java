package jmri.jmrit.logixng.analog.implementation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Set;
import jmri.JmriException;
import jmri.NamedBean;
import jmri.jmrit.logixng.Category;
import javax.annotation.Nonnull;
import jmri.jmrit.logixng.AnalogAction;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.FemaleSocket;
import jmri.jmrit.logixng.MaleAnalogActionSocket;
import jmri.jmrit.logixng.MaleSocket;
import jmri.util.Log4JUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Every AnalogAction has an DefaultMaleAnalogActionSocket as its parent.
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public class DefaultMaleAnalogActionSocket implements MaleAnalogActionSocket {

    private Base _parent = null;
    private final AnalogAction _action;
    private Lock _lock = Lock.NONE;
    private DebugConfig _debugConfig = null;
    private ErrorHandlingType _errorHandlingType = ErrorHandlingType.LOG_ERROR;
    private boolean _enabled = true;
    
    
    public DefaultMaleAnalogActionSocket(@Nonnull AnalogAction action) {
        _action = action;
    }
    
    /** {@inheritDoc} */
    @Override
    public Base getNewObjectBasedOnTemplate(String sys) {
        return _action.getNewObjectBasedOnTemplate(sys);
    }
    
    @Override
    public Base getParent() {
        return _parent;
    }
    
    @Override
    public void setParent(Base parent) {
        _parent = parent;
    }
    
    /** {@inheritDoc} */
    @Override
    public Lock getLock() {
        return _lock;
    }
    
    /** {@inheritDoc} */
    @Override
    public void setLock(Lock lock) {
        _lock = lock;
    }
    
    public ErrorHandlingType getErrorHandlingType() {
        return _errorHandlingType;
    }
    
    public void setErrorHandlingType(ErrorHandlingType errorHandlingType)
    {
        _errorHandlingType = errorHandlingType;
    }
    
    /** {@inheritDoc} */
    @Override
    public Category getCategory() {
        return _action.getCategory();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isExternal() {
        return false;
    }
    
    /**
     * Set the value of the AnalogAction.
     */
    private void internalSetValue(double value) {
        if (Double.isNaN(value)) {
            throw new IllegalArgumentException("The value is NaN");
        }
        if (value == Double.NEGATIVE_INFINITY) {
            throw new IllegalArgumentException("The value is negative infinity");
        }
        if (value == Double.POSITIVE_INFINITY) {
            throw new IllegalArgumentException("The value is positive infinity");
        }
        _action.setValue(value);
    }
    
    /** {@inheritDoc} */
    @Override
    public void setValue(double value) {
        if (! _enabled) {
            return;
        }
        
        if ((_debugConfig != null)
                && ((AnalogActionDebugConfig)_debugConfig)._dontExecute) {
            return;
        }
        
        try {
            internalSetValue(value);
        } catch (Exception e) {
            switch (_errorHandlingType) {
                case SHOW_DIALOG_BOX:
                    // We don't show a dialog box yet so log instead.
                    log.error("action {} thrown an exception: {}", _action.toString(), e);
                    break;
                    
                case LOG_ERROR:
                    log.error("action {} thrown an exception: {}", _action.toString(), e);
                    break;
                    
                case LOG_ERROR_ONCE:
                    Log4JUtil.warnOnce(log, "action {} thrown an exception: {}", _action.toString(), e);
                    break;
                    
                case THROW:
                    throw e;
                    
                default:
                    throw e;
            }
        }
    }

    @Override
    public String getShortDescription() {
        return _action.getShortDescription();
    }

    @Override
    public String getLongDescription() {
        return _action.getLongDescription();
    }

    @Override
    public FemaleSocket getChild(int index)
            throws IllegalArgumentException, UnsupportedOperationException {
        return _action.getChild(index);
    }

    @Override
    public int getChildCount() {
        return _action.getChildCount();
    }

    @Override
    public String getUserName() {
        return _action.getUserName();
    }

    @Override
    public void setUserName(String s) throws BadUserNameException {
        _action.setUserName(s);
    }

    @Override
    public String getSystemName() {
        return _action.getSystemName();
    }

    @Override
    public String getDisplayName() {
        return _action.getDisplayName();
    }

    @Override
    public String getFullyFormattedDisplayName() {
        return _action.getFullyFormattedDisplayName();
    }

    @Override
    public String getFullyFormattedDisplayName(boolean userNameFirst) {
        return _action.getFullyFormattedDisplayName(userNameFirst);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l, String name, String listenerRef) {
        _action.addPropertyChangeListener(l, name, listenerRef);
    }

    @Override
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener l, String name, String listenerRef) {
        _action.addPropertyChangeListener(propertyName, l, name, listenerRef);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        _action.addPropertyChangeListener(l);
    }

    @Override
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener l) {
        _action.addPropertyChangeListener(propertyName, l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        _action.removePropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener l) {
        _action.removePropertyChangeListener(propertyName, l);
    }

    @Override
    public void updateListenerRef(PropertyChangeListener l, String newName) {
        _action.updateListenerRef(l, newName);
    }

    @Override
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
        _action.vetoableChange(evt);
    }

    @Override
    public String getListenerRef(PropertyChangeListener l) {
        return _action.getListenerRef(l);
    }

    @Override
    public ArrayList<String> getListenerRefs() {
        return _action.getListenerRefs();
    }

    @Override
    public int getNumPropertyChangeListeners() {
        return _action.getNumPropertyChangeListeners();
    }

    @Override
    public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
        return _action.getPropertyChangeListeners();
    }

    @Override
    public synchronized PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return _action.getPropertyChangeListeners(propertyName);
    }

    @Override
    public PropertyChangeListener[] getPropertyChangeListenersByReference(String name) {
        return _action.getPropertyChangeListenersByReference(name);
    }

    @Override
    public void dispose() {
        _action.dispose();
    }

    /**
     * Register listeners if this object needs that.
     */
    @Override
    public void registerListeners() {
        _action.registerListeners();
    }
    
    /**
     * Register listeners if this object needs that.
     */
    @Override
    public void unregisterListeners() {
        _action.unregisterListeners();
    }
    
    @Override
    public void setState(int s) throws JmriException {
        _action.setState(s);
    }

    @Override
    public int getState() {
        return _action.getState();
    }

    @Override
    public String describeState(int state) {
        return _action.describeState(state);
    }

    @Override
    public String getComment() {
        return _action.getComment();
    }

    @Override
    public void setComment(String comment) {
        _action.setComment(comment);
    }

    @Override
    public void setProperty(String key, Object value) {
        _action.setProperty(key, value);
    }

    @Override
    public Object getProperty(String key) {
        return _action.getProperty(key);
    }

    @Override
    public void removeProperty(String key) {
        _action.removeProperty(key);
    }

    @Override
    public Set<String> getPropertyKeys() {
        return _action.getPropertyKeys();
    }

    @Override
    public String getBeanType() {
        return _action.getBeanType();
    }

    @Override
    public int compareSystemNameSuffix(String suffix1, String suffix2, NamedBean n2) {
        return _action.compareSystemNameSuffix(suffix1, suffix2, n2);
    }

    /** {@inheritDoc} */
    @Override
    public void setDebugConfig(DebugConfig config) {
        _debugConfig = config;
    }

    /** {@inheritDoc} */
    @Override
    public DebugConfig getDebugConfig() {
        return _debugConfig;
    }

    /** {@inheritDoc} */
    @Override
    public DebugConfig createDebugConfig() {
        return new AnalogActionDebugConfig();
    }

    /** {@inheritDoc} */
    @Override
    public Base getObject() {
        return _action;
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
    }


    public static class AnalogActionDebugConfig implements MaleSocket.DebugConfig {
        
        // If true, the socket is not executing the action.
        // It's useful if you want to test the LogixNG without affecting the
        // layout (turnouts, sensors, and so on).
        public boolean _dontExecute = false;
        
    }

    private final static Logger log = LoggerFactory.getLogger(DefaultMaleAnalogActionSocket.class);

}