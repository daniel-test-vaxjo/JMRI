package jmri.jmrit.logixng.analog.implementation;

import java.util.List;
import java.util.Map;
import jmri.InstanceManager;
import jmri.jmrit.logixng.AnalogExpressionManager;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.FemaleAnalogExpressionSocket;
import jmri.jmrit.logixng.FemaleSocketListener;
import jmri.jmrit.logixng.MaleAnalogExpressionSocket;
import jmri.jmrit.logixng.MaleSocket;
import jmri.jmrit.logixng.implementation.AbstractFemaleSocket;

/**
 *
 */
public class DefaultFemaleAnalogExpressionSocket extends AbstractFemaleSocket
        implements FemaleAnalogExpressionSocket {

    public DefaultFemaleAnalogExpressionSocket(Base parent, FemaleSocketListener listener, String name) {
        super(parent, listener, name);
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
        return socket instanceof MaleAnalogExpressionSocket;
    }
    
    /** {@inheritDoc} */
    @Override
    public double evaluate() {
        if (isConnected()) {
            return ((MaleAnalogExpressionSocket)getConnectedSocket()).evaluate();
        } else {
            return 0.0;
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void reset() {
        if (isConnected()) {
            ((MaleAnalogExpressionSocket)getConnectedSocket()).reset();
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getShortDescription() {
        return Bundle.getMessage("DefaultFemaleAnalogExpressionSocket_Short");
    }

    /** {@inheritDoc} */
    @Override
    public String getLongDescription() {
        return Bundle.getMessage("DefaultFemaleAnalogExpressionSocket_Long", getName());
    }

    /** {@inheritDoc} */
    @Override
    public String getExampleSystemName() {
        return InstanceManager.getDefault(AnalogExpressionManager.class).getSystemNamePrefix() + "AE10";
    }

    /** {@inheritDoc} */
    @Override
    public String getNewSystemName() {
        return InstanceManager.getDefault(AnalogExpressionManager.class)
                .getNewSystemName();
    }

    /** {@inheritDoc} */
    @Override
    public Map<Category, List<Class<? extends Base>>> getConnectableClasses() {
        return InstanceManager.getDefault(AnalogExpressionManager.class).getExpressionClasses();
    }

    /** {@inheritDoc} */
    @Override
    public void disposeMe() {
        // Do nothing
    }

}
