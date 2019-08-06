package jmri.jmrit.logixng.string.implementation;

import java.util.List;
import java.util.Map;
import jmri.InstanceManager;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.FemaleSocketListener;
import jmri.jmrit.logixng.FemaleStringActionSocket;
import jmri.jmrit.logixng.MaleStringActionSocket;
import jmri.jmrit.logixng.MaleSocket;
import jmri.jmrit.logixng.SocketAlreadyConnectedException;
import jmri.jmrit.logixng.StringActionManager;
import jmri.jmrit.logixng.implementation.AbstractFemaleSocket;

/**
 *
 */
public final class DefaultFemaleStringActionSocket
        extends AbstractFemaleSocket
        implements FemaleStringActionSocket {

    public DefaultFemaleStringActionSocket(Base parent, FemaleSocketListener listener, String name) {
        super(parent, listener, name);
    }
    
    /** {@inheritDoc} */
    @Override
    public Base getNewObjectBasedOnTemplate(String sys) {
        // Female sockets have special handling
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean isCompatible(MaleSocket socket) {
        return socket instanceof MaleStringActionSocket;
    }
    
    @Override
    public void setValue(String value) {
        if (isConnected()) {
            ((MaleStringActionSocket)getConnectedSocket()).setValue(value);
        }
    }

    @Override
    public String getShortDescription() {
        return Bundle.getMessage("DefaultFemaleStringActionSocket_Short");
    }

    @Override
    public String getLongDescription() {
        return Bundle.getMessage("DefaultFemaleStringActionSocket_Long", getName());
    }

    /** {@inheritDoc} */
    @Override
    public String getExampleSystemName() {
        return InstanceManager.getDefault(StringActionManager.class).getSystemNamePrefix() + "SA10";
    }

    /** {@inheritDoc} */
    @Override
    public String getNewSystemName() {
        return InstanceManager.getDefault(StringActionManager.class).getNewSystemName();
    }

    @Override
    public Map<Category, List<Class<? extends Base>>> getConnectableClasses() {
        return InstanceManager.getDefault(StringActionManager.class).getActionClasses();
    }

    /** {@inheritDoc} */
    @Override
    public void disposeMe() {
        // Do nothing
    }

}
