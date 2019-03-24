package jmri.jmrit.logixng.analog.expressions;

import jmri.AnalogIO;
import jmri.InstanceManager;
import jmri.jmrit.logixng.AnalogExpressionManager;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.FemaleSocket;

/**
 * Reads an AnalogIO.
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public class GetAnalogIO extends AbstractAnalogExpression {

    private String _analogIO_SystemName;
    private AnalogIO _analogIO;
    
    public GetAnalogIO(String sys) throws BadUserNameException,
            BadSystemNameException {
        
        super(sys);
    }

    public GetAnalogIO(String sys, String user)
            throws BadUserNameException, BadSystemNameException {
        
        super(sys, user);
    }

    public GetAnalogIO(String sys, AnalogIO analogIO)
            throws BadUserNameException, BadSystemNameException {
        
        super(sys);
        _analogIO = analogIO;
    }
    
    public GetAnalogIO(String sys, String user, AnalogIO analogIO)
            throws BadUserNameException, BadSystemNameException {
        
        super(sys, user);
        _analogIO = analogIO;
    }
    
    /** {@inheritDoc} */
    @Override
    public Category getCategory() {
        return Category.ITEM;
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean isExternal() {
        return true;
    }
    
    /** {@inheritDoc} */
    @Override
    public float evaluate() {
        if (_analogIO != null) {
            return _analogIO.getKnownAnalogValue();
        } else {
            return (float) 0.0;
        }
    }
    
    @Override
    public FemaleSocket getChild(int index)
            throws IllegalArgumentException, UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public String getShortDescription() {
        if (_analogIO != null) {
            return Bundle.getMessage("AnalogExpressionAnalogIO", _analogIO.getDisplayName());
        } else {
            return Bundle.getMessage("AnalogExpressionAnalogIO", "none");
        }
    }

    @Override
    public String getLongDescription() {
        return getShortDescription();
    }

    public void setAnalogIO_SystemName(String analogIO_SystemName) {
        _analogIO_SystemName = analogIO_SystemName;
    }
    
    /** {@inheritDoc} */
    @Override
    public void setup() {
        if ((_analogIO == null) && (_analogIO_SystemName != null)) {
//            _analogIO = InstanceManager.getDefault(AnalogIOManager.class).getBeanBySystemName(_analogIO_SystemName);
        }
    }

}
