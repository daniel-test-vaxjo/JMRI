package jmri.jmrit.logixng.util.parser;

/**
 * A parsed expression
 */
public interface ExpressionNode {

    public Object calculate() throws CalculateException;
    
    /**
     * Get a String that defines this expression node.
     * @return the string
     */
    public String getDefinitionString();
    
}
