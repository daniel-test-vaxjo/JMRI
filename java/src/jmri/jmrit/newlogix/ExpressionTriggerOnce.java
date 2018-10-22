package jmri.jmrit.newlogix;

import jmri.NewLogixCategory;
import jmri.implementation.AbstractExpression;
import jmri.NewLogixExpression;

/**
 * An NewLogixExpression that returns True only once while its child expression returns
 True.
 * 
 * The first time the child expression returns True, this expression returns
 * True. After that, this expression returns False until the child expression
 * returns False and again returns True.
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public class ExpressionTriggerOnce extends AbstractExpression {

    private NewLogixExpression _childExpression;
    private boolean _childLastState = false;
    
    public ExpressionTriggerOnce(String sys, String user, NewLogixExpression childExpression)
            throws BadUserNameException, BadSystemNameException {
        
        super(sys, user);
        _childExpression = childExpression;
    }
    
    /** {@inheritDoc} */
    @Override
    public NewLogixCategory getCategory() {
        return NewLogixCategory.OTHER;
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
    public NewLogixSocket getChild(int index) throws IllegalArgumentException, UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getChildCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
