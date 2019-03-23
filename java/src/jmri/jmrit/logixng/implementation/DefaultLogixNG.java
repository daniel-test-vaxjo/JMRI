package jmri.jmrit.logixng.implementation;

import static jmri.NamedBean.UNKNOWN;

import java.util.SortedSet;
import jmri.InstanceManager;
import jmri.JmriException;
import jmri.implementation.AbstractNamedBean;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.FemaleSocket;
import jmri.jmrit.logixng.FemaleSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jmri.jmrit.logixng.LogixNG;
import jmri.jmrit.logixng.LogixNG_Manager;
import jmri.jmrit.logixng.DigitalAction;
import jmri.jmrit.logixng.DigitalActionManager;
import jmri.jmrit.logixng.FemaleDigitalActionSocket;
import jmri.jmrit.logixng.MaleDigitalActionSocket;
import jmri.jmrit.logixng.MaleSocket;

/**
 * The default implementation of LogixNG.
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public final class DefaultLogixNG extends AbstractNamedBean
        implements LogixNG, FemaleSocketListener {
    
    private Base _parent = null;
    private final FemaleDigitalActionSocket _femaleActionSocket;
    private boolean _enabled = false;
    private boolean _userEnabled = false;
    
    public DefaultLogixNG(String sys, String user) throws BadUserNameException, BadSystemNameException  {
        super(sys, user);
        _femaleActionSocket = InstanceManager.getDefault(DigitalActionManager.class).createFemaleActionSocket(this, this, "");
    }
    
    public DefaultLogixNG(String sys, String user, MaleDigitalActionSocket action) throws BadUserNameException, BadSystemNameException  {
        super(sys, user);
        _femaleActionSocket = InstanceManager.getDefault(DigitalActionManager.class).createFemaleActionSocket(this, this, "", action);
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
    public FemaleSocket getFemaleSocket() {
        return _femaleActionSocket;
    }
    
    /** {@inheritDoc} */
    @Override
    public void execute() {
        if (isEnabled()) {
            _femaleActionSocket.executeStart();
        }
//        if (_enabled) {
//            _enabled = _femaleActionSocket.executeStart();
//        } else {
//            _enabled = _femaleActionSocket.executeContinue();
//        }
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
    public void connected(FemaleSocket socket) {
        // Do nothing
    }

    @Override
    public void disconnected(FemaleSocket socket) {
        // Do nothing
    }

    @Override
    public String getShortDescription() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public String getLongDescription() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public FemaleSocket getChild(int index) throws IllegalArgumentException, UnsupportedOperationException {
        if (index != 0) {
            throw new IllegalArgumentException(
                    String.format("index has invalid value: %d", index));
        }
        
        return _femaleActionSocket;
    }

    @Override
    public int getChildCount() {
        return 1;
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
    final public void dispose() {
        _femaleActionSocket.dispose();
    }
    
    /** {@inheritDoc} */
    @Override
    public void setEnabled(boolean enable) {
        _enabled = enable;
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean isEnabled() {
        return _enabled && _userEnabled;
//        return _enabled && _userEnabled && _parent.isEnabled();
    }
    
    /**
     * Set whenether this object is enabled or disabled by the user.
     * 
     * @param enable true if this object should be enabled, false otherwise
     */
    public void setUserEnabled(boolean enable) {
        _userEnabled = enable;
    }
    
    /**
     * Determines whether this object is enabled by the user.
     * 
     * @return true if the object is enabled, false otherwise
     */
    public boolean isUserEnabled() {
        return _userEnabled;
    }

}
