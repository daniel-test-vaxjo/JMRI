package jmri.jmrit.logixng.expressions;

import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import jmri.InstanceManager;
import jmri.JmriException;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.FemaleSocket;
import jmri.jmrit.logixng.FemaleSocketListener;
import jmri.jmrit.logixng.DigitalExpressionManager;
import jmri.jmrit.logixng.FemaleDigitalExpressionSocket;
import jmri.jmrit.logixng.MaleSocket;
import jmri.jmrit.logixng.SocketAlreadyConnectedException;

/**
 * Evaluates to True if all of the children expressions evaluate to true.
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public class And extends AbstractDigitalExpression implements FemaleSocketListener {

    private final List<ExpressionEntry> _expressionEntries = new ArrayList<>();
    private boolean disableCheckForUnconnectedSocket = false;
    
    public And(String sys, String user)
            throws BadUserNameException, BadSystemNameException {
        super(sys, user);
        _expressionEntries
                .add(new ExpressionEntry(InstanceManager.getDefault(DigitalExpressionManager.class)
                        .createFemaleSocket(this, this, getNewSocketName())));
    }
    
    public And(String sys, String user, List<Map.Entry<String, String>> expressionSystemNames)
            throws BadUserNameException, BadSystemNameException {
        super(sys, user);
        setExpressionSystemNames(expressionSystemNames);
    }

    private void setExpressionSystemNames(List<Map.Entry<String, String>> systemNames) {
        if (!_expressionEntries.isEmpty()) {
            throw new RuntimeException("expression system names cannot be set more than once");
        }
        
        for (Map.Entry<String, String> entry : systemNames) {
            FemaleDigitalExpressionSocket socket =
                    InstanceManager.getDefault(DigitalExpressionManager.class)
                            .createFemaleSocket(this, this, entry.getKey());
            
            _expressionEntries.add(new ExpressionEntry(socket, entry.getValue()));
        }
    }
    
    public String getExpressionSystemName(int index) {
        return _expressionEntries.get(index)._socketSystemName;
    }

    /** {@inheritDoc} */
    @Override
    public Category getCategory() {
        return Category.COMMON;
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean isExternal() {
        return false;
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean evaluate() throws JmriException {
        boolean result = true;
        for (ExpressionEntry e : _expressionEntries) {
            if (e._socket.isConnected() && !e._socket.evaluate()) {
                result = false;
            }
        }
        return result;
    }
    
    @Override
    public FemaleSocket getChild(int index) throws IllegalArgumentException, UnsupportedOperationException {
        return _expressionEntries.get(index)._socket;
    }
    
    @Override
    public int getChildCount() {
        return _expressionEntries.size();
    }
    
    @Override
    public String getShortDescription(Locale locale) {
        return Bundle.getMessage(locale, "And_Short");
    }
    
    @Override
    public String getLongDescription(Locale locale) {
        return Bundle.getMessage(locale, "And_Long");
    }

    private void checkFreeSocket() {
        boolean hasFreeSocket = false;
        
        for (ExpressionEntry entry : _expressionEntries) {
            hasFreeSocket |= !entry._socket.isConnected();
        }
        if (!hasFreeSocket) {
            FemaleDigitalExpressionSocket socket =
                    InstanceManager.getDefault(DigitalExpressionManager.class)
                            .createFemaleSocket(this, this, getNewSocketName());
            _expressionEntries.add(new ExpressionEntry(socket));
            
            List<FemaleSocket> list = new ArrayList<>();
            list.add(socket);
            firePropertyChange(Base.PROPERTY_CHILD_COUNT, null, list);
        }
    }
    
    @Override
    public void connected(FemaleSocket socket) {
        if (disableCheckForUnconnectedSocket) return;
        
        for (ExpressionEntry entry : _expressionEntries) {
            if (socket == entry._socket) {
                entry._socketSystemName =
                        socket.getConnectedSocket().getSystemName();
            }
        }
        
        checkFreeSocket();
    }

    @Override
    public void disconnected(FemaleSocket socket) {
        for (ExpressionEntry entry : _expressionEntries) {
            if (socket == entry._socket) {
                entry._socketSystemName = null;
                break;
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setup() {
        // We don't want to check for unconnected sockets while setup sockets
        disableCheckForUnconnectedSocket = true;
        
        for (ExpressionEntry ee : _expressionEntries) {
            try {
                if ( !ee._socket.isConnected()
                        || !ee._socket.getConnectedSocket().getSystemName()
                                .equals(ee._socketSystemName)) {

                    String socketSystemName = ee._socketSystemName;
                    ee._socket.disconnect();
                    if (socketSystemName != null) {
                        MaleSocket maleSocket =
                                InstanceManager.getDefault(DigitalExpressionManager.class)
                                        .getBySystemName(socketSystemName);
                        if (maleSocket != null) {
                            ee._socket.connect(maleSocket);
                            maleSocket.setup();
                        } else {
                            log.error("cannot load digital expression " + socketSystemName);
                        }
                    }
                } else {
                    ee._socket.getConnectedSocket().setup();
                }
            } catch (SocketAlreadyConnectedException ex) {
                // This shouldn't happen and is a runtime error if it does.
                throw new RuntimeException("socket is already connected");
            }
        }
        
        checkFreeSocket();
        
        disableCheckForUnconnectedSocket = false;
    }
    
    
    /* This class is public since ExpressionAndXml needs to access it. */
    private static class ExpressionEntry {
        private String _socketSystemName;
        private final FemaleDigitalExpressionSocket _socket;
        
        private ExpressionEntry(FemaleDigitalExpressionSocket socket, String socketSystemName) {
            _socketSystemName = socketSystemName;
            _socket = socket;
        }
        
        private ExpressionEntry(FemaleDigitalExpressionSocket socket) {
            this._socket = socket;
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
    }
    
    private final static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(And.class);
    
}