package jmri.jmrit.logixng.digital.actions;

import java.util.Locale;
import java.util.Objects;
import javax.annotation.Nonnull;
import jmri.InstanceManager;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.DigitalActionManager;
import jmri.jmrit.logixng.FemaleSocket;
import jmri.jmrit.logixng.DigitalActionPlugin;

/**
 * This class has a plugin class.
 */
public class DigitalActionPluginSocket extends AbstractDigitalAction {

    private DigitalActionPluginSocket _template;
    private final DigitalActionPlugin _actionPlugin;
    
    public DigitalActionPluginSocket(
            @Nonnull String sys, @Nonnull String user, @Nonnull DigitalActionPlugin actionPlugin) {
        super(sys, user);
        Objects.requireNonNull(actionPlugin, "parameter actionPlugin must not be null");
        _actionPlugin = actionPlugin;
    }
    
    private DigitalActionPluginSocket(@Nonnull DigitalActionPluginSocket template) {
        super(InstanceManager.getDefault(DigitalActionManager.class).getAutoSystemName(), null);
        Objects.requireNonNull(template, "parameter template must not be null");
        _actionPlugin = null;
        _template = template;
        if (_template == null) throw new NullPointerException();    // Temporary solution to make variable used.
    }
    
    /** {@inheritDoc} */
    @Override
    public Base getNewObjectBasedOnTemplate() {
        return new DigitalActionPluginSocket(this);
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
    public void execute() {
        _actionPlugin.execute();
    }

    @Override
    public FemaleSocket getChild(int index) throws IllegalArgumentException, UnsupportedOperationException {
        return _actionPlugin.getChild(index);
    }

    @Override
    public int getChildCount() {
        return _actionPlugin.getChildCount();
    }
    
    @Override
    public String getShortDescription(Locale locale) {
        return _actionPlugin.getShortDescription(locale);
    }

    @Override
    public String getLongDescription(Locale locale) {
        return _actionPlugin.getLongDescription(locale);
    }

    /** {@inheritDoc} */
    @Override
    public void setup() {
        _actionPlugin.setup();
    }
    
    /** {@inheritDoc} */
    @Override
    public void registerListenersForThisClass() {
        _actionPlugin.registerListeners();
    }
    
    /** {@inheritDoc} */
    @Override
    public void unregisterListenersForThisClass() {
        _actionPlugin.unregisterListeners();
    }
    
    /** {@inheritDoc} */
    @Override
    public void disposeMe() {
        _actionPlugin.dispose();
    }

}