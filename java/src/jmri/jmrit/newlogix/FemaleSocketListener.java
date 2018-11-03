package jmri.jmrit.newlogix;

/**
 * A listener for when a socket is connected or disconnected.
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public interface FemaleSocketListener {

    /**
     * The socket is connected.
     * @param socket the socket
     */
    public void connected(FemaleSocket socket);

    /**
     * The socket is disconnected.
     * @param socket the socket
     */
    public void disconnected(FemaleSocket socket);

}
