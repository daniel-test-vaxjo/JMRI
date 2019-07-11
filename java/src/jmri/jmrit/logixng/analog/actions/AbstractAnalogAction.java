package jmri.jmrit.logixng.analog.actions;

import jmri.InstanceManager;
import jmri.JmriException;
import jmri.Manager;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.implementation.AbstractBase;
import jmri.jmrit.logixng.AnalogActionBean;
import jmri.jmrit.logixng.AnalogActionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The base class for LogixNG AnalogActions
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public abstract class AbstractAnalogAction extends AbstractBase
        implements AnalogActionBean {

    private Base _parent = null;
    private Lock _lock = Lock.NONE;
    private int _state = AnalogActionBean.UNKNOWN;


    public AbstractAnalogAction(String sys) throws BadSystemNameException {
        super(sys);
        
        // Do this test here to ensure all the tests are using correct system names
        Manager.NameValidity isNameValid = InstanceManager.getDefault(AnalogActionManager.class).validSystemNameFormat(mSystemName);
        if (isNameValid != Manager.NameValidity.VALID) {
            throw new IllegalArgumentException("system name is not valid");
        }
    }

    public AbstractAnalogAction(String sys, String user)
            throws BadUserNameException, BadSystemNameException {
        super(sys, user);
        
        // Do this test here to ensure all the tests are using correct system names
        Manager.NameValidity isNameValid = InstanceManager.getDefault(AnalogActionManager.class).validSystemNameFormat(mSystemName);
        if (isNameValid != Manager.NameValidity.VALID) {
            throw new IllegalArgumentException("system name is not valid");
        }
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

    @Override
    public String getBeanType() {
        return Bundle.getMessage("BeanNameAnalogAction");
    }

    @Override
    public void setState(int s) throws JmriException {
        log.warn("Unexpected call to setState in AbstractAnalogAction.");  // NOI18N
        _state = s;
    }

    @Override
    public int getState() {
        log.warn("Unexpected call to getState in AbstractAnalogAction.");  // NOI18N
        return _state;
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
    
    
    private final static Logger log = LoggerFactory.getLogger(AbstractAnalogAction.class);
}
