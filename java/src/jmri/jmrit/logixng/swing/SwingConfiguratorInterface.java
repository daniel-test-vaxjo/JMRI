package jmri.jmrit.logixng.swing;

import javax.annotation.Nonnull;
import javax.swing.JPanel;
import jmri.NamedBean.BadUserNameException;
import jmri.NamedBean.BadSystemNameException;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.MaleSocket;

/**
 * The parent interface for configuring classes with Swing.
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public interface SwingConfiguratorInterface {

    /**
     * Get a configuration panel when a new object is to be created and we don't
     * have it yet.
     * This method initializes the panel with an empty configuration.
     * 
     * @return a panel that configures this object
     * @throws IllegalArgumentException if this class does not support the class
     * with the name given in parameter 'className'
     */
    public JPanel getConfigPanel() throws IllegalArgumentException;
    
    /**
     * Get a configuration panel for an object.
     * This method initializes the panel with the configuration of the object.
     * 
     * @param object the object for which to return a configuration panel
     * @return a panel that configures this object
     */
    public JPanel getConfigPanel(@Nonnull Base object) throws IllegalArgumentException;
    
    /**
     * Validate the form.
     * The parameter errorMessage is used to give the error message in case of
     * an error. The caller must ensure that errorMessage.length() is zero.
     * 
     * @param errorMessage the error message in case of an error
     * @return true if data in the form is valid, false otherwise
     */
    public boolean validate(@Nonnull StringBuilder errorMessage);
    
    /**
     * Create a new object with the data entered.
     * This method must also register the object in its manager.
     * 
     * @param systemName system name
     * @return a male socket for the new object
     */
    public MaleSocket createNewObject(@Nonnull String systemName)
            throws BadUserNameException, BadSystemNameException;
    
    /**
     * Create a new object with the data entered.
     * This method must also register the object in its manager.
     * 
     * @param systemName system name
     * @param userName user name
     * @return a male socket for the new object
     */
    public MaleSocket createNewObject(@Nonnull String systemName, @Nonnull String userName)
            throws BadUserNameException, BadSystemNameException;
    
    /**
     * Updates the object with the data in the form.
     * 
     * @param object the object to update
     */
    public void updateObject(@Nonnull Base object);
    
    /**
     * Returns the name of the class that this class configures.
     *
     * @return the name of the class this class configures.
     */
    @Override
    public String toString();
    
    /**
     * Dispose the panel and remove all the listeners that this class may have
     * registered.
     */
    public void dispose();
    
}
