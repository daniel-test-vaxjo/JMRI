package jmri.jmrit.logixng.util;

/**
 * A parsed expression
 */
public interface ExpressionNode<E> {

    public E calculate();
    
}
