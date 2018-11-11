package jmri.jmrit.newlogix.engine;

import static jmri.NamedBean.UNKNOWN;

import java.util.SortedSet;
import jmri.InstanceManager;
import jmri.JmriException;
import jmri.implementation.AbstractNamedBean;
import jmri.jmrit.newlogix.Action;
import jmri.jmrit.newlogix.ActionManager;
import jmri.jmrit.newlogix.FemaleActionSocket;
import jmri.jmrit.newlogix.FemaleSocket;
import jmri.jmrit.newlogix.FemaleSocketListener;
import jmri.jmrit.newlogix.MaleActionSocket;
import jmri.jmrit.newlogix.NewLogix;
import jmri.jmrit.newlogix.NewLogixManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default implementation of NewLogix.
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public final class DefaultNewLogix extends AbstractNamedBean
        implements NewLogix, FemaleSocketListener {
    
//    private Action _action;
    private final FemaleActionSocket _femaleActionSocket;
    private boolean _enabled = false;
    
    public DefaultNewLogix(String sys, String user) throws BadUserNameException, BadSystemNameException  {
        super(sys, user);
        _femaleActionSocket = InstanceManager.getDefault(ActionManager.class).createFemaleActionSocket(this, "");
    }

    public DefaultNewLogix(String sys, String user, MaleActionSocket action) throws BadUserNameException, BadSystemNameException  {
        super(sys, user);
        _femaleActionSocket = InstanceManager.getDefault(ActionManager.class).createFemaleActionSocket(this, "", action);
    }
    
    /** {@inheritDoc} */
    @Override
    public FemaleSocket getFemaleSocket() {
        return _femaleActionSocket;
    }
    
    /** {@inheritDoc} */
    @Override
    public void execute() {
        if (_enabled) {
            _enabled = _femaleActionSocket.executeStart();
        } else {
            _enabled = _femaleActionSocket.executeContinue();
        }
    }

    @Override
    public String getBeanType() {
        return Bundle.getMessage("BeanNameNewLogix");
    }

    @Override
    public void setState(int s) throws JmriException {
        log.warn("Unexpected call to setState in DefaultNewLogix.");  // NOI18N
    }

    @Override
    public int getState() {
        log.warn("Unexpected call to getState in DefaultNewLogix.");  // NOI18N
        return UNKNOWN;
    }

    /**
     * Set enabled status. Enabled is a bound property All conditionals are set
     * to UNKNOWN state and recalculated when the Logix is enabled, provided the
     * Logix has been previously activated.
     */
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
        }
    }

    /**
     * Get enabled status
     */
    @Override
    public boolean getEnabled() {
        return _enabled;
    }

    private final static Logger log = LoggerFactory.getLogger(DefaultNewLogix.class);

    @Override
    public void connected(FemaleSocket socket) {
        // Do nothing
    }

    @Override
    public void disconnected(FemaleSocket socket) {
        // Do nothing
    }
}
