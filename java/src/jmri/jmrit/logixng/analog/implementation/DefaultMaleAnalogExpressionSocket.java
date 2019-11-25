package jmri.jmrit.logixng.analog.implementation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nonnull;
import jmri.JmriException;
import jmri.NamedBean;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.FemaleSocket;
import jmri.jmrit.logixng.MaleAnalogExpressionSocket;
import jmri.jmrit.logixng.MaleSocket;
import jmri.jmrit.logixng.AnalogExpressionBean;
import jmri.jmrit.logixng.ConditionalNG;
import jmri.jmrit.logixng.LogixNG;
import jmri.jmrit.logixng.implementation.AbstractMaleSocket;
import jmri.util.Log4JUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Every AnalogExpressionBean has an DefaultMaleAnalogExpressionSocket as its parent.
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public class DefaultMaleAnalogExpressionSocket extends AbstractMaleSocket implements MaleAnalogExpressionSocket {

    private final AnalogExpressionBean _expression;
    private DebugConfig _debugConfig = null;
    private ErrorHandlingType _errorHandlingType = ErrorHandlingType.LOG_ERROR;
    private boolean _enabled = true;


    public DefaultMaleAnalogExpressionSocket(@Nonnull AnalogExpressionBean expression) {
        _expression = expression;
    }

    /** {@inheritDoc} */
    @Override
    public Base getNewObjectBasedOnTemplate() {
        return _expression.getNewObjectBasedOnTemplate();
    }
    
    /** {@inheritDoc} */
    @Override
    public final ConditionalNG getConditionalNG() {
        return _expression.getConditionalNG();
    }
    
    /** {@inheritDoc} */
    @Override
    public final LogixNG getLogixNG() {
        return _expression.getLogixNG();
    }
    
    /** {@inheritDoc} */
    @Override
    public final Base getRoot() {
        return _expression.getRoot();
    }
    
    @Override
    public Base getParent() {
        return _expression.getParent();
    }

    @Override
    public void setParent(Base parent) {
        _expression.setParent(parent);
    }

    /** {@inheritDoc} */
    @Override
    public Lock getLock() {
        return _expression.getLock();
    }
    
    /** {@inheritDoc} */
    @Override
    public void setLock(Lock lock) {
        _expression.setLock(lock);
    }
    
    public ErrorHandlingType getErrorHandlingType() {
        return _errorHandlingType;
    }

    public void setErrorHandlingType(ErrorHandlingType errorHandlingType)
    {
        _errorHandlingType = errorHandlingType;
    }

    /** {@inheritDoc} */
    @Override
    public Category getCategory() {
        return _expression.getCategory();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isExternal() {
        return _expression.isExternal();
    }
    
    /**
     * Get the value of the AnalogExpressionBean.
     */
    private double internalEvaluate() {
        double result = _expression.evaluate();
        
        if (Double.isNaN(result)) {
            throw new IllegalArgumentException("The result is NaN");
        }
        if (result == Double.NEGATIVE_INFINITY) {
            throw new IllegalArgumentException("The result is negative infinity");
        }
        if (result == Double.POSITIVE_INFINITY) {
            throw new IllegalArgumentException("The result is positive infinity");
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public double evaluate() {
        if (! _enabled) {
            return 0.0;
        }
        
        if ((_debugConfig != null)
                && ((AnalogExpressionDebugConfig)_debugConfig)._forceResult) {
            return ((AnalogExpressionDebugConfig)_debugConfig)._result;
        }
        
        try {
            return internalEvaluate();
        } catch (Exception e) {
            switch (_errorHandlingType) {
                case SHOW_DIALOG_BOX:
                    // We don't show a dialog box yet so fall thrue.
                case LOG_ERROR:
                    log.error("expression {} thrown an exception: {}", _expression.toString(), e);
                    return 0.0;
                    
                case LOG_ERROR_ONCE:
                    Log4JUtil.warnOnce(log, "expression {} thrown an exception: {}", _expression.toString(), e);
                    return 0.0;
                    
                case THROW:
                    throw e;
                    
                default:
                    throw e;
            }
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void reset() {
        _expression.reset();
    }

    @Override
    public int getState() {
        return _expression.getState();
    }

    @Override
    public FemaleSocket getChild(int index) throws IllegalArgumentException, UnsupportedOperationException {
        return _expression.getChild(index);
    }

    @Override
    public int getChildCount() {
        return _expression.getChildCount();
    }

    @Override
    public String getShortDescription(Locale locale) {
        return _expression.getShortDescription(locale);
    }

    @Override
    public String getLongDescription(Locale locale) {
        return _expression.getLongDescription(locale);
    }

    @Override
    public String getUserName() {
        return _expression.getUserName();
    }

    @Override
    public void setUserName(String s) throws BadUserNameException {
        _expression.setUserName(s);
    }

    @Override
    public String getSystemName() {
        return _expression.getSystemName();
    }

    @Override
    public String getDisplayName() {
        return _expression.getDisplayName();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l, String name, String listenerRef) {
        _expression.addPropertyChangeListener(l, name, listenerRef);
    }

    @Override
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener l, String name, String listenerRef) {
        _expression.addPropertyChangeListener(propertyName, l, name, listenerRef);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        _expression.addPropertyChangeListener(l);
    }

    @Override
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener l) {
        _expression.addPropertyChangeListener(propertyName, l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        _expression.removePropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener l) {
        _expression.removePropertyChangeListener(propertyName, l);
    }

    @Override
    public void updateListenerRef(PropertyChangeListener l, String newName) {
        _expression.updateListenerRef(l, newName);
    }

    @Override
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
        _expression.vetoableChange(evt);
    }

    @Override
    public String getListenerRef(PropertyChangeListener l) {
        return _expression.getListenerRef(l);
    }

    @Override
    public ArrayList<String> getListenerRefs() {
        return _expression.getListenerRefs();
    }

    @Override
    public int getNumPropertyChangeListeners() {
        return _expression.getNumPropertyChangeListeners();
    }

    @Override
    public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
        return _expression.getPropertyChangeListeners();
    }

    @Override
    public synchronized PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return _expression.getPropertyChangeListeners(propertyName);
    }

    @Override
    public PropertyChangeListener[] getPropertyChangeListenersByReference(String name) {
        return _expression.getPropertyChangeListenersByReference(name);
    }

    @Override
    public void disposeMe() {
        _expression.dispose();
    }

    /**
     * Register listeners if this object needs that.
     */
    @Override
    public void registerListenersForThisClass() {
        _expression.registerListeners();
    }
    
    /**
     * Register listeners if this object needs that.
     */
    @Override
    public void unregisterListenersForThisClass() {
        _expression.unregisterListeners();
    }
    
    @Override
    public void setState(int s) throws JmriException {
        _expression.setState(s);
    }

    @Override
    public String describeState(int state) {
        return Bundle.getMessage("BeanStateUnknown");
    }

    @Override
    public String getComment() {
        return _expression.getComment();
    }

    @Override
    public void setComment(String comment) {
        _expression.setComment(comment);
    }

    @Override
    public void setProperty(String key, Object value) {
        _expression.setProperty(key, value);
    }

    @Override
    public Object getProperty(String key) {
        return _expression.getProperty(key);
    }

    @Override
    public void removeProperty(String key) {
        _expression.removeProperty(key);
    }

    @Override
    public Set<String> getPropertyKeys() {
        return _expression.getPropertyKeys();
    }

    @Override
    public String getBeanType() {
        return _expression.getBeanType();
    }

    @Override
    public int compareSystemNameSuffix(String suffix1, String suffix2, NamedBean n2) {
        return _expression.compareSystemNameSuffix(suffix1, suffix2, n2);
    }

    /** {@inheritDoc} */
    @Override
    public void setDebugConfig(DebugConfig config) {
        _debugConfig = config;
    }

    /** {@inheritDoc} */
    @Override
    public DebugConfig getDebugConfig() {
        return _debugConfig;
    }

    /** {@inheritDoc} */
    @Override
    public DebugConfig createDebugConfig() {
        return new AnalogExpressionDebugConfig();
    }

    /** {@inheritDoc} */
    @Override
    public Base getObject() {
        return _expression;
    }
    
    /** {@inheritDoc} */
    @Override
    public void setEnabled(boolean enable) {
        _enabled = enable;
        if (enable) {
            registerListeners();
        } else {
            unregisterListeners();
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean isEnabled() {
        return _enabled;
    }



    public static class AnalogExpressionDebugConfig implements MaleSocket.DebugConfig {
        
        // If true, the socket is returning the value of "result" instead of
        // executing the expression.
        public boolean _forceResult = false;
        
        // The result if the result is forced.
        public float _result = 0.0f;
        
    }
    
    private final static Logger log = LoggerFactory.getLogger(DefaultMaleAnalogExpressionSocket.class);

}