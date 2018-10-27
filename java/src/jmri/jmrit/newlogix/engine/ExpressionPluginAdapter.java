package jmri.jmrit.newlogix.engine;

import jmri.jmrit.newlogix.Category;
import jmri.jmrit.newlogix.AbstractExpression;
import jmri.jmrit.newlogix.Expression;
import jmri.jmrit.newlogix.FemaleSocket;

/**
 * Adapter for expression plugins.
 * Every expression needs to have a configurator class that delivers a JPanel
 * used for configuration. Since plugin expressions has 
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public class ExpressionPluginAdapter extends AbstractExpression {

    private Expression _pluginExpression;
    
    public ExpressionPluginAdapter(String sys, Expression pluginExpression)
            throws BadUserNameException,
            BadSystemNameException {
        
        super(sys);
//        jmri.jmrix.ConnectionConfig cc;
        _pluginExpression = pluginExpression;
    }

    /** {@inheritDoc} */
    @Override
    public Category getCategory() {
        return _pluginExpression.getCategory();
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean isExternal() {
        return false;
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean evaluate() {
        return _pluginExpression.evaluate();
    }
    
    /** {@inheritDoc} */
    @Override
    public void reset() {
        _pluginExpression.reset();
    }

    @Override
    public FemaleSocket getChild(int index) throws IllegalArgumentException, UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getChildCount() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}