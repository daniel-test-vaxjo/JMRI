package jmri.jmrit.logixng.analog.implementation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jmri.InstanceManager;
import jmri.JmriException;
import jmri.NamedBean;
import jmri.jmrit.logixng.AnalogExpressionManager;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.FemaleAnalogExpressionSocket;
import jmri.jmrit.logixng.FemaleSocket;
import jmri.jmrit.logixng.FemaleSocketListener;
import jmri.jmrit.logixng.MaleAnalogExpressionSocket;
import jmri.jmrit.logixng.MaleSocket;
import jmri.jmrit.logixng.SocketAlreadyConnectedException;
import jmri.jmrit.logixng.implementation.AbstractFemaleSocket;

/**
 *
 */
public final class DefaultFemaleAnalogExpressionSocket extends AbstractFemaleSocket
        implements FemaleAnalogExpressionSocket {

    public DefaultFemaleAnalogExpressionSocket(Base parent, FemaleSocketListener listener, String name) {
        super(parent, listener, name);
    }
    
    public DefaultFemaleAnalogExpressionSocket(
            Base parent,
            FemaleSocketListener listener,
            String name,
            MaleAnalogExpressionSocket maleSocket) {
        
        super(parent, listener, name);
        
        try {
            connect(maleSocket);
        } catch (SocketAlreadyConnectedException e) {
            // This should never be able to happen since a newly created
            // socket is not connected.
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public boolean isCompatible(MaleSocket socket) {
        return socket instanceof MaleAnalogExpressionSocket;
    }
    
    @Override
    public float evaluate() {
        if (isConnected()) {
            return ((MaleAnalogExpressionSocket)getConnectedSocket()).evaluate();
        } else {
            return (float) 0.0;
        }
    }

    @Override
    public void dispose() {
    }

    @Override
    public String getShortDescription() {
        return Bundle.getMessage("DefaultFemaleAnalogExpressionSocket_Short");
    }

    @Override
    public String getLongDescription() {
        return Bundle.getMessage("DefaultFemaleAnalogExpressionSocket_Long", getName());
    }

    /** {@inheritDoc} */
    @Override
    public String getExampleSystemName() {
        return getLogixNG().getSystemName() + ":AE10";
    }

    @Override
    public Map<Category, List<Class<? extends Base>>> getConnectableClasses() {
        return InstanceManager.getDefault(AnalogExpressionManager.class).getExpressionClasses();
    }

}
