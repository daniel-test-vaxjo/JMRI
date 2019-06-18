package jmri.jmrit.logixng;

import jmri.NamedBean;

/**
 * LogixNG.
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public interface LogixNG extends Base, NamedBean {

    /**
     * Set whenether this LogixNG is enabled or disabled.
     * <P>
     * This method must call registerListeners() / unregisterListeners().
     * 
     * @param enable true if this LogixNG should be enabled, false otherwise
     */
    public void setEnabled(boolean enable);
    
    /**
     * Determines whether this LogixNG is enabled.
     * 
     * @return true if the LogixNG is enabled, false otherwise
     */
    public boolean isEnabled();
    
    /**
     * Get number of ConditionalNGs for this LogixNG.
     *
     * @return the number of conditionals
     */
    public int getNumConditionalNGs();

    /**
     * Move 'row' to 'nextInOrder' and shift all between 'nextInOrder' and 'row'
     * up one position. Requires {@code row > nextInOrder}.
     *
     * @param nextInOrder target order for ConditionalNG at row
     * @param row         position of ConditionalNG to move
     */
    public void swapConditionalNG(int nextInOrder, int row);

    /**
     * Returns the conditional that will calculate in the specified order.
     * This is also the order the ConditionalNG is listed in the
     * Add/Edit LogixNG dialog. If 'order' is greater than the number of
     * ConditionalNGs for this LogixNG, and empty String is returned.
     *
     * @param order order in which the ConditionalNG calculates
     * @return system name of conditional or an empty String
     */
    public ConditionalNG getConditionalNG(int order);

    /**
     * Returns the system name of the conditional that will calculate in the
     * specified order. This is also the order the ConditionalNG is listed in the
     * Add/Edit LogixNG dialog. If 'order' is greater than the number of
     * ConditionalNGs for this LogixNG, and empty String is returned.
     *
     * @param order order in which the ConditionalNG calculates
     * @return system name of conditional or an empty String
     */
    public String getConditionalNGByNumberOrder(int order);

    /**
     * Add a child ConditionalNG to the parent LogixNG.
     *
     * @param conditionalNG The ConditionalNG object.
     * @return true if the ConditionalNG was added, false otherwise.
     */
    public boolean addConditionalNG(ConditionalNG conditionalNG);

    /**
     * Get a ConditionalNG belonging to this LogixNG.
     *
     * @param systemName The name of the ConditionalNG object.
     * @return the ConditionalNG object or null if not found.
     */
    public ConditionalNG getConditionalNG(String systemName);

    /**
     * Get a ConditionalNG belonging to this LogixNG.
     *
     * @param userName The name of the ConditionalNG object.
     * @return the ConditionalNG object or null if not found.
     */
    public ConditionalNG getConditionalNGByUserName(String userName);

    /**
     * Delete a ConditionalNG from this LogixNG.
     * <p>
     * Note: Since each LogixNG must have at least one ConditionalNG, the last
     * ConditionalNG will not be deleted.
     * <p>
     * Returns An array of names used in an error message explaining why
     * ConditionalNG should not be deleted.
     *
     * @param systemName The ConditionalNG system name
     */
    public void deleteConditionalNG(String systemName);

    /**
     * Calculate all ConditionalNGs, triggering action if the user specified
     * conditions are met, and the LogixNG is enabled.
     */
    public void calculateConditionalNGs();

    /**
     * Activate the LogixNG, starts LogixNG processing by connecting all inputs that
     * are included the ConditionalNGs in this LogixNG.
     * <p>
     * A LogixNG must be activated before it will calculate any of its
     * ConditionalNGs.
     */
    public void activateLogixNG();

    /**
     * Deactivate the LogixNG. This method disconnects the LogixNG from all input
     * objects and stops it from being triggered to calculate.
     * <p>
     * A LogixNG must be deactivated before it's ConditionalNGs are changed.
     */
    public void deActivateLogixNG();

}
