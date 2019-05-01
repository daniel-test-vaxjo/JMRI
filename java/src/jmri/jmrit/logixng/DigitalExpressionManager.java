package jmri.jmrit.logixng;

import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import jmri.Manager;

/**
 * Manager for DigitalExpression
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public interface DigitalExpressionManager extends Manager<MaleDigitalExpressionSocket> {

    /**
     * Remember a NamedBean Object created outside the manager.
     * This method creates a MaleActionSocket for the action.
     *
     * @param expression the bean
     * @return the male socket for this expression
     * @throws IllegalArgumentException if the expression has an invalid system name
     */
    public MaleDigitalExpressionSocket registerExpression(@Nonnull DigitalExpression expression)
            throws IllegalArgumentException;
    
    /**
     * Create a new system name for an DigitalExpression.
     *
     * @param conditionalNG the ConditionalNG that this expression belongs to
     * @return a new system name
     */
    public String getNewSystemName(ConditionalNG conditionalNG);

    public FemaleDigitalExpressionSocket createFemaleSocket(
            Base parent, FemaleSocketListener listener, String socketName);

    public FemaleDigitalExpressionSocket createFemaleExpressionSocket(
            Base parent,
            FemaleSocketListener listener,
            String socketName,
            MaleDigitalExpressionSocket maleSocket);

    /**
     * Get a set of classes that implements the DigitalAction interface.
     * 
     * @return a set of entries with category and class
     */
    public Map<Category, List<Class<? extends Base>>> getExpressionClasses();

    /*.*
     * Add an DigitalExpression.
     *
     * @param expression the expression to add
     * @throws IllegalArgumentException if the expression has an invalid system name
     */
//    public void addExpression(DigitalExpression expression)
//            throws IllegalArgumentException;

    /*.*
     * Locate via user name, then system name if needed. Does not create a new
     * one if nothing found
     *
     * @param name User name or system name to match
     * @return null if no match found
     */
//    public DigitalExpression getExpression(String name);

//    public DigitalExpression getByUserName(String s);

//    public DigitalExpression getBySystemName(String s);

    /**
     * Delete Expression by removing it from the manager. The Expression must
     * first be deactivated so it stops processing.
     *
     * @param x the Expression to delete
     */
//    void deleteExpression(DigitalExpression x);

}
