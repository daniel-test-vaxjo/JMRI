package jmri.jmrit.logixng.digital.expressions;

import jmri.InstanceManager;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.DigitalActionManager;
import jmri.jmrit.logixng.FemaleSocket;
import jmri.jmrit.logixng.FemaleSocketListener;
import jmri.jmrit.logixng.SocketAlreadyConnectedException;
import jmri.jmrit.logixng.DigitalExpressionManager;
import jmri.jmrit.logixng.FemaleDigitalExpressionSocket;
import jmri.jmrit.logixng.MaleDigitalExpressionSocket;
import jmri.jmrit.logixng.MaleSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An Expression that returns True only once while its child expression returns
 * True.
 * 
 * The first time the child expression returns True, this expression returns
 * True. After that, this expression returns False until the child expression
 * returns False and again returns True.
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public class TriggerOnce extends AbstractDigitalExpression implements FemaleSocketListener {

    private TriggerOnce _template;
    private String _childExpressionSystemName;
    private final FemaleDigitalExpressionSocket _childExpression;
    private boolean _childLastState = false;
    
    public TriggerOnce() {
        
        super(InstanceManager.getDefault(DigitalExpressionManager.class).getNewSystemName());
        
        _childExpression = InstanceManager.getDefault(DigitalExpressionManager.class)
                .createFemaleSocket(this, this, "E1");
    }
    
    public TriggerOnce(String sys, String user) {
        
        super(sys, user);
        
        _childExpression = InstanceManager.getDefault(DigitalExpressionManager.class)
                .createFemaleSocket(this, this, "E1");
    }
    
    public TriggerOnce(String sys, String user, MaleDigitalExpressionSocket expression) {
        
        super(sys, user);
        
        _childExpression = InstanceManager.getDefault(DigitalExpressionManager.class)
                .createFemaleSocket(this, this, "E1");
        
        try {
            _childExpression.connect(expression);
        } catch (SocketAlreadyConnectedException ex) {
            log.error("socket is already connected", ex);
        }
    }
    
    private TriggerOnce(TriggerOnce template, String sys) {
        super(sys);
        _template = template;
        _childExpression = InstanceManager.getDefault(DigitalExpressionManager.class)
                .createFemaleSocket(this, this, _template._childExpression.getName());
    }
    
    /** {@inheritDoc} */
    @Override
    public Base getNewObjectBasedOnTemplate(String sys) {
        return new TriggerOnce(this, sys);
    }
    
    /** {@inheritDoc} */
    @Override
    public Category getCategory() {
        return Category.OTHER;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isExternal() {
        return false;
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean evaluate() {
        if (_childExpression.evaluate() && !_childLastState) {
            _childLastState = true;
            return true;
        }
        _childLastState = _childExpression.evaluate();
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void reset() {
        _childLastState = false;
    }

    @Override
    public FemaleSocket getChild(int index) throws IllegalArgumentException, UnsupportedOperationException {
        if (index == 0) {
            return _childExpression;
        } else {
                throw new IllegalArgumentException(
                        String.format("index has invalid value: %d", index));
        }
    }

    @Override
    public int getChildCount() {
        return 1;
    }
    
    @Override
    public void connected(FemaleSocket socket) {
        if (socket == _childExpression) {
            _childExpressionSystemName = socket.getConnectedSocket().getSystemName();
        } else {
            throw new IllegalArgumentException("unkown socket");
        }
    }
    
    @Override
    public void disconnected(FemaleSocket socket) {
        if (socket == _childExpression) {
            _childExpressionSystemName = null;
        } else {
            throw new IllegalArgumentException("unkown socket");
        }
    }

    @Override
    public String getShortDescription() {
        return Bundle.getMessage("TriggerOnce_Short");
    }
    
    @Override
    public String getLongDescription() {
        return Bundle.getMessage("TriggerOnce_Long");
    }

    public void setAnalogActionSocketSystemName(String systemName) {
        _childExpressionSystemName = systemName;
    }

    /** {@inheritDoc} */
    @Override
    public void setup() {
        try {
            if ( !_childExpression.isConnected()
                    || !_childExpression.getConnectedSocket().getSystemName()
                            .equals(_childExpressionSystemName)) {
                
                String socketSystemName = _childExpressionSystemName;
                _childExpression.disconnect();
                if (socketSystemName != null) {
                    MaleSocket maleSocket =
                            InstanceManager.getDefault(DigitalExpressionManager.class)
                                    .getBeanBySystemName(socketSystemName);
                    if (maleSocket != null) {
                        _childExpression.connect(maleSocket);
                        maleSocket.setup();
                    } else {
                        log.error("cannot load digital expression " + socketSystemName);
                    }
                }
            } else {
                _childExpression.getConnectedSocket().setup();
            }
        } catch (SocketAlreadyConnectedException ex) {
            // This shouldn't happen and is a runtime error if it does.
            throw new RuntimeException("socket is already connected");
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void registerListenersForThisClass() {
    }
    
    /** {@inheritDoc} */
    @Override
    public void unregisterListenersForThisClass() {
    }
    
    /** {@inheritDoc} */
    @Override
    public void disposeMe() {
    }

    private final static Logger log = LoggerFactory.getLogger(TriggerOnce.class);

}
