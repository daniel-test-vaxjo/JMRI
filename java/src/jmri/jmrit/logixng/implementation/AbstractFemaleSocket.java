package jmri.jmrit.logixng.implementation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import jmri.JmriException;
import jmri.NamedBean;
import jmri.beans.Beans;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.FemaleSocket;
import jmri.jmrit.logixng.FemaleSocketListener;
import jmri.jmrit.logixng.MaleSocket;
import jmri.jmrit.logixng.SocketAlreadyConnectedException;

/**
 * Abstract female socket.
 * 
 * @author Daniel Bergqvist 2019
 */
public abstract class AbstractFemaleSocket implements FemaleSocket, NamedBean{
    
    // The reason AbstractFemaleSocket implements NamedBean is that it's
    // implementation classes implements NamedBean. And since there is a lot
    // of methods in NamedBean that isn't useful for female sockets, it's
    // better to let AbstractFemaleSocket implement these.
    
    private Base _parent;
    private final FemaleSocketListener _listener;
    private MaleSocket _socket = null;
    private String _name = null;
    private Lock _lock = Lock.NONE;
    
    
    public AbstractFemaleSocket(Base parent, FemaleSocketListener listener, String name) {
        _listener = listener;
        _name = name;
        _parent = parent;
    }
    
    /** {@inheritDoc} */
    @Override
    public Base getParent() {
        return _parent;
    }
    
    /** {@inheritDoc} */
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
    public void connect(MaleSocket socket) throws SocketAlreadyConnectedException {
        if (socket == null) {
            throw new NullPointerException("socket cannot be null");
        }
        
        if (_socket != null) {
            throw new SocketAlreadyConnectedException("Socket is already connected");
        }
        
        if (!isCompatible(socket)) {
            throw new UnsupportedOperationException("Socket is not compatible");
        }
        
        _socket = socket;
        _socket.setParent(this);
        _listener.connected(this);
    }

    /** {@inheritDoc} */
    @Override
    public void disconnect() {
        if (_socket == null) {
            return;
        }
        
        _socket.setParent(null);
        _socket = null;
        _listener.disconnected(this);
    }

    /** {@inheritDoc} */
    @Override
    public MaleSocket getConnectedSocket() {
        return _socket;
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean isConnected() {
        return _socket != null;
    }
    
    /** {@inheritDoc} */
    @Override
    public void setName(String name) {
        _name = name;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return _name;
    }

    /** {@inheritDoc} */
    @Override
    public void dispose() {
        if (_socket != null) {
            _socket.dispose();
        }
    }

    /**
     * Register listeners if this object needs that.
     */
    @Override
    public void registerListeners() {
        if (isConnected()) {
            getConnectedSocket().registerListeners();
        }
    }
    
    /**
     * Register listeners if this object needs that.
     */
    @Override
    public void unregisterListeners() {
        if (isConnected()) {
            getConnectedSocket().unregisterListeners();
        }
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
    public FemaleSocket getChild(int index) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public int getChildCount() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public String getUserName() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void setUserName(String s) throws NamedBean.BadUserNameException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public String getSystemName() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public String getDisplayName() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public String getFullyFormattedDisplayName() {
        throw new UnsupportedOperationException("Not supported.");
    }

    /** {@inheritDoc} */
    @Override
    final public String getFullyFormattedDisplayName(boolean userNameFirst) {
        throw new UnsupportedOperationException("Not supported.");
    }

    // implementing classes will typically have a function/listener to get
    // updates from the layout, which will then call
    //  public void firePropertyChange(String propertyName,
    //             Object oldValue,
    //      Object newValue)
    // _once_ if anything has changed state
    // since we can't do a "super(this)" in the ctor to inherit from PropertyChangeSupport, we'll
    // reflect to it
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    protected final HashMap<PropertyChangeListener, String> register = new HashMap<>();
    protected final HashMap<PropertyChangeListener, String> listenerRefs = new HashMap<>();

    @Override
    @OverridingMethodsMustInvokeSuper
    public synchronized void addPropertyChangeListener(PropertyChangeListener l, String beanRef, String listenerRef) {
        pcs.addPropertyChangeListener(l);
        if (beanRef != null) {
            register.put(l, beanRef);
        }
        if (listenerRef != null) {
            listenerRefs.put(l, listenerRef);
        }
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener l, String beanRef, String listenerRef) {
        pcs.addPropertyChangeListener(propertyName, l);
        if (beanRef != null) {
            register.put(l, beanRef);
        }
        if (listenerRef != null) {
            listenerRefs.put(l, listenerRef);
        }
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
        if (listener != null && !Beans.contains(pcs.getPropertyChangeListeners(), listener)) {
            register.remove(listener);
            listenerRefs.remove(listener);
        }
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
        if (listener != null && !Beans.contains(pcs.getPropertyChangeListeners(), listener)) {
            register.remove(listener);
            listenerRefs.remove(listener);
        }
    }

    @Override
    public synchronized PropertyChangeListener[] getPropertyChangeListenersByReference(String name) {
        ArrayList<PropertyChangeListener> list = new ArrayList<>();
        register.entrySet().forEach((entry) -> {
            PropertyChangeListener l = entry.getKey();
            if (entry.getValue().equals(name)) {
                list.add(l);
            }
        });
        return list.toArray(new PropertyChangeListener[list.size()]);
    }

    /**
     * Get a meaningful list of places where the bean is in use.
     *
     * @return ArrayList of the listeners
     */
    @Override
    public synchronized ArrayList<String> getListenerRefs() {
        return new ArrayList<>(listenerRefs.values());
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public synchronized void updateListenerRef(PropertyChangeListener l, String newName) {
        if (listenerRefs.containsKey(l)) {
            listenerRefs.put(l, newName);
        }
    }

    @Override
    public synchronized String getListenerRef(PropertyChangeListener l) {
        return listenerRefs.get(l);
    }

    /**
     * Get the number of current listeners.
     *
     * @return -1 if the information is not available for some reason.
     */
    @Override
    public synchronized int getNumPropertyChangeListeners() {
        return pcs.getPropertyChangeListeners().length;
    }

    @Override
    public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
        return pcs.getPropertyChangeListeners();
    }

    @Override
    public synchronized PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return pcs.getPropertyChangeListeners(propertyName);
    }

    @Override
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
    }

    @Override
    public void setState(int s) throws JmriException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public int getState() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public String describeState(int state) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public String getComment() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void setComment(String comment) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void setProperty(String key, Object value) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Object getProperty(String key) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void removeProperty(String key) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Set<String> getPropertyKeys() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public String getBeanType() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public int compareSystemNameSuffix(String suffix1, String suffix2, NamedBean n2) {
        throw new UnsupportedOperationException("Not supported.");
    }

}
