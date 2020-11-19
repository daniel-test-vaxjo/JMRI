package jmri.jmrit.logixng.actions;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import javax.script.Bindings;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import jmri.InstanceManager;
import jmri.JmriException;
import jmri.jmrit.logixng.AnalogActionManager;
import jmri.jmrit.logixng.AnalogExpressionManager;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.DigitalAction;
import jmri.jmrit.logixng.DigitalActionManager;
import jmri.jmrit.logixng.DigitalBooleanActionManager;
import jmri.jmrit.logixng.DigitalExpressionManager;
import jmri.jmrit.logixng.FemaleSocket;
import jmri.jmrit.logixng.StringActionManager;
import jmri.jmrit.logixng.StringExpressionManager;
import jmri.script.JmriScriptEngineManager;

/**
 * Executes a script.
 * 
 * @author Daniel Bergqvist Copyright 2019
 */
public class ActionScript extends AbstractDigitalAction {

    private String _scriptText;
    private AbstractScriptDigitalAction _scriptClass;

    public ActionScript(String sys, String user)
            throws BadUserNameException, BadSystemNameException {
        super(sys, user);
    }
    
    private void loadScript() {
        try {
            jmri.script.JmriScriptEngineManager scriptEngineManager = jmri.script.JmriScriptEngineManager.getDefault();

            Bindings bindings = new SimpleBindings();
            ScriptParams params = new ScriptParams(this);
            
            // this should agree with help/en/html/tools/scripting/Start.shtml - this link is wrong and should point to LogixNG documentation
            bindings.put("analogActions", InstanceManager.getNullableDefault(AnalogActionManager.class));
            bindings.put("analogExpressions", InstanceManager.getNullableDefault(AnalogExpressionManager.class));
            bindings.put("digitalActions", InstanceManager.getNullableDefault(DigitalActionManager.class));
            bindings.put("digitalBooleanActions", InstanceManager.getNullableDefault(DigitalBooleanActionManager.class));
            bindings.put("digitalExpressions", InstanceManager.getNullableDefault(DigitalExpressionManager.class));
            bindings.put("stringActions", InstanceManager.getNullableDefault(StringActionManager.class));
            bindings.put("stringExpressions", InstanceManager.getNullableDefault(StringExpressionManager.class));
            
            bindings.put("params", params);    // Give the script access to the local variable 'params'
            
            scriptEngineManager.getEngineByName(JmriScriptEngineManager.PYTHON)
                    .eval(_scriptText, bindings);
            
            _scriptClass = params._scriptClass.get();
        } catch (ScriptException e) {
            log.error("cannot load script", e);
            _scriptText = null;
            _scriptClass = null;
            return;
        }
        
        if (_scriptClass == null) {
            log.error("script has not initialized params._scriptClass");
        }
    }
    
    public void setScript(String script) {
        if (_listenersAreRegistered) {
            RuntimeException e = new RuntimeException("setScript must not be called when listeners are registered");
            log.error("setScript must not be called when listeners are registered", e);
            throw e;
        }
        _scriptText = script;
        if (_scriptText != null) {
            loadScript();
        } else {
            _scriptClass = null;
        }
    }
    
    public String getScriptText() {
        return _scriptText;
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
    public void execute() throws JmriException {
        if (_scriptClass != null) _scriptClass.execute();
    }

    @Override
    public FemaleSocket getChild(int index) throws IllegalArgumentException, UnsupportedOperationException {
        if (_scriptClass != null) {
            return _scriptClass.getChild(index);
        } else {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

    @Override
    public int getChildCount() {
        if (_scriptClass != null) {
            return _scriptClass.getChildCount();
        } else {
            return 0;
        }
    }

    @Override
    public String getShortDescription(Locale locale) {
        return Bundle.getMessage(locale, "Script_Short");
    }

    @Override
    public String getLongDescription(Locale locale) {
        return Bundle.getMessage(locale, "Script_Long");
    }
    
    /** {@inheritDoc} */
    @Override
    public void setup() {
        if (_scriptClass != null) _scriptClass.setup();
    }
    
    /** {@inheritDoc} */
    @Override
    public void registerListenersForThisClass() {
        if (!_listenersAreRegistered && (_scriptClass != null)) {
            _scriptClass.registerListeners();
            _listenersAreRegistered = true;
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void unregisterListenersForThisClass() {
        if (_listenersAreRegistered) {
            _scriptClass.unregisterListeners();
            _listenersAreRegistered = false;
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void firePropertyChange(String p, Object old, Object n) {
        super.firePropertyChange(p, old, n);
    }
    
    /** {@inheritDoc} */
    @Override
    public void disposeMe() {
        if (_scriptClass != null) _scriptClass.dispose();
    }
    
    
    public static class ScriptParams {
        
        public final AtomicReference<AbstractScriptDigitalAction> _scriptClass
                = new AtomicReference<>();
        
        public final DigitalAction _parentAction;
        
        public ScriptParams(DigitalAction parentExpression) {
            _parentAction  = parentExpression;
        }
    }
    
    
    private final static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ActionScript.class);
    
}