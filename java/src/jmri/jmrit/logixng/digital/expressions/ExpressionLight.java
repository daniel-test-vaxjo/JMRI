package jmri.jmrit.logixng.digital.expressions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import javax.annotation.CheckForNull;
import jmri.InstanceManager;
import jmri.NamedBeanHandle;
import jmri.NamedBeanHandleManager;
import jmri.Light;
import jmri.LightManager;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.DigitalExpressionManager;
import jmri.jmrit.logixng.FemaleSocket;
import jmri.jmrit.logixng.Is_IsNot_Enum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Evaluates the state of a Light.
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public class ExpressionLight extends AbstractDigitalExpression
        implements PropertyChangeListener, VetoableChangeListener {

    private ExpressionLight _template;
    private NamedBeanHandle<Light> _lightHandle;
    private Is_IsNot_Enum _is_IsNot = Is_IsNot_Enum.IS;
    private LightState _lightState = LightState.ON;
    private boolean _listenersAreRegistered = false;

    public ExpressionLight()
            throws BadUserNameException {
        super(InstanceManager.getDefault(DigitalExpressionManager.class).getNewSystemName());
    }

    public ExpressionLight(String sys)
            throws BadUserNameException, BadSystemNameException {
        super(sys);
    }

    public ExpressionLight(String sys, String user)
            throws BadUserNameException, BadSystemNameException {
        super(sys, user);
    }
    
    private ExpressionLight(ExpressionLight template, String sys) {
        super(sys);
        _template = template;
        if (_template == null) throw new NullPointerException();    // Temporary solution to make variable used.
    }
    
    /** {@inheritDoc} */
    @Override
    public Base getNewObjectBasedOnTemplate(String sys) {
        return new ExpressionLight(this, sys);
    }
    
    public void setLight(String lightName) {
        if (_listenersAreRegistered) {
            RuntimeException e = new RuntimeException("setLight must not be called when listeners are registered");
            log.error("setLight must not be called when listeners are registered", e);
            throw e;
        }
        Light light = InstanceManager.getDefault(LightManager.class).getLight(lightName);
        _lightHandle = InstanceManager.getDefault(NamedBeanHandleManager.class).getNamedBeanHandle(lightName, light);
    }
    
    public void setLight(NamedBeanHandle<Light> handle) {
        if (_listenersAreRegistered) {
            RuntimeException e = new RuntimeException("setLight must not be called when listeners are registered");
            log.error("setLight must not be called when listeners are registered", e);
            throw e;
        }
        _lightHandle = handle;
    }
    
    public void setLight(@CheckForNull Light light) {
        if (_listenersAreRegistered) {
            RuntimeException e = new RuntimeException("setLight must not be called when listeners are registered");
            log.error("setLight must not be called when listeners are registered", e);
            throw e;
        }
        if (light != null) {
            _lightHandle = InstanceManager.getDefault(NamedBeanHandleManager.class)
                    .getNamedBeanHandle(light.getDisplayName(), light);
        } else {
            _lightHandle = null;
        }
    }
    
    public NamedBeanHandle<Light> getLight() {
        return _lightHandle;
    }
    
    public void set_Is_IsNot(Is_IsNot_Enum is_IsNot) {
        _is_IsNot = is_IsNot;
    }
    
    public Is_IsNot_Enum get_Is_IsNot() {
        return _is_IsNot;
    }
    
    public void setLightState(LightState state) {
        _lightState = state;
    }
    
    public LightState getLightState() {
        return _lightState;
    }

    @Override
    public void vetoableChange(java.beans.PropertyChangeEvent evt) throws java.beans.PropertyVetoException {
        if ("CanDelete".equals(evt.getPropertyName())) { // No I18N
            if (evt.getOldValue() instanceof Light) {
                if (evt.getOldValue().equals(getLight())) {
                    throw new PropertyVetoException(getDisplayName(), evt);
                }
            }
        } else if ("DoDelete".equals(evt.getPropertyName())) { // No I18N
            if (evt.getOldValue() instanceof Light) {
                if (evt.getOldValue().equals(getLight())) {
                    setLight((Light)null);
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
    
    /** {@inheritDoc} */
    @Override
    public boolean evaluate() {
        LightState currentLightState = LightState.get(_lightHandle.getBean().getCommandedState());
        if (_is_IsNot == Is_IsNot_Enum.IS) {
            return currentLightState == _lightState;
        } else {
            return currentLightState != _lightState;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void reset() {
        // Do nothing.
    }

    @Override
    public FemaleSocket getChild(int index) throws IllegalArgumentException, UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public String getShortDescription() {
        return Bundle.getMessage("Light_Short");
    }

    @Override
    public String getLongDescription() {
        String lightName;
        if (_lightHandle != null) {
            lightName = _lightHandle.getBean().getDisplayName();
        } else {
            lightName = Bundle.getMessage("BeanNotSelected");
        }
        return Bundle.getMessage("Light_Long", lightName, _is_IsNot.toString(), _lightState._text);
    }
    
    /** {@inheritDoc} */
    @Override
    public void setup() {
        // Do nothing
    }
    
    /** {@inheritDoc} */
    @Override
    public void registerListenersForThisClass() {
        if (!_listenersAreRegistered && (_lightHandle != null)) {
            _lightHandle.getBean().addPropertyChangeListener("KnownState", this);
            _listenersAreRegistered = true;
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void unregisterListenersForThisClass() {
        if (_listenersAreRegistered) {
            _lightHandle.getBean().removePropertyChangeListener("KnownState", this);
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
    
    
    
    public enum LightState {
        OFF(Light.OFF, Bundle.getMessage("StateOff")),
        ON(Light.ON, Bundle.getMessage("StateOn")),
        OTHER(-1, Bundle.getMessage("LightOtherStatus"));
        
        private final int _id;
        private final String _text;
        
        private LightState(int id, String text) {
            this._id = id;
            this._text = text;
        }
        
        static public LightState get(int id) {
            switch (id) {
                case Light.OFF:
                    return OFF;
                    
                case Light.ON:
                    return ON;
                    
                default:
                    return OTHER;
            }
        }
        
        public int getID() {
            return _id;
        }
        
        @Override
        public String toString() {
            return _text;
        }
        
    }
    
    
    private final static Logger log = LoggerFactory.getLogger(ExpressionLight.class);
    
}
