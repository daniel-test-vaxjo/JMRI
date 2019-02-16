package jmri.jmrit.logixng;

import java.util.List;
import jmri.Manager;

/**
 * Manager for LogixNG
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public interface LogixNGManager extends Manager<LogixNG> {

    /**
     * Create a new LogixNG if the LogixNG does not exist.
     *
     * @param systemName the system name
     * @param userName   the user name
     * @return a new LogixNG or null if unable to create
     */
    public LogixNG createLogixNG(String systemName, String userName)
            throws IllegalArgumentException;

    /**
     * For use with User GUI, to allow the auto generation of systemNames, where
     * the user can optionally supply a username.
     *
     * @param userName the user name
     * @return a new LogixNG or null if unable to create
     */
    public LogixNG createLogixNG(String userName)
            throws IllegalArgumentException;
    
    /**
     * Creates the initial items in the LogixNG tree.
     * 
     * By default, this is as following:
     * + ActionMany
     *   + ActionHoldAnything
     *   + ActionDoIf
     */
    public void setupInitialLogixNGTree(LogixNG logixNG);

    /**
     * Locate via user name, then system name if needed. Does not create a new
     * one if nothing found
     *
     * @param name User name or system name to match
     * @return null if no match found
     */
    public LogixNG getLogixNG(String name);

    public LogixNG getByUserName(String name);

    public LogixNG getBySystemName(String name);
    
//    public MaleActionSocket createMaleActionSocket(Action action);

//    public MaleExpressionSocket createMaleExpressionSocket(Expression expression);
    
    /**
     * Activate all LogixNGs that are not currently active. This method is
     * called after a configuration file is loaded.
     */
    public void activateAllLogixNGs();

    /**
     * Delete LogixNG by removing it from the manager. The LogixNG must first
     * be deactivated so it stops processing.
     *
     * @param x the LogixNG to delete
     */
    void deleteLogixNG(LogixNG x);

    /**
     * Support for loading LogixNGs in a disabled state
     * 
     * @param s true if LogixNG should be disabled when loaded
     */
    public void setLoadDisabled(boolean s);
    
    /**
     * Register a FemaleSocketFactory.
     */
    public void registerFemaleSocketFactory(FemaleSocketFactory factory);
    
    /**
     * Register a FemaleSocketFactory.
     */
    public List<FemaleSocketFactory> getFemaleSocketFactories();
    
}
