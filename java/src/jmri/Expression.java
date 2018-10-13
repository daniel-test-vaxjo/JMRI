package jmri;

/**
 * Expression is used in NewLogix to answer a question that can give the answers
 * 'true' or 'false'.
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public interface Expression extends NamedBean {
    
    public enum TriggerCondition {
        TRUE,
        FALSE,
        CHANGE
    }
    
    /**
     * Get the category of this expression.
     * @return the category
     */
    public NewLogixCategory getCategory();

    /**
     * Is this an external expression?
     * Does this action affects on or is dependent on external things, like
     * turnouts and sensors? Timers are considered as internal since they
     * behavies the same on every computer on every layout.
     * @return true if this expression is external
     */
    public boolean isExternal();

    /**
     * Evaluate this expression.
     * 
     * @return the result of the evaluation
     */
    public boolean evaluate();
    
    /**
     * Reset the evaluation.
     * This method is called when the closest ancestor Action is activated, if
     * this Expression is used by an Action. An example is a timer who is used
     * to delay the execution of an action's child action.
     * 
     * A parent expression needs to call reset() on its child when the parent
     * is reset().
     */
    public void reset();
    
}
