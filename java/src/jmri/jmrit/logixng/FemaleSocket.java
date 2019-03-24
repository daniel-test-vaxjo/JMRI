package jmri.jmrit.logixng;

import java.util.Map;
import java.util.List;
import javax.annotation.CheckForNull;

/**
 * A LogixNG female expression socket.
 * A Expression or a Action that has children must not use
 * these directly but instead use a FemaleSocket.
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public interface FemaleSocket extends Base {

    /**
     * Connect the male socket to this female socket.
     * @param socket the socket to connect
     * @throws SocketAlreadyConnectedException if the socket is already connected
     */
    public void connect(MaleSocket socket) throws SocketAlreadyConnectedException;

    /**
     * Disconnect the current connected male socket from this female socket.
     */
    public void disconnect();
    
    /**
     * Get the connected socket.
     * @return the male socket or null if not connected
     */
    public MaleSocket getConnectedSocket();
    
    /**
     * Is a male socket connected to this female socket?
     * @return true if connected
     */
    public boolean isConnected();
    
    /**
     * Is a particular male socket compatible with this female socket?
     * @param socket the male socket
     * @return true if the male socket can be connected to this female socket
     */
    public boolean isCompatible(MaleSocket socket);
    
    /**
     * Set the name of this socket.
     * @param name the name
     */
    public void setName(String name);
    
    /**
     * Get the name of this socket.
     * @return the name
     */
    @CheckForNull
    public String getName();
    
    /**
     * Get an example of a system name for an object that can be connected to
     * this socket.
     */
    public String getExampleSystemName();
    
    /**
     * Create a new system name for a new object that can be connected to this
     * socket.
     *
     * @return a new system name
     */
    public String getNewSystemName();
    
    /**
     * Get a set of classes that are compatible with this female socket.
     * 
     * @return a set of entries with category and class
     */
    public Map<Category, List<Class<? extends Base>>> getConnectableClasses();
    
    /** {@inheritDoc} */
    @Override
    default public void setup() {
        if (isConnected()) {
            getConnectedSocket().setup();
        }
    }

}
