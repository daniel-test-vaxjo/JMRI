package jmri.jmrit.logixng.digital.implementation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Set;
import jmri.JmriException;
import jmri.NamedBean;
import jmri.jmrit.logixng.Category;
import javax.annotation.Nonnull;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.FemaleSocket;
import jmri.jmrit.logixng.MaleSocket;
import jmri.jmrit.logixng.MaleDigitalActionSocket;
import jmri.jmrit.logixng.DigitalActionBean;
import jmri.jmrit.logixng.implementation.AbstractMaleSocket;

/**
 * Every DigitalActionBean has an DefaultMaleDigitalActionSocket as its parent.
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public class DefaultMaleDigitalActionSocket extends AbstractMaleSocket implements MaleDigitalActionSocket {

    private Base _parent = null;
    private final DigitalActionBean _action;
    private Lock _lock = Lock.NONE;
    private DebugConfig _debugConfig = null;
    private boolean _enabled = true;
    
    
    public DefaultMaleDigitalActionSocket(@Nonnull DigitalActionBean action) {
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
    
    /** {@inheritDoc} */
    @Override
    public void execute() {
        if (! _enabled) {
            return;
        }
        
        if ((_debugConfig != null)
                && ((DigitalActionDebugConfig)_debugConfig)._dontExecute) {
            return;
        }
        _action.execute();
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
    public void disposeMe() {
        _action.dispose();
    }

    /**
     * Register listeners if this object needs that.
     */
    @Override
    public void registerListenersForThisClass() {
        _action.registerListeners();
    }
    
    /**
     * Register listeners if this object needs that.
     */
    @Override
    public void unregisterListenersForThisClass() {
        System.err.format("Class: %s%n", this.getClass().getName());
        System.err.format("Class: %s, Action: %s%n", this.getClass().getName(), _action.getClass().getName());
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
        return new DigitalActionDebugConfig();
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
    


    public static class DigitalActionDebugConfig implements MaleSocket.DebugConfig {
        
        // If true, the socket is not executing the action.
        // It's useful if you want to test the LogixNG without affecting the
        // layout (turnouts, sensors, and so on).
        public boolean _dontExecute = false;
        
    }

}
