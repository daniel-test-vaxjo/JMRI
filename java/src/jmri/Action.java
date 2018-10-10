package jmri;

/**
 * A NewLogix action.
 * 
 * @author Daniel Bergqvist 2018
 */
public interface Action {

    /**
     * Get the category of this action.
     * @return the category
     */
    public NewLogixCategory getCategory();

    /**
     * Start execution of this Action.
     * 
     * @return true if this action is not finished.
     */
    public boolean executeStart();
    
    /**
     * Continue execution of this Action.
     * This method is called if Type == TRIGGER_ACTION, the previous call to
     * one of the execute???() methods returned True and the expression is
     * still True.
     * 
     * @return true if this action is not finished.
     */
    public boolean executeContinue();
    
    /**
     * Restart the execute of this Action.
     * This method is called if Type == TRIGGER_ACTION and the expression has
     * become False and then True again.
     * 
     * If a parent action is restarted, it must restart all its children.
     * 
     * @return true if this action is not finished.
     */
    public boolean executeRestart();
    
    /**
     * Abort this action.
     * This method is called when the NewLogix expression evaluates to False,
     * but the last call to executeStart() returned True.
     */
    public void abort();
    
}
