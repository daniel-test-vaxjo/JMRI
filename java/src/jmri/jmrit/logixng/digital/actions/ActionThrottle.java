package jmri.jmrit.logixng.digital.actions;

import jmri.DccThrottle;
import jmri.InstanceManager;
import jmri.LocoAddress;
import jmri.ThrottleListener;
import jmri.ThrottleManager;
import jmri.jmris.AbstractThrottleServer;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.FemaleSocket;
import jmri.jmrit.logixng.FemaleSocketListener;
import jmri.jmrit.logixng.AnalogExpressionManager;
import jmri.jmrit.logixng.DigitalActionManager;
import jmri.jmrit.logixng.DigitalExpressionManager;
import jmri.jmrit.logixng.FemaleAnalogExpressionSocket;
import jmri.jmrit.logixng.FemaleDigitalExpressionSocket;
import jmri.jmrit.logixng.MaleAnalogExpressionSocket;
import jmri.jmrit.logixng.MaleSocket;
import jmri.jmrit.logixng.SocketAlreadyConnectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runs an engine.
 * This action reads an analog expression with the loco address and sets its
 * speed according to an alaog expression and the direction according to a
 * digital expression.
 * 
 * @author Daniel Bergqvist Copyright 2019
 */
public class ActionThrottle extends AbstractDigitalAction
        implements FemaleSocketListener {

    private ActionThrottle _template;
    private DccThrottle _throttle;
    private ThrottleListener _throttleListener;
    private String _locoAddressSocketSystemName;
    private String _locoSpeedSocketSystemName;
    private String _locoDirectionSocketSystemName;
    private final FemaleAnalogExpressionSocket _locoAddressSocket;
    private final FemaleAnalogExpressionSocket _locoSpeedSocket;
    private final FemaleDigitalExpressionSocket _locoDirectionSocket;
    long _delay = 0;
    boolean _isActive = false;
    
    /**
     * Create a new instance of ActionIfThen and generate a new system name.
     */
    public ActionThrottle() {
        super(InstanceManager.getDefault(DigitalActionManager.class).getNewSystemName());
        _locoAddressSocket = InstanceManager.getDefault(AnalogExpressionManager.class)
                .createFemaleSocket(this, this, "E1");
        _locoSpeedSocket = InstanceManager.getDefault(AnalogExpressionManager.class)
                .createFemaleSocket(this, this, "E2");
        _locoDirectionSocket = InstanceManager.getDefault(DigitalExpressionManager.class)
                .createFemaleSocket(this, this, "E3");
    }
    
    public ActionThrottle(String sys) {
        super(sys);
        _locoAddressSocket = InstanceManager.getDefault(AnalogExpressionManager.class)
                .createFemaleSocket(this, this, "E1");
        _locoSpeedSocket = InstanceManager.getDefault(AnalogExpressionManager.class)
                .createFemaleSocket(this, this, "E2");
        _locoDirectionSocket = InstanceManager.getDefault(DigitalExpressionManager.class)
                .createFemaleSocket(this, this, "E3");
    }
    
    public ActionThrottle(String sys, String user) {
        super(sys, user);
        _locoAddressSocket = InstanceManager.getDefault(AnalogExpressionManager.class)
                .createFemaleSocket(this, this, "E1");
        _locoSpeedSocket = InstanceManager.getDefault(AnalogExpressionManager.class)
                .createFemaleSocket(this, this, "E2");
        _locoDirectionSocket = InstanceManager.getDefault(DigitalExpressionManager.class)
                .createFemaleSocket(this, this, "E3");
    }
    
    private ActionThrottle(ActionThrottle template, String sys) {
        super(sys);
        _template = template;
        _locoAddressSocket = InstanceManager.getDefault(AnalogExpressionManager.class)
                .createFemaleSocket(this, this, _template._locoAddressSocket.getName());
        _locoSpeedSocket = InstanceManager.getDefault(AnalogExpressionManager.class)
                .createFemaleSocket(this, this, _template._locoAddressSocket.getName());
        _locoDirectionSocket = InstanceManager.getDefault(DigitalExpressionManager.class)
                .createFemaleSocket(this, this, _template._locoAddressSocket.getName());
    }
    
    /** {@inheritDoc} */
    @Override
    public Base getNewObjectBasedOnTemplate(String sys) {
        return new ActionThrottle(this, sys);
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
    public void execute() {
        
        int newLocoAddress;
        
        if (_locoAddressSocket.isConnected()) {
            newLocoAddress =
                    (int) ((MaleAnalogExpressionSocket)_locoAddressSocket.getConnectedSocket())
                            .evaluate();
            
        } else {
            newLocoAddress = -1;
        }
        
        if (((newLocoAddress != -1) && (_throttle == null))
                || (newLocoAddress != _throttle.getLocoAddress().getNumber())) {
            
            if (_throttle != null) {
                InstanceManager.getDefault(ThrottleManager.class).releaseThrottle(_throttle, _throttleListener);
            }
            
            if (newLocoAddress != -1) {
                
                _throttleListener =  new ThrottleListener() {
                    @Override
                    @Deprecated
                    public void notifyStealThrottleRequired(LocoAddress address) {
//                        InstanceManager.throttleManagerInstance().responseThrottleDecision(address, this, DecisionType.STEAL );
                        log.warn("Loco {} cannot be aquired. Decision required.", address.getNumber());
                    }

                    @Override
                    public void notifyThrottleFound(DccThrottle t) {
                        _throttle = t;
//                        t.addPropertyChangeListener();
                    }

                    @Override
                    public void notifyFailedThrottleRequest(LocoAddress address, String reason) {
                        log.warn("loco {} cannot be aquired", address.getNumber());
                    }

                    @Override
                    public void notifyDecisionRequired(LocoAddress address, ThrottleListener.DecisionType question) {
                        log.warn("Loco {} cannot be aquired. Decision required.", address.getNumber());
                    }
                };
                
                boolean result = InstanceManager.getDefault(ThrottleManager.class)
                        .requestThrottle(newLocoAddress, _throttleListener);
                
                if (!result) {
                    log.warn("loco {} cannot be aquired", newLocoAddress);
                }
            }
            
        }
        
//        jmri.jmrit.throttle.ControlPanel b;
        
//        jmri.jmrit.automat.AbstractAutomaton a;
        
        
        
    }

    /**
     * Get the type.
     */
    public long getDelay() {
        return _delay;
    }
    
    /**
     * Set the type.
     */
    public void setDelay(long delay) {
        _delay = delay;
    }
    
    @Override
    public FemaleSocket getChild(int index) throws IllegalArgumentException, UnsupportedOperationException {
        switch (index) {
            case 0:
                return _locoAddressSocket;
                
            default:
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
        if (socket == _locoAddressSocket) {
            _locoAddressSocketSystemName = socket.getConnectedSocket().getSystemName();
        } else if (socket == _locoSpeedSocket) {
            _locoSpeedSocketSystemName = socket.getConnectedSocket().getSystemName();
        } else if (socket == _locoDirectionSocket) {
            _locoDirectionSocketSystemName = socket.getConnectedSocket().getSystemName();
        } else {
            throw new IllegalArgumentException("unkown socket");
        }
    }

    @Override
    public void disconnected(FemaleSocket socket) {
        if (socket == _locoAddressSocket) {
            _locoAddressSocketSystemName = null;
        } else if (socket == _locoSpeedSocket) {
            _locoSpeedSocketSystemName = null;
        } else if (socket == _locoDirectionSocket) {
            _locoDirectionSocketSystemName = null;
        } else {
            throw new IllegalArgumentException("unkown socket");
        }
    }

    @Override
    public String getShortDescription() {
        return Bundle.getMessage("Throttle_Short");
    }

    @Override
    public String getLongDescription() {
        return Bundle.getMessage("Throttle_Long", _locoAddressSocket.getName(), _delay);
    }

    public FemaleAnalogExpressionSocket getThenActionSocket() {
        return _locoAddressSocket;
    }

    public String getTimerActionSocketSystemName() {
        return _locoAddressSocketSystemName;
    }

    public void setTimerActionSocketSystemName(String systemName) {
        _locoAddressSocketSystemName = systemName;
    }

    /** {@inheritDoc} */
    @Override
    public void setup() {
        try {
            if ( !_locoAddressSocket.isConnected()
                    || !_locoAddressSocket.getConnectedSocket().getSystemName()
                            .equals(_locoAddressSocketSystemName)) {
                
                String socketSystemName = _locoAddressSocketSystemName;
                _locoAddressSocket.disconnect();
                if (socketSystemName != null) {
                    MaleSocket maleSocket =
                            InstanceManager.getDefault(DigitalActionManager.class)
                                    .getBeanBySystemName(socketSystemName);
                    _locoAddressSocket.disconnect();
                    if (maleSocket != null) {
                        _locoAddressSocket.connect(maleSocket);
                        maleSocket.setup();
                    } else {
                        log.error("cannot load analog expression " + socketSystemName);
                    }
                }
            } else {
                _locoAddressSocket.getConnectedSocket().setup();
            }
            
            if ( !_locoSpeedSocket.isConnected()
                    || !_locoSpeedSocket.getConnectedSocket().getSystemName()
                            .equals(_locoSpeedSocketSystemName)) {
                
                String socketSystemName = _locoSpeedSocketSystemName;
                _locoSpeedSocket.disconnect();
                if (socketSystemName != null) {
                    MaleSocket maleSocket =
                            InstanceManager.getDefault(DigitalActionManager.class)
                                    .getBeanBySystemName(socketSystemName);
                    _locoSpeedSocket.disconnect();
                    if (maleSocket != null) {
                        _locoSpeedSocket.connect(maleSocket);
                        maleSocket.setup();
                    } else {
                        log.error("cannot load analog expression " + socketSystemName);
                    }
                }
            } else {
                _locoSpeedSocket.getConnectedSocket().setup();
            }
            
            if ( !_locoDirectionSocket.isConnected()
                    || !_locoDirectionSocket.getConnectedSocket().getSystemName()
                            .equals(_locoDirectionSocketSystemName)) {
                
                String socketSystemName = _locoDirectionSocketSystemName;
                _locoDirectionSocket.disconnect();
                if (socketSystemName != null) {
                    MaleSocket maleSocket =
                            InstanceManager.getDefault(DigitalActionManager.class)
                                    .getBeanBySystemName(socketSystemName);
                    _locoDirectionSocket.disconnect();
                    if (maleSocket != null) {
                        _locoDirectionSocket.connect(maleSocket);
                        maleSocket.setup();
                    } else {
                        log.error("cannot load digital expression " + socketSystemName);
                    }
                }
            } else {
                _locoDirectionSocket.getConnectedSocket().setup();
            }
        } catch (SocketAlreadyConnectedException ex) {
            // This shouldn't happen and is a runtime error if it does.
            throw new RuntimeException("socket is already connected");
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void registerListenersForThisClass() {
        // Do nothing
    }
    
    /** {@inheritDoc} */
    @Override
    public void unregisterListenersForThisClass() {
        // Do nothing
    }
    
    /** {@inheritDoc} */
    @Override
    public void disposeMe() {
        InstanceManager.getDefault(ThrottleManager.class)
                .releaseThrottle(_throttle, _throttleListener);
    }

    private final static Logger log = LoggerFactory.getLogger(ActionThrottle.class);

}
