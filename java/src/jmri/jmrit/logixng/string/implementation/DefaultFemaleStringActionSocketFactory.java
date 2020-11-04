package jmri.jmrit.logixng.string.implementation;

import jmri.jmrit.logixng.*;

import org.openide.util.lookup.ServiceProvider;

/**
 * Factory class for DefaultFemaleStringActionSocket class.
 * 
 * @author Daniel Bergqvist Copyright 2020
 */
@ServiceProvider(service = FemaleSocketFactory.class)
public class DefaultFemaleStringActionSocketFactory implements FemaleSocketFactory {

    private static final FemaleSocketManager.SocketType _femaleSocketType = new SocketType();
    
    
    @Override
    public FemaleSocketManager.SocketType getFemaleSocketType() {
        return _femaleSocketType;
    }


    private static class SocketType implements FemaleSocketManager.SocketType {
        
        @Override
        public String getName() {
            return "DefaultFemaleStringActionSocket";
        }

        @Override
        public String getDescr() {
            return Bundle.getMessage("FemaleStringActionSocket_Descr");
        }

        @Override
        public FemaleSocket createSocket(Base parent, FemaleSocketListener listener, String name) {
            return new DefaultFemaleStringActionSocket(parent, listener, name);
        }
    }
    
}