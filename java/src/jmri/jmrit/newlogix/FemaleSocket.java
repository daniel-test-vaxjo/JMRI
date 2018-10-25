package jmri.jmrit.newlogix;

/**
 * A NewLogix female expression socket.
 * A NewLogixExpression or a NewLogixAction that has children must not
 use these directly but instead use a FemaleSocket.
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public interface FemaleSocket {

    /**
     * Connect the male socket to this female socket.
     * @param socket the socket to connect
     */
    public void connect(MaleSocket socket);

    /**
     * Disconnect the current connected male socket from this female socket.
     */
    public void disconnect();
    
    /**
     * Is a particular male socket compatible with this female socket?
     * @param socket the male socket
     * @return true if the male socket can be connected to this female socket
     */
    public boolean isCompatible(MaleSocket socket);

}
