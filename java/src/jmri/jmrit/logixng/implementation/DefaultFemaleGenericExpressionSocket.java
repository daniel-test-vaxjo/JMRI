package jmri.jmrit.logixng.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import jmri.InstanceManager;
import jmri.jmrit.logixng.AnalogExpressionManager;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.DigitalExpressionManager;
import jmri.jmrit.logixng.FemaleAnalogExpressionSocket;
import jmri.jmrit.logixng.FemaleDigitalExpressionSocket;
import jmri.jmrit.logixng.FemaleGenericExpressionSocket;
import jmri.jmrit.logixng.FemaleSocket;
import jmri.jmrit.logixng.FemaleStringExpressionSocket;
import jmri.jmrit.logixng.FemaleSocketListener;
import jmri.jmrit.logixng.MaleAnalogExpressionSocket;
import jmri.jmrit.logixng.MaleDigitalExpressionSocket;
import jmri.jmrit.logixng.MaleStringExpressionSocket;
import jmri.jmrit.logixng.MaleSocket;
import jmri.jmrit.logixng.SocketAlreadyConnectedException;
import jmri.jmrit.logixng.StringExpressionManager;
import jmri.jmrit.logixng.analog.implementation.DefaultFemaleAnalogExpressionSocket;
import jmri.jmrit.logixng.digital.implementation.DefaultFemaleDigitalExpressionSocket;
import jmri.jmrit.logixng.string.implementation.DefaultFemaleStringExpressionSocket;
import jmri.util.TypeConversionUtil;

/**
 *
 */
public class DefaultFemaleGenericExpressionSocket
        extends AbstractFemaleSocket
        implements FemaleGenericExpressionSocket, FemaleSocketListener {

    private SocketType _socketType;             // The type of the socket the user has selected
    private SocketType _currentSocketType;      // The current type of the socket.
    private FemaleSocket internalSocket;        //
    private FemaleSocket _currentActiveSocket;  // The socket that is currently in use, if any. Null otherwise.
    private final FemaleAnalogExpressionSocket _analogSocket = new DefaultFemaleAnalogExpressionSocket(this, this, "A");
    private final FemaleDigitalExpressionSocket _digitalSocket = new DefaultFemaleDigitalExpressionSocket(this, this, "D");
    private final FemaleStringExpressionSocket _stringSocket = new DefaultFemaleStringExpressionSocket(this, this, "S");
    private boolean _do_i18n;
    
    public DefaultFemaleGenericExpressionSocket(
            SocketType socketType,
            Base parent,
            FemaleSocketListener listener,
            String name) {
        
        super(parent, listener, name);
        
        _socketType = socketType;
        _currentSocketType = socketType;
        
        switch (_socketType) {
            case ANALOG:
                _currentActiveSocket = _analogSocket;
                break;
                
            case DIGITAL:
                _currentActiveSocket = _digitalSocket;
                break;
                
            case STRING:
                _currentActiveSocket = _stringSocket;
                break;
                
            case GENERIC:
                _currentActiveSocket = null;
                break;
                
            default:
                throw new RuntimeException("_socketType has invalid value: "+socketType.name());
        }
    }
    
    
    /** {@inheritDoc} */
    @Override
    public FemaleSocket getCurrentActiveSocket() {
        return _currentActiveSocket;
    }
    
    
    public FemaleAnalogExpressionSocket getAnalogSocket() {
        if (internalSocket != null) {
            throw new RuntimeException("internal socket cannot be set more than once");
        }
        FemaleAnalogExpressionSocket socket = new AnalogSocket();
        internalSocket = socket;
        return socket;
    }
    
    
    public FemaleDigitalExpressionSocket getDigitalSocket() {
        if (internalSocket != null) {
            throw new RuntimeException("internal socket cannot be set more than once");
        }
        FemaleDigitalExpressionSocket socket = new DigitalSocket();
        internalSocket = socket;
        return socket;
    }
    
    
    public FemaleStringExpressionSocket getStringSocket() {
        if (internalSocket != null) {
            throw new RuntimeException("internal socket cannot be set more than once");
        }
        FemaleStringExpressionSocket socket = new StringSocket();
        internalSocket = socket;
        return socket;
    }
    
    
    /** {@inheritDoc} */
    @Override
    public Base getNewObjectBasedOnTemplate(String sys) {
        // Female sockets have special handling
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean isCompatible(MaleSocket socket) {
        return (socket instanceof MaleAnalogExpressionSocket)
                || (socket instanceof MaleDigitalExpressionSocket)
                || (socket instanceof MaleStringExpressionSocket);
    }
    
    /** {@inheritDoc} */
    @Override
    public void setSocketType(SocketType socketType)
            throws SocketAlreadyConnectedException {
        
        if (socketType == _socketType) {
            return;
        }
        
        switch (socketType) {
            case DIGITAL:
                if ((_currentActiveSocket == null)
                        || (_currentActiveSocket == _digitalSocket)) {
                    
                    _socketType = SocketType.DIGITAL;
                    _currentSocketType = SocketType.DIGITAL;
                    _currentActiveSocket = _digitalSocket;
                } else {
                    throw new SocketAlreadyConnectedException("Socket is already connected");
                }
                break;
                
            case ANALOG:
                if ((_currentActiveSocket == null)
                        || (_currentActiveSocket == _analogSocket)) {
                    
                    _socketType = SocketType.ANALOG;
                    _currentSocketType = SocketType.ANALOG;
                    _currentActiveSocket = _analogSocket;
                } else {
                    throw new SocketAlreadyConnectedException("Socket is already connected");
                }
                break;
                
            case STRING:
                if ((_currentActiveSocket == null)
                        || (_currentActiveSocket == _stringSocket)) {
                    
                    _socketType = SocketType.STRING;
                    _currentSocketType = SocketType.STRING;
                    _currentActiveSocket = _stringSocket;
                } else {
                    throw new SocketAlreadyConnectedException("Socket is already connected");
                }
                break;
                
            case GENERIC:
                _socketType = SocketType.GENERIC;
                _currentSocketType = SocketType.GENERIC;
                
                if ((_currentActiveSocket != null)
                        && !_currentActiveSocket.isConnected()) {
                    
                    _currentActiveSocket = null;
                }
                break;
                
            default:
                throw new RuntimeException("socketType has invalid value: "+socketType.name());
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public SocketType getSocketType() {
        return _socketType;
    }
    
    public void setDoI18N(boolean do_i18n) {
        _do_i18n = do_i18n;
    }
    
    public boolean getDoI18N() {
        return _do_i18n;
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean evaluateBoolean() {
//        System.err.format("evaluateBoolean: %b%n", isConnected());
        if (isConnected()) {
//            System.err.format("socket type: %s%n", _currentSocketType);
//            System.err.format("socket type: %s%n", _currentSocketType.name());
            if (_currentSocketType == SocketType.DIGITAL) {
                return ((MaleDigitalExpressionSocket)getConnectedSocket()).evaluate();
            } else {
                return TypeConversionUtil.convertToBoolean(evaluateBoolean(), _do_i18n);
            }
        } else {
            return false;
        }
    }

    @Override
    public double evaluateDouble() {
        if (isConnected()) {
            if (_currentSocketType == SocketType.ANALOG) {
                return ((MaleAnalogExpressionSocket)getConnectedSocket()).evaluate();
            } else {
                return TypeConversionUtil.convertToDouble(evaluateDouble(), _do_i18n);
            }
        } else {
            return 0.0f;
        }
    }

    @Override
    public String evaluateString() {
        if (isConnected()) {
            if (_currentSocketType == SocketType.STRING) {
                return ((MaleStringExpressionSocket)getConnectedSocket()).evaluate();
            } else {
                return TypeConversionUtil.convertToString(evaluateString(), _do_i18n);
            }
        } else {
            return "";
        }
    }

    @Override
    @CheckForNull
    public Object evaluateGeneric() {
        if (isConnected()) {
            switch (_currentSocketType) {
                case DIGITAL:
                    return ((MaleDigitalExpressionSocket)getConnectedSocket())
                            .evaluate();
                    
                case ANALOG:
                    return ((MaleAnalogExpressionSocket)getConnectedSocket())
                            .evaluate();
                    
                case STRING:
                    return ((MaleStringExpressionSocket)getConnectedSocket())
                            .evaluate();
                    
                default:
                    throw new RuntimeException("_currentSocketType has invalid value: "+_currentSocketType.name());
            }
        } else {
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void reset() {
        if (isConnected()) {
            switch (_currentSocketType) {
                case DIGITAL:
                    ((MaleDigitalExpressionSocket)getConnectedSocket()).reset();
                    break;
                    
                case ANALOG:
                    ((MaleAnalogExpressionSocket)getConnectedSocket()).reset();
                    break;
                    
                case STRING:
                    ((MaleStringExpressionSocket)getConnectedSocket()).reset();
                    break;
                    
                default:
                    throw new RuntimeException("_currentSocketType has invalid value: "+_currentSocketType.name());
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getShortDescription() {
        return Bundle.getMessage("DefaultFemaleGenericExpressionSocket_Short");
    }

    /** {@inheritDoc} */
    @Override
    public String getLongDescription() {
        return Bundle.getMessage("DefaultFemaleGenericExpressionSocket_Long", getName());
    }

    /** {@inheritDoc} */
    @Override
    public String getExampleSystemName() {
        throw new RuntimeException("This method must be moved to the configuration of new expression");
//        return getConditionalNG().getSystemName() + ":DE10";
    }

    /** {@inheritDoc} */
    @Override
    public String getNewSystemName() {
        return InstanceManager.getDefault(DigitalExpressionManager.class)
                .getNewSystemName(getConditionalNG());
    }

    private void addClassesToMap(
            Map<Category, List<Class<? extends Base>>> destinationClasses,
            Map<Category, List<Class<? extends Base>>> sourceClasses) {
        
        for (Category category : Category.values()) {
            for (Class<? extends Base> clazz : sourceClasses.get(category)) {
                destinationClasses.get(category).add(clazz);
            }
        }
    }
    
    @Override
    public Map<Category, List<Class<? extends Base>>> getConnectableClasses() {
        Map<Category, List<Class<? extends Base>>> classes = new HashMap<>();
        
        for (Category category : Category.values()) {
            classes.put(category, new ArrayList<>());
        }
        
        addClassesToMap(classes, InstanceManager.getDefault(AnalogExpressionManager.class).getExpressionClasses());
        addClassesToMap(classes, InstanceManager.getDefault(DigitalExpressionManager.class).getExpressionClasses());
        addClassesToMap(classes, InstanceManager.getDefault(StringExpressionManager.class).getExpressionClasses());
        
        return classes;
    }

    /** {@inheritDoc} */
    @Override
    public void connect(MaleSocket socket) throws SocketAlreadyConnectedException {
        if (socket == null) {
            throw new NullPointerException("socket cannot be null");
        }
        
        // If _currentActiveSocket is not null, the socket is either connected
        // or locked to a particular type.
        if (_currentActiveSocket != null) {
            if (_currentActiveSocket.isConnected()) {
                throw new SocketAlreadyConnectedException("Socket is already connected");
            } else {
                _currentActiveSocket.connect(socket);
                return;
            }
        }
        
        // If we are here, the socket is not connected and is not locked to a
        // particular type.
        
        if (_digitalSocket.isCompatible(socket)) {
            _currentSocketType = SocketType.DIGITAL;
            _currentActiveSocket = _digitalSocket;
            _currentActiveSocket.connect(socket);
        } else if (_analogSocket.isCompatible(socket)) {
            _currentSocketType = SocketType.ANALOG;
            _currentActiveSocket = _analogSocket;
            _currentActiveSocket.connect(socket);
        } else if (_stringSocket.isCompatible(socket)) {
            _currentSocketType = SocketType.STRING;
            _currentActiveSocket = _stringSocket;
            _currentActiveSocket.connect(socket);
        } else {
            throw new UnsupportedOperationException("Socket is not compatible");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void disconnect() {
        if ((_currentActiveSocket != null) && _currentActiveSocket.isConnected()) {
            
            _currentActiveSocket.disconnect();
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public MaleSocket getConnectedSocket() {
        return _currentActiveSocket.getConnectedSocket();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isConnected() {
        return (_currentActiveSocket != null) && _currentActiveSocket.isConnected();
    }

    @Override
    public void connected(FemaleSocket socket) {
        _listener.connected(socket);
    }

    @Override
    public void disconnected(FemaleSocket socket) {
        if (_currentSocketType == SocketType.GENERIC) {
            _currentActiveSocket = null;
        }
        _listener.disconnected(socket);
    }
    
    /** {@inheritDoc} */
    @Override
    protected void registerListenersForThisClass() {
        if (_currentActiveSocket != null) {
            _currentActiveSocket.registerListeners();
        }
    }
    
    /** {@inheritDoc} */
    @Override
    protected void unregisterListenersForThisClass() {
        if (_currentActiveSocket != null) {
            _currentActiveSocket.unregisterListeners();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void disposeMe() {
        if ((_currentActiveSocket != null) && (_currentActiveSocket.isConnected())) {
            MaleSocket aSocket = _currentActiveSocket.getConnectedSocket();
            disconnect();
            aSocket.dispose();
        }
    }
    
    
    
    private class AnalogSocket extends DefaultFemaleAnalogExpressionSocket {
        
        public AnalogSocket() {
            super(null, null, DefaultFemaleGenericExpressionSocket.this.getName());
        }
        
        /** {@inheritDoc} */
        @Override
        public boolean isCompatible(MaleSocket socket) {
            return DefaultFemaleGenericExpressionSocket.this.isCompatible(socket);
        }
        
        /** {@inheritDoc} */
        @Override
        public double evaluate() {
            return DefaultFemaleGenericExpressionSocket.this.evaluateDouble();
        }

        /** {@inheritDoc} */
        @Override
        public void reset() {
            DefaultFemaleGenericExpressionSocket.this.reset();
        }
        
        @Override
        public Map<Category, List<Class<? extends Base>>> getConnectableClasses() {
            return DefaultFemaleGenericExpressionSocket.this.getConnectableClasses();
        }

        /** {@inheritDoc} */
        @Override
        public Base getParent() {
            return DefaultFemaleGenericExpressionSocket.this.getParent();
        }

        /** {@inheritDoc} */
        @Override
        public void setParent(Base parent) {
            DefaultFemaleGenericExpressionSocket.this.setParent(parent);
        }

        /** {@inheritDoc} */
        @Override
        public Lock getLock() {
            return DefaultFemaleGenericExpressionSocket.this.getLock();
        }

        /** {@inheritDoc} */
        @Override
        public void setLock(Lock lock) {
            DefaultFemaleGenericExpressionSocket.this.setLock(lock);
        }

        /** {@inheritDoc} */
        @Override
        public void connect(MaleSocket socket) throws SocketAlreadyConnectedException {
            DefaultFemaleGenericExpressionSocket.this.connect(socket);
        }

        /** {@inheritDoc} */
        @Override
        public void disconnect() {
            DefaultFemaleGenericExpressionSocket.this.disconnect();
        }

        /** {@inheritDoc} */
        @Override
        public MaleSocket getConnectedSocket() {
            return DefaultFemaleGenericExpressionSocket.this.getConnectedSocket();
        }

        /** {@inheritDoc} */
        @Override
        public boolean isConnected() {
            return DefaultFemaleGenericExpressionSocket.this.isConnected();
        }

        /** {@inheritDoc} */
        @Override
        public void setName(String name) {
            DefaultFemaleGenericExpressionSocket.this.setName(name);
        }

        /** {@inheritDoc} */
        @Override
        public String getName() {
            return DefaultFemaleGenericExpressionSocket.this.getName();
        }

        /** {@inheritDoc} */
        @Override
        public void disposeMe() {
            DefaultFemaleGenericExpressionSocket.this.dispose();
        }

        /**
         * Register listeners if this object needs that.
         */
        @Override
        public void registerListeners() {
            DefaultFemaleGenericExpressionSocket.this.registerListeners();
        }

        /**
         * Register listeners if this object needs that.
         */
        @Override
        public void unregisterListeners() {
            DefaultFemaleGenericExpressionSocket.this.unregisterListeners();
        }
        
    }
    
    
    private class DigitalSocket extends DefaultFemaleDigitalExpressionSocket {
        
        public DigitalSocket() {
            super(null, null, DefaultFemaleGenericExpressionSocket.this.getName());
        }
        
        /** {@inheritDoc} */
        @Override
        public boolean isCompatible(MaleSocket socket) {
            return DefaultFemaleGenericExpressionSocket.this.isCompatible(socket);
        }
        
        /** {@inheritDoc} */
        @Override
        public boolean evaluate() {
            return DefaultFemaleGenericExpressionSocket.this.evaluateBoolean();
        }

        /** {@inheritDoc} */
        @Override
        public void reset() {
            DefaultFemaleGenericExpressionSocket.this.reset();
        }
        
        @Override
        public Map<Category, List<Class<? extends Base>>> getConnectableClasses() {
            return DefaultFemaleGenericExpressionSocket.this.getConnectableClasses();
        }

        /** {@inheritDoc} */
        @Override
        public Base getParent() {
            return DefaultFemaleGenericExpressionSocket.this.getParent();
        }

        /** {@inheritDoc} */
        @Override
        public void setParent(Base parent) {
            DefaultFemaleGenericExpressionSocket.this.setParent(parent);
        }

        /** {@inheritDoc} */
        @Override
        public Lock getLock() {
            return DefaultFemaleGenericExpressionSocket.this.getLock();
        }

        /** {@inheritDoc} */
        @Override
        public void setLock(Lock lock) {
            DefaultFemaleGenericExpressionSocket.this.setLock(lock);
        }

        /** {@inheritDoc} */
        @Override
        public void connect(MaleSocket socket) throws SocketAlreadyConnectedException {
            DefaultFemaleGenericExpressionSocket.this.connect(socket);
        }

        /** {@inheritDoc} */
        @Override
        public void disconnect() {
            DefaultFemaleGenericExpressionSocket.this.disconnect();
        }

        /** {@inheritDoc} */
        @Override
        public MaleSocket getConnectedSocket() {
            return DefaultFemaleGenericExpressionSocket.this.getConnectedSocket();
        }

        /** {@inheritDoc} */
        @Override
        public boolean isConnected() {
            return DefaultFemaleGenericExpressionSocket.this.isConnected();
        }

        /** {@inheritDoc} */
        @Override
        public void setName(String name) {
            DefaultFemaleGenericExpressionSocket.this.setName(name);
        }

        /** {@inheritDoc} */
        @Override
        public String getName() {
            return DefaultFemaleGenericExpressionSocket.this.getName();
        }

        /** {@inheritDoc} */
        @Override
        public void disposeMe() {
            DefaultFemaleGenericExpressionSocket.this.dispose();
        }

        /**
         * Register listeners if this object needs that.
         */
        @Override
        public void registerListeners() {
            DefaultFemaleGenericExpressionSocket.this.registerListeners();
        }

        /**
         * Register listeners if this object needs that.
         */
        @Override
        public void unregisterListeners() {
            DefaultFemaleGenericExpressionSocket.this.unregisterListeners();
        }
        
    }
    
    
    private class StringSocket extends DefaultFemaleStringExpressionSocket {
        
        public StringSocket() {
            super(null, null, DefaultFemaleGenericExpressionSocket.this.getName());
        }
        
        /** {@inheritDoc} */
        @Override
        public boolean isCompatible(MaleSocket socket) {
            return DefaultFemaleGenericExpressionSocket.this.isCompatible(socket);
        }
        
        /** {@inheritDoc} */
        @Override
        public String evaluate() {
            return DefaultFemaleGenericExpressionSocket.this.evaluateString();
        }

        /** {@inheritDoc} */
        @Override
        public void reset() {
            DefaultFemaleGenericExpressionSocket.this.reset();
        }
        
        @Override
        public Map<Category, List<Class<? extends Base>>> getConnectableClasses() {
            return DefaultFemaleGenericExpressionSocket.this.getConnectableClasses();
        }

        /** {@inheritDoc} */
        @Override
        public Base getParent() {
            return DefaultFemaleGenericExpressionSocket.this.getParent();
        }

        /** {@inheritDoc} */
        @Override
        public void setParent(Base parent) {
            DefaultFemaleGenericExpressionSocket.this.setParent(parent);
        }

        /** {@inheritDoc} */
        @Override
        public Lock getLock() {
            return DefaultFemaleGenericExpressionSocket.this.getLock();
        }

        /** {@inheritDoc} */
        @Override
        public void setLock(Lock lock) {
            DefaultFemaleGenericExpressionSocket.this.setLock(lock);
        }

        /** {@inheritDoc} */
        @Override
        public void connect(MaleSocket socket) throws SocketAlreadyConnectedException {
            DefaultFemaleGenericExpressionSocket.this.connect(socket);
        }

        /** {@inheritDoc} */
        @Override
        public void disconnect() {
            DefaultFemaleGenericExpressionSocket.this.disconnect();
        }

        /** {@inheritDoc} */
        @Override
        public MaleSocket getConnectedSocket() {
            return DefaultFemaleGenericExpressionSocket.this.getConnectedSocket();
        }

        /** {@inheritDoc} */
        @Override
        public boolean isConnected() {
            return DefaultFemaleGenericExpressionSocket.this.isConnected();
        }

        /** {@inheritDoc} */
        @Override
        public void setName(String name) {
            DefaultFemaleGenericExpressionSocket.this.setName(name);
        }

        /** {@inheritDoc} */
        @Override
        public String getName() {
            return DefaultFemaleGenericExpressionSocket.this.getName();
        }

        /** {@inheritDoc} */
        @Override
        public void disposeMe() {
            DefaultFemaleGenericExpressionSocket.this.dispose();
        }

        /**
         * Register listeners if this object needs that.
         */
        @Override
        public void registerListeners() {
            DefaultFemaleGenericExpressionSocket.this.registerListeners();
        }

        /**
         * Register listeners if this object needs that.
         */
        @Override
        public void unregisterListeners() {
            DefaultFemaleGenericExpressionSocket.this.unregisterListeners();
        }
        
    }
    
}
