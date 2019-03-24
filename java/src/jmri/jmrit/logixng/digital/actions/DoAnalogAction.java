package jmri.jmrit.logixng.digital.actions;

import jmri.InstanceManager;
import jmri.jmrit.logixng.AnalogActionManager;
import jmri.jmrit.logixng.AnalogExpressionManager;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.FemaleAnalogActionSocket;
import jmri.jmrit.logixng.FemaleAnalogExpressionSocket;
import jmri.jmrit.logixng.FemaleSocket;
import jmri.jmrit.logixng.FemaleSocketListener;
import jmri.jmrit.logixng.MaleAnalogActionSocket;
import jmri.jmrit.logixng.MaleAnalogExpressionSocket;
import jmri.jmrit.logixng.SocketAlreadyConnectedException;

/**
 * Executes an analog action with the result of an analog expression.
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public class DoAnalogAction
        extends AbstractDigitalAction
        implements FemaleSocketListener {

    private String _analogExpressionSocketSystemName;
    private String _analogActionSocketSystemName;
    private final FemaleAnalogExpressionSocket _analogExpressionSocket;
    private final FemaleAnalogActionSocket _analogActionSocket;
    
    public DoAnalogAction(String sys) {
        super(sys);
        _analogExpressionSocket = InstanceManager.getDefault(AnalogExpressionManager.class)
                .createFemaleAnalogExpressionSocket(this, this, "E1");
        _analogActionSocket = InstanceManager.getDefault(AnalogActionManager.class)
                .createFemaleAnalogActionSocket(this, this, "A1");
    }
    
    public DoAnalogAction(String sys, String user) {
        super(sys, user);
        _analogExpressionSocket = InstanceManager.getDefault(AnalogExpressionManager.class)
                .createFemaleAnalogExpressionSocket(this, this, "E1");
        _analogActionSocket = InstanceManager.getDefault(AnalogActionManager.class)
                .createFemaleAnalogActionSocket(this, this, "A1");
    }
    
    public DoAnalogAction(
            String sys,
            String expressionSocketName, String actionSocketName,
            MaleAnalogExpressionSocket expression, MaleAnalogActionSocket action) {
        
        super(sys);
        _analogExpressionSocket = InstanceManager.getDefault(AnalogExpressionManager.class)
                .createFemaleAnalogExpressionSocket(this, this, expressionSocketName, expression);
        _analogActionSocket = InstanceManager.getDefault(AnalogActionManager.class)
                .createFemaleAnalogActionSocket(this, this, actionSocketName, action);
    }
    
    public DoAnalogAction(
            String sys, String user,
            String expressionSocketName, String actionSocketName, 
            MaleAnalogExpressionSocket expression, MaleAnalogActionSocket action) {
        
        super(sys, user);
        _analogExpressionSocket = InstanceManager.getDefault(AnalogExpressionManager.class)
                .createFemaleAnalogExpressionSocket(this, this, expressionSocketName, expression);
        _analogActionSocket = InstanceManager.getDefault(AnalogActionManager.class)
                .createFemaleAnalogActionSocket(this, this, actionSocketName, action);
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
    public boolean executeStart() {
        float result = _analogExpressionSocket.evaluate();
        
        _analogActionSocket.setValue(result);

        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean executeContinue() {
        // We should never be here since executeStart() always return false.
        throw new UnsupportedOperationException("Not supported.");
    }

    /** {@inheritDoc} */
    @Override
    public boolean executeRestart() {
        return executeStart();
    }

    /** {@inheritDoc} */
    @Override
    public void abort() {
        // Do nothing
    }
    
    @Override
    public FemaleSocket getChild(int index) throws IllegalArgumentException, UnsupportedOperationException {
        switch (index) {
            case 0:
                return _analogExpressionSocket;
                
            case 1:
                return _analogActionSocket;
                
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
    public void connected(FemaleSocket socket) {
        // This class doesn't care.
    }

    @Override
    public void disconnected(FemaleSocket socket) {
        // This class doesn't care.
    }

    @Override
    public String getShortDescription() {
        return Bundle.getMessage("ActionDoAnalogAction_Short");
    }

    @Override
    public String getLongDescription() {
        return Bundle.getMessage("ActionDoAnalogAction", _analogExpressionSocket.getName(), _analogActionSocket.getName());
    }

    public void setAnalogActionSocketSystemName(String systemName) {
        _analogActionSocketSystemName = systemName;
    }

    public void setAnalogExpressionSocketSystemName(String systemName) {
        _analogExpressionSocketSystemName = systemName;
    }

    /** {@inheritDoc} */
    @Override
    public void setup() {
        try {
            if ((!_analogActionSocket.isConnected()) && (_analogActionSocketSystemName != null)) {
                _analogActionSocket.connect(
                        InstanceManager.getDefault(AnalogActionManager.class)
                                .getBeanBySystemName(_analogActionSocketSystemName));
            }
            if ((!_analogExpressionSocket.isConnected()) && (_analogExpressionSocketSystemName != null)) {
                _analogExpressionSocket.connect(
                        InstanceManager.getDefault(AnalogExpressionManager.class)
                                .getBeanBySystemName(_analogExpressionSocketSystemName));
            }
        } catch (SocketAlreadyConnectedException ex) {
            // This shouldn't happen and is a runtime error if it does.
            throw new RuntimeException("socket is already connected");
        }
    }

}
