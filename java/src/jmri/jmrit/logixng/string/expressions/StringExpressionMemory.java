package jmri.jmrit.logixng.string.expressions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import javax.annotation.CheckForNull;
import jmri.InstanceManager;
import jmri.Memory;
import jmri.MemoryManager;
import jmri.NamedBeanHandle;
import jmri.NamedBeanHandleManager;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.FemaleSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads a Memory.
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public class StringExpressionMemory extends AbstractStringExpression
        implements PropertyChangeListener, VetoableChangeListener {

    private StringExpressionMemory _template;
    private NamedBeanHandle<Memory> _memoryHandle;
    private boolean _listenersAreRegistered = false;
    
    public StringExpressionMemory(String sys) throws BadUserNameException,
            BadSystemNameException {
        
        super(sys);
    }

    public StringExpressionMemory(String sys, String user)
            throws BadUserNameException, BadSystemNameException {
        
        super(sys, user);
    }

    private StringExpressionMemory(StringExpressionMemory template, String sys) {
        super(sys);
        _template = template;
        _memoryHandle = _template._memoryHandle;
    }
    
    /** {@inheritDoc} */
    @Override
    public Base getNewObjectBasedOnTemplate(String sys) {
        return new StringExpressionMemory(this, sys);
    }
    
    @Override
    public void vetoableChange(java.beans.PropertyChangeEvent evt) throws java.beans.PropertyVetoException {
        if ("CanDelete".equals(evt.getPropertyName())) { // No I18N
            if (evt.getOldValue() instanceof Memory) {
                if (evt.getOldValue().equals(getMemory())) {
                    throw new PropertyVetoException(getDisplayName(), evt);
                }
            }
        } else if ("DoDelete".equals(evt.getPropertyName())) { // No I18N
            if (evt.getOldValue() instanceof Memory) {
                if (evt.getOldValue().equals(getMemory())) {
                    setMemory((Memory)null);
                }
            }
        }
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
    
    public void setMemory(String memoryName) {
        if (_listenersAreRegistered) {
            RuntimeException e = new RuntimeException("setMemory must not be called when listeners are registered");
            log.error("setMemory must not be called when listeners are registered", e);
            throw e;
        }
        Memory memory = InstanceManager.getDefault(MemoryManager.class).getMemory(memoryName);
        _memoryHandle = InstanceManager.getDefault(NamedBeanHandleManager.class).getNamedBeanHandle(memoryName, memory);
    }
    
    public void setMemory(NamedBeanHandle<Memory> handle) {
        if (_listenersAreRegistered) {
            RuntimeException e = new RuntimeException("setMemory must not be called when listeners are registered");
            log.error("setMemory must not be called when listeners are registered", e);
            throw e;
        }
        _memoryHandle = handle;
    }
    
    public void setMemory(@CheckForNull Memory memory) {
        if (_listenersAreRegistered) {
            RuntimeException e = new RuntimeException("setMemory must not be called when listeners are registered");
            log.error("setMemory must not be called when listeners are registered", e);
            throw e;
        }
        if (memory != null) {
            _memoryHandle = InstanceManager.getDefault(NamedBeanHandleManager.class)
                    .getNamedBeanHandle(memory.getDisplayName(), memory);
        } else {
            _memoryHandle = null;
        }
    }
    
    public NamedBeanHandle<Memory> getMemory() {
        return _memoryHandle;
    }
    
    /** {@inheritDoc} */
    @Override
    public String evaluate() {
        if (_memoryHandle != null) {
            return jmri.util.TypeConversionUtil.convertToString(_memoryHandle.getBean().getValue(), false);
        } else {
            return "";
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void reset() {
        // Do nothing
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
        if (_memoryHandle != null) {
            return Bundle.getMessage("StringExpressionMemory1", _memoryHandle.getBean().getDisplayName());
        } else {
            return Bundle.getMessage("StringExpressionMemory1", "none");
        }
    }

    @Override
    public String getLongDescription() {
        return getShortDescription();
    }

    /** {@inheritDoc} */
    @Override
    public void setup() {
        // Do nothing
    }
    
    /** {@inheritDoc} */
    @Override
    public void registerListenersForThisClass() {
        if ((! _listenersAreRegistered) && (_memoryHandle != null)) {
            _memoryHandle.getBean().addPropertyChangeListener("value", this);
            _listenersAreRegistered = true;
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void unregisterListenersForThisClass() {
        if (_listenersAreRegistered) {
            _memoryHandle.getBean().removePropertyChangeListener("value", this);
            _listenersAreRegistered = false;
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        getConditionalNG().execute();
    }
    
    /** {@inheritDoc} */
    @Override
    public void disposeMe() {
    }
    
    
    private final static Logger log = LoggerFactory.getLogger(StringExpressionMemory.class);
    
}
