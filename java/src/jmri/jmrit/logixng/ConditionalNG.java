package jmri.jmrit.logixng;

import static jmri.jmrit.logixng.DigitalAction.log;

import jmri.NamedBean;

/**
 * ConditionalNG.
 * 
 * @author Daniel Bergqvist Copyright 2019
 */
public interface ConditionalNG extends Base, NamedBean {

    /**
     * Get the female socket of this ConditionalNG.
     */
    public FemaleSocket getFemaleSocket();
    
    /**
     * Determines whether this ConditionalNG supports enable execution. It does
     * that if its action supports enable execution. An action for which
     * execution is disabled will evaluate its expressions, if it has that, but
     * not execute any actions.
     * <p>
     * Note that EnableExecution for LogixNG is the equivalent of enable for Logix.
     * 
     * @return true if execution is enbaled for the digital action, false otherwise
     */
    public boolean supportsEnableExecution();
    
    /**
     * Enables or disables execution for the digital action of this ConditionalNG.
     * An action which is disabled execution will evaluate its expressions, if
     * it has that, but not execute any actions.
     * <p>
     * Note that enable execution for LogixNG is the equivalent of enable for Logix.
     * 
     * @param b if true, enables execution, otherwise disables execution
     */
    public void setEnableExecution(boolean b);
    
    /**
     * Determines whether execution is enabled for this digital action. An
     * action for which execution is disabled will evaluate its expressions,
     * if it has that, but not execute any actions.
     * <p>
     * Note that EnableExecution for LogixNG is the equivalent of enable for Logix.
     * 
     * @return true if execution is enbaled for the digital action, false otherwise
     */
    public boolean isExecutionEnabled();
    
    /**
     * Execute the LogixNG.
     * Most of the LogixNG's has a ActionDoIf as its action and it's that
     * action that evaluates the expression and decides if it should execute
     * its action.
     */
    public void execute();

}
