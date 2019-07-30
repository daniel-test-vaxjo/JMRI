package jmri.jmrit.logixng.digital.expressions;

import java.util.concurrent.atomic.AtomicBoolean;
import jmri.InstanceManager;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.ConditionalNG;
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
 * This Expression has two expressions, the primary expression and the secondary
 * expression. When the primary expression becomes True after have been False,
 * the secondary expression is reset.
 * 
 * The result of the evaluation of this expression is True if both the
 * expressions evaluates to True.
 * 
 * This expression is used for example if one expression should trigger a timer.
 * If the primary expression is a sensor having a certain state and the secondary
 * expression is a timer, this expression will evaluate to True if the sensor
 * has had that state during the specified time.
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public class ResetOnTrue extends AbstractDigitalExpression implements FemaleSocketListener {

    private ResetOnTrue _template;
    private String _primaryExpressionSocketSystemName;
    private String _secondaryExpressionSocketSystemName;
    private final FemaleDigitalExpressionSocket _primaryExpressionSocket;
    private final FemaleDigitalExpressionSocket _secondaryExpressionSocket;
    private boolean _lastMainResult = false;
    
    public ResetOnTrue() {
        
        super(InstanceManager.getDefault(DigitalExpressionManager.class).getNewSystemName());
        
        _primaryExpressionSocket = InstanceManager.getDefault(DigitalExpressionManager.class)
                .createFemaleSocket(this, this, "E1");
        _secondaryExpressionSocket = InstanceManager.getDefault(DigitalExpressionManager.class)
                .createFemaleSocket(this, this, "E2");
    }
    
    public ResetOnTrue(String sys, String user) {
        
        super(sys, user);
        
        _primaryExpressionSocket = InstanceManager.getDefault(DigitalExpressionManager.class)
                .createFemaleSocket(this, this, "E1");
        _secondaryExpressionSocket = InstanceManager.getDefault(DigitalExpressionManager.class)
                .createFemaleSocket(this, this, "E2");
    }
    
    public ResetOnTrue(String sys, String user,
            MaleDigitalExpressionSocket primaryExpression,
            MaleDigitalExpressionSocket secondaryExpression) {
        
        super(sys, user);
        
        _primaryExpressionSocket = InstanceManager.getDefault(DigitalExpressionManager.class)
                .createFemaleSocket(this, this, "E1");
        _secondaryExpressionSocket = InstanceManager.getDefault(DigitalExpressionManager.class)
                .createFemaleSocket(this, this, "E2");
        
        try {
            _primaryExpressionSocket.connect(primaryExpression);
            _secondaryExpressionSocket.connect(secondaryExpression);
        } catch (SocketAlreadyConnectedException ex) {
            log.error("socket is already connected", ex);
        }
    }
    
    private ResetOnTrue(ResetOnTrue template, String sys) {
        super(sys);
        _template = template;
        _primaryExpressionSocket = InstanceManager.getDefault(DigitalExpressionManager.class)
                .createFemaleSocket(this, this, _template._primaryExpressionSocket.getName());
        _secondaryExpressionSocket = InstanceManager.getDefault(DigitalExpressionManager.class)
                .createFemaleSocket(this, this, _template._secondaryExpressionSocket.getName());
    }
    
    /** {@inheritDoc} */
    @Override
    public Base getNewObjectBasedOnTemplate(String sys) {
        return new ResetOnTrue(this, sys);
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
        boolean result = _primaryExpressionSocket.evaluate();
        if (!_lastMainResult && result) {
            _secondaryExpressionSocket.reset();
        }
        _lastMainResult = result;
        result |= _secondaryExpressionSocket.evaluate();
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public void reset() {
        _primaryExpressionSocket.reset();
        _secondaryExpressionSocket.reset();
    }

    @Override
    public FemaleSocket getChild(int index) throws IllegalArgumentException, UnsupportedOperationException {
        switch (index) {
            case 0:
                return _primaryExpressionSocket;
                
            case 1:
                return _secondaryExpressionSocket;
                
            default:
                throw new IllegalArgumentException(
                        String.format("index has invalid value: %d", index));
        }
    }

    @Override
    public int getChildCount() {
        return 2;
    }

    @Override
    public String getShortDescription() {
        return Bundle.getMessage("ResetOnTrue_Short");
    }
    
    @Override
    public String getLongDescription() {
        return Bundle.getMessage("ResetOnTrue_Long");
    }

    @Override
    public void connected(FemaleSocket socket) {
        if (socket == _primaryExpressionSocket) {
            _primaryExpressionSocketSystemName = socket.getConnectedSocket().getSystemName();
        } else if (socket == _secondaryExpressionSocket) {
            _secondaryExpressionSocketSystemName = socket.getConnectedSocket().getSystemName();
        } else {
            throw new IllegalArgumentException("unkown socket");
        }
    }

    @Override
    public void disconnected(FemaleSocket socket) {
        if (socket == _primaryExpressionSocket) {
            _primaryExpressionSocketSystemName = null;
        } else if (socket == _secondaryExpressionSocket) {
            _secondaryExpressionSocketSystemName = null;
        } else {
            throw new IllegalArgumentException("unkown socket");
        }
    }

    public String getPrimaryExpressionSocketSystemName() {
        return _primaryExpressionSocketSystemName;
    }

    public void setPrimaryExpressionSocketSystemName(String systemName) {
        _primaryExpressionSocketSystemName = systemName;
    }

    public String getSecondaryExpressionSocketSystemName() {
        return _secondaryExpressionSocketSystemName;
    }

    public void setSecondaryExpressionSocketSystemName(String systemName) {
        _secondaryExpressionSocketSystemName = systemName;
    }

    /** {@inheritDoc} */
    @Override
    public void setup() {
        try {
            if ( !_primaryExpressionSocket.isConnected()
                    || !_primaryExpressionSocket.getConnectedSocket().getSystemName()
                            .equals(_primaryExpressionSocketSystemName)) {
                
                String socketSystemName = _primaryExpressionSocketSystemName;
                _primaryExpressionSocket.disconnect();
                if (socketSystemName != null) {
                    MaleSocket maleSocket =
                            InstanceManager.getDefault(DigitalExpressionManager.class)
                                    .getBeanBySystemName(socketSystemName);
                    if (maleSocket != null) {
                        _primaryExpressionSocket.connect(maleSocket);
                        maleSocket.setup();
                    } else {
                        log.error("cannot load digital expression " + socketSystemName);
                    }
                }
            } else {
                _primaryExpressionSocket.getConnectedSocket().setup();
            }
            
            if ( !_secondaryExpressionSocket.isConnected()
                    || !_secondaryExpressionSocket.getConnectedSocket().getSystemName()
                            .equals(_secondaryExpressionSocketSystemName)) {
                
                String socketSystemName = _secondaryExpressionSocketSystemName;
                _secondaryExpressionSocket.disconnect();
                if (socketSystemName != null) {
                    MaleSocket maleSocket =
                            InstanceManager.getDefault(DigitalExpressionManager.class)
                                    .getBeanBySystemName(socketSystemName);
                    _secondaryExpressionSocket.disconnect();
                    if (maleSocket != null) {
                        _secondaryExpressionSocket.connect(maleSocket);
                        maleSocket.setup();
                    } else {
                        log.error("cannot load digital action " + socketSystemName);
                    }
                }
            } else {
                _secondaryExpressionSocket.getConnectedSocket().setup();
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

    private final static Logger log = LoggerFactory.getLogger(ResetOnTrue.class);
}
