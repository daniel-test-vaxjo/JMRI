package jmri;

/**
 * NewLogix.
 * 
 * @author Daniel Bergqvist 2018
 */
public interface NewLogix {

    /**
     * Execute the NewLogix.
     * Most of the NewLogix's has a ActionDoIf as its action and it's that
     * action that evaluates the expression and decides if it should execute
     * its action.
     */
    public void execute();
    
}