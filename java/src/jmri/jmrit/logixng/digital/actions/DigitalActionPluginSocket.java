package jmri.jmrit.logixng.digital.actions;

import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.FemaleSocket;
import jmri.jmrit.logixng.DigitalActionPlugin;

/**
 * This class has a plugin class.
 */
public class DigitalActionPluginSocket extends AbstractDigitalAction {

    private DigitalActionPluginSocket _template;
    private final DigitalActionPlugin _actionPlugin;
    private DebugConfig _debugConfig = null;
    
    public DigitalActionPluginSocket(String sys, DigitalActionPlugin actionPlugin) {
        super(sys);
        _actionPlugin = actionPlugin;
    }
    
    public DigitalActionPluginSocket(
            String sys, String user, DigitalActionPlugin actionPlugin) {
        super(sys, user);
        _actionPlugin = actionPlugin;
    }
    
    private DigitalActionPluginSocket(DigitalActionPluginSocket template, String sys) {
        super(sys);
        _actionPlugin = null;
        _template = template;
        if (_template == null) throw new NullPointerException();    // Temporary solution to make variable used.
    }
    
    /** {@inheritDoc} */
    @Override
    public Base getNewObjectBasedOnTemplate(String sys) {
        return new DigitalActionPluginSocket(this, sys);
    }
    
    @Override
    public Category getCategory() {
        return _actionPlugin.getCategory();
    }

    @Override
    public boolean isExternal() {
        return _actionPlugin.isExternal();
    }

    @Override
    public boolean executeStart() {
        return _actionPlugin.executeStart();
    }

    @Override
    public boolean executeContinue() {
        return _actionPlugin.executeContinue();
    }

    @Override
    public boolean executeRestart() {
        return _actionPlugin.executeRestart();
    }

    @Override
    public void abort() {
        _actionPlugin.abort();
    }

    @Override
    public FemaleSocket getChild(int index) throws IllegalArgumentException, UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getChildCount() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public String getShortDescription() {
        return Bundle.getMessage("ActionPluginSocket_Short");
    }

    @Override
    public String getLongDescription() {
        return Bundle.getMessage("ActionPluginSocket_Long");
//        return Bundle.getMessage("ActionPluginSocket_Long", _analogExpressionSocket.getName(), _analogActionSocket.getName());
    }

    /** {@inheritDoc} */
    @Override
    public void setup() {
        _actionPlugin.setup();
    }
    
    /** {@inheritDoc} */
    @Override
    public void registerListenersForThisClass() {
    }
    
    /** {@inheritDoc} */
    @Override
    public void unregisterListenersForThisClass() {
    }
    
    /** {@inheritDoc} */
    @Override
    public void disposeMe() {
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
        return new MyDebugConfig();
    }
    
    
    
    public class MyDebugConfig implements DebugConfig {

    }

}
